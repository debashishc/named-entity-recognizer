/** An Interface to OPENNLP Parsing
 * 
 *  See main() for an example.
 *  
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 *  @author Scott Sanner (ssanner@nicta.com.au)
 */

package nlp.opennlp;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.mention.Mention;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.lang.english.TreebankParser;
import opennlp.tools.parser.*;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;

public class Parser {
	
	public static final DecimalFormat _df = new DecimalFormat("#.###");
	
	SentenceDetectorME     _sdetector;
	Tokenizer              _tokenizer;
	AbstractBottomUpParser _parser;
	
	/** Constructor
	 */
	public Parser() throws IOException{

		// Load models for Sentence Detector
		System.out.println("Loading model for Sentence Detector...");
		_sdetector = new SharedSentenceDetector(
				"./models/sentdetect/EnglishSD.bin.gz");

		// Load models for Tokenizer
		System.out.println("Loading model for Tokenizer...");
		_tokenizer = new Tokenizer(
				"./models/tokenize/EnglishTok.bin.gz");
		
		// Load the Parser
		System.out.println("Loading model for Parser...");
		_parser = (AbstractBottomUpParser) TreebankParser.getParser(
				"./models/parser", /*useTagDict*/false, 
				/*useCaseInsensitiveTagDict*/false, 
				/*beamSize*/AbstractBottomUpParser.defaultBeamSize, 
				/*advancePercentage*/AbstractBottomUpParser.defaultAdvancePercentage);
		
		System.out.println("Done initializing.");
	}
	
	/**  Return value for Parsing
	 */
	public static class ParseResult {
		public ParseResult(String[] sentences, Parse[][] parses) {
			_sentences  = sentences;
			_parses     = parses;
			_annotation = null;
			_entities   = null;
		}
		public ParseResult(String[] sentences, Parse[][] parses,
						   Map<Parse,?> annotation, DiscourseEntity[] entities) {
			_sentences  = sentences;
			_parses     = parses;
			_annotation = annotation;
			_entities   = entities;
		}
		public String[/*sent*/]             _sentences;
		public Parse[/*sent*/][/*parse #*/] _parses;
		public Map<Parse,?>                 _annotation;
		public DiscourseEntity[]            _entities;
		
		// Display sentences and their parses
		public String toString() {			
			StringBuilder sb = new StringBuilder();
			for(int si = 0; si < _sentences.length; si++) {
				sb.append("\nSentence #" + si + ": \"" + _sentences[si] + "\"\n");
				for(int pi = 0; pi < _parses[si].length; pi++) 
					sb.append("----------------\n- Parse #" + pi + ":\n" + 
							  getParseString(_parses[si][pi], _annotation) + "\n");
			}
			if (_entities != null) {
				sb.append("\nDiscourse Mentions\n");
				for (DiscourseEntity d : _entities)
					sb.append("- " + d.toString() + "\n");
			}
			sb.append("----------------");
			return sb.toString();
		}
	}
	
	/** Parse multiple sentences from text
	 * 
	 * @param para 
	 * @param num_parses
	 * @param all_one_parse True if all parses should be under one parent
	 *                      node; caveat if true is that cannot choose
	 *                      best parse on a sentence-by-sentence basis
	 * @return a ParseResult: for each sentence in para, an array 
	 *         of num_parses possible parse trees
	 * @throws IOException
	 */
	public ParseResult process(String para, int num_parses, boolean all_one_parse) 
		throws IOException {
		
		// Extract sentences and parse each
		String[] sents = _sdetector.sentDetect(para.toString());
		
		// If all one parse, compress sentences into "\n" separated sentences
		if (all_one_parse) {
			StringBuilder one_sent = new StringBuilder();
			for (String s : sents)
				one_sent.append(s + "\n");
			sents = new String[] { one_sent.toString() };
		}
		Parse[][] parses = new Parse[sents.length][];
		
		// Get parses for each sentence
		for (int si = 0; si < sents.length; si++)
			parses[si] = processSentence(sents[si], num_parses);
		
		return new ParseResult(sents, parses);
	}
	
