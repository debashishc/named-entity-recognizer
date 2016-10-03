/** An Interface to OPENNLP Sentence Tokenization
 * 
 *  See main() for an example.
 *  
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 *  @author Scott Sanner (ssanner@nicta.com.au)
 */

package nlp.opennlp;

import java.io.IOException;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.sentdetect.SentenceDetectorME;

public class SentenceTokenizer {
	
	SentenceDetectorME _sdetector;
	Tokenizer          _tokenizer;

	// Constructor
	public SentenceTokenizer() throws IOException {

		// Load models for Sentence Detector
		System.out.println("Loading models for Sentence Detector...");
		_sdetector = new SharedSentenceDetector(
				"./models/sentdetect/EnglishSD.bin.gz");

		// Load models for Tokenizer
		System.out.println("Loading models for Tokenizer...");
		_tokenizer = new Tokenizer(
				"./models/tokenize/EnglishTok.bin.gz");
	}

	// Return String representation of tokens
	public String getSentenceString(String[][] tokens) {
		StringBuilder sb = new StringBuilder();
		for (int si = 0; si < tokens.length; si++) {
			sb.append("Sentence #" + si + " [" + tokens[si].length + "]: ");
			for (int wi = 0; wi < tokens[si].length; wi++) {
				sb.append("[" + tokens[si][wi] + "] ");
			}
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	// Main method to extract tokens
	public String[][] process(String para) {

		// Extract sentences
		String[] sents = _sdetector.sentDetect(para.toString());

		// Extract tokens
		String[][] tokens = new String[sents.length][];
		for (int n = 0; n < sents.length; n++) 
			tokens[n] = _tokenizer.tokenize(sents[n]);

		return tokens;
	}	
	
	//////////////////////////////////////////////////////////////////
	//                              Tests
	//////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws IOException {
		
		SentenceTokenizer tokenizer = new SentenceTokenizer();
		String para1 = "Jack isn't a girl. Colorless dreams swim through steel.  Mr. Coffee is a great coffee maker for me.";
		String para2 = "Australia is a well-known obtaining country. Tokyo is a city.";
		System.out.println("----------------------\n");
		String[][] tokens1 =tokenizer.process(para1);
		System.out.print(tokenizer.getSentenceString(tokens1));
		System.out.println("----------------------\n");
		String[][] tokens2 = tokenizer.process(para2);
		System.out.print(tokenizer.getSentenceString(tokens2));
		System.out.println("----------------------\n");
	}
}
