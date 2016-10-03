/** An Interface to OPENNLP Chunking
 * 
 *  See main() for an example.
 *  
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 *  @author Scott Sanner (ssanner@nicta.com.au)
 */

package nlp.opennlp;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.sentdetect.SentenceDetectorME;

import java.util.ArrayList;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

import util.Pair;

public class Chunker {
	
	SentenceDetectorME _sdetector;
	Tokenizer          _tokenizer;
	POSTaggerME        _tagger;
	ChunkerME          _chunker;

	public Chunker() throws IOException {

		// Load models for Sentence Detector
		System.out.println("Loading models for Sentence Detector...");
		_sdetector = new SharedSentenceDetector(
				"./models/sentdetect/EnglishSD.bin.gz");

		// Load models for Tokenizer
		System.out.println("Loading models for Tokenizer...");
		_tokenizer = new Tokenizer(
				"./models/tokenize/EnglishTok.bin.gz");

		// Load models for POS tagging
		System.out.println("Loading models for POS Tagging...");
		_tagger = new SharedPOSTagger(
				"./models/postag/tag.bin.gz", (Dictionary) null);
		
		// Load models for Chunking
		System.out.println("Loading models for Chunking...");
		_chunker = new ChunkerME( 
				new SuffixSensitiveGISModelReader(
						new File("./models/chunker/EnglishChunk.bin.gz")).getModel());

	}

	// Return value for Chunking
	public static class Chunking {
		public Chunking(String[][] tokens, String[][][] taggings, 
						String[/*sent*/][/*tagging*/][/*label*/] labels) {
			_tokens   = tokens;
			_taggings = taggings;
			
			_chunks      = new ArrayList<ArrayList<ArrayList<String>>>();
			_chunkLabels = new ArrayList<ArrayList<ArrayList<String>>>();
			
			for (int si = 0; si < labels.length; si++) {
				ArrayList<ArrayList<String>> al_chunk = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> al_label = new ArrayList<ArrayList<String>>();
				_chunks.add(al_chunk);
				_chunkLabels.add(al_label);
				for (int ti = 0; ti < taggings[si].length; ti++) {
					Pair p = addChunks(_tokens[si], labels[si][ti]);
					al_chunk.addAll((ArrayList<ArrayList<String>>)p._o1);
					al_label.addAll((ArrayList<ArrayList<String>>)p._o2);
				}
			}
		}
		
		public String[/*sent*/][/*word*/]            _tokens;
		public String[/*sent*/][/*word*/][/*tag*/]   _taggings;
		public ArrayList<ArrayList<ArrayList<String>>> _chunks; // sent, chunk, token
		public ArrayList<ArrayList<ArrayList<String>>> _chunkLabels; // sent, chunk, token
		
		public Pair addChunks(String[] tokens, String[] labels) {
			
			ArrayList<String> al_chunk = new ArrayList<String>();
			ArrayList<ArrayList<String>> al_chunks = new ArrayList<ArrayList<String>>();
			ArrayList<String> al_label = new ArrayList<String>();
			ArrayList<ArrayList<String>> al_labels = new ArrayList<ArrayList<String>>();

			boolean in_chunk = false;
			for (int wi = 0; wi < tokens.length; wi++) {
				if (labels[wi].startsWith("B")) {
					if (in_chunk) {
						// End current chunk and start a new one
						al_chunks.add(al_chunk);
						al_labels.add(al_label);
						al_chunk = new ArrayList<String>();
						al_label = new ArrayList<String>();
					} 
					// Append word to current chunk
					al_chunk.add(tokens[wi]);
					al_label.add(labels[wi]);
					in_chunk = true;
				} else if (labels[wi].startsWith("I")) {
					// Inside a chunk, just append word to current chunk
					al_chunk.add(tokens[wi]);					
					al_label.add(labels[wi]);					
				} else {
					// Don't append this word, if in chunk, this
					// terminates the current chunk
					if (in_chunk) {
						al_chunks.add(al_chunk);
						al_labels.add(al_label);
						al_chunk = new ArrayList<String>();
						al_label = new ArrayList<String>();
					}
					in_chunk = false;
				}
			}
			if (al_chunk.size() > 0) {
				al_chunks.add(al_chunk);
				al_labels.add(al_label);
			}
				
			return new Pair(al_chunks, al_labels);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int si = 0; si < _taggings.length; si++) {
				sb.append("Sentence #" + si + " [" + _tokens[si].length + "]: ");
				for (int ti = 0; ti < _taggings[si].length; ti++) {
					sb.append("\n- Tagging #" + ti + ": ");
					for (int wi = 0; wi < _taggings[si][ti].length; wi++) {
						sb.append(_tokens[si][wi] + "/");
						sb.append(_taggings[si][ti][wi] + " ");
					}
				}
				sb.append("\n- Chunks");
				for (int ci = 0; ci < _chunks.get(si).size(); ci++) {
					sb.append("\n  * " + _chunks.get(si).get(ci) + " / " + 
							_chunkLabels.get(si).get(ci));
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}
	
	public Chunking process(String content, int num_taggings) {
		
		// Extract sentences
		String[] sents = _sdetector.sentDetect(content.toString());
		String[][][] labels = new String[sents.length][][];

		// Extract tokens
		String[][] tokens = new String[sents.length][];
		for (int n = 0; n < sents.length; n++) 
			tokens[n] = _tokenizer.tokenize(sents[n]);
		
		// Perform POS tagging
		String[][][] taggings = new String[sents.length][][];
		for (int sent_index = 0; sent_index < tokens.length; sent_index++) {
			taggings[sent_index] = 
				_tagger.tag(num_taggings, tokens[sent_index]);
		}

		// Produce chunks for each sentence
		for (int si = 0; si < taggings.length; si++) {
			labels[si] = new String[taggings[si].length][];
			for (int ti = 0; ti < taggings[si].length; ti++)
				labels[si][ti] = _chunker.chunk(tokens[si], taggings[si][ti]);
		}

		return new Chunking(tokens, taggings, labels);
	}

	public static void main(String[] args) throws IOException {
		
		Chunker chunker = new Chunker();
		String para1 = "I swam through a river of mud. Colorless dreams swim through steel that shines brightly.";
		String para2 = "Australia is an expansive country with numerous distinctions of kangaroos. Tokyo is a wonderful city with skyscapers as far as the eye can see.";
		System.out.println("----------------------\n");
		Chunking chunks1 = chunker.process(para1, 1);
		System.out.print(chunks1);
		System.out.println("----------------------\n");
		Chunking chunks2 = chunker.process(para2, 1);
		System.out.print(chunks2);
		System.out.println("----------------------\n");
	}
}