	/** Parse an individual sentence
	 * 
	 * @param sent
	 * @param num_parses
	 * @return an array of num_parses possible parse trees for sent
	 * @throws IOException
	 */
	public Parse[/*parse*/] processSentence(String sent, int num_parses) 
		throws IOException {

		// Tokenize
		String[] tokens = _tokenizer.tokenize(sent);

		// Note: the following seems a horribly inefficient way to setup
		//       a parse but seems to be the way it is done for OpenNLP
		//       based on example code from others

		// Build a string to parse as well as a list of tokens
		StringBuffer sb = new StringBuffer();
		List<String> tokenList = new ArrayList<String>();
		for (int j = 0; j < tokens.length; j++) {
			tokenList.add(tokens[j]);
			sb.append(tokens[j]).append(" ");
		}
		String text = sb.substring(0, sb.length() - 1).toString();
		Parse p = new Parse(text, new Span(0, text.length()), "INC", 1,
				null);
		
		// Create a parse object for each token and add it to the parent
		int start = 0;
		for (Iterator ti = tokenList.iterator(); ti.hasNext();) {
			String tok = (String) ti.next();
			p.insert(new Parse(text, new Span(start, start + tok.length()),
					AbstractBottomUpParser.TOK_NODE, 0, 0));
			start += tok.length() + 1;
		}

		// Fetch multiple possible parse trees
		return _parser.parse(p, num_parses);
	}
	
	/** Get the String representation of a parse tree
	 * 
	 * @param p
	 */
	public static String getParseString(Parse p) {
		return getParseString(p, 0, null);
	}
		
	/** Get the String representation of a parse tree with
	 *  annotations in annotation
	 * 
	 * @param p
	 */
	public static String getParseString(Parse p, Map<Parse,?> annotation) {
		return getParseString(p, 0, annotation);
	}

	/** Helper method, use getParseString
	 * 
	 * @param p
	 * @param level
	 * @param annotation A map with optional annotations
	 * @return
	 */
	public static String getParseString(Parse p, int level, 
			Map<Parse,?> annotation) {

		StringBuilder sb = new StringBuilder();

		// Recursively process all children of this node
		// Note: can also call getParent() to get a node's parent
		Parse[] kids = p.getChildren();
		for (int ki = 0; ki < kids.length; ki++) {

			// Indent by the appropriate amount
			sb.append(indent(level));

			// Display node label and probability once 
			if (ki == 0) {	
				// Display indent and node type (for first iteration only)
				sb.append(((ki == 0) ? kids[ki].getParent().getType() : ""));

				// Display annotation if available
				Object note = null;
				if (annotation != null && (note = annotation.get(p)) != null)
					sb.append("#" + note.toString());
				
				// Display PCFG probability
				// sb.append(" (Pr=" + _df.format(kids[ki].getProb()) + ")");
			}
			
			// Display word token (if terminal) or sub-tree (otherwise) 
			if (kids[ki].getChildCount() == 0)
				sb.append(" " + kids[ki].toString());
			else {
				// Display sub-tree
				sb.append("\n" + getParseString(kids[ki], level + 1, annotation));
			}
		}
		
		return sb.toString();
	}
	
	/** Helper method for indenting
	 * 
	 * @param level
	 * @return
	 */
	public static String indent(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++)
			sb.append("   ");
		return sb.toString();
	}
	
	//////////////////////////////////////////////////////////////////
	//                              Tests
	//////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws IOException {

			String para1 = 
				"A rare black squirrel has become a regular visitor to a suburban garden. " +
				"The company is the second-largest global supplier of microprocessors.";
            String para2 =
            	"The phalanx of reporters waited patiently for the arrival of Mr. Sushi, the eminent chef-star. " +
            	"Mr. Sushi is world-renowned for his spicy crunchy tuna roll.";

            Parser parser = new Parser();
            
            System.out.println("\n================\nParsing paragraph: " + para1);
 			ParseResult pr1 = parser.process(para1, 1, true);
 			System.out.println(pr1);
 			
            System.out.println("\n================\nParsing paragraph: " + para2);
 			ParseResult pr2 = parser.process(para2, 2, false);
 			System.out.println(pr2);
	}
	
}
