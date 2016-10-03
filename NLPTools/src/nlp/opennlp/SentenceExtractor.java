/** An Interface to OPENNLP Sentence Extraction
 * 
 *  See main() for an example.
 *  
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 *  @author Scott Sanner (ssanner@nicta.com.au)
 */

package nlp.opennlp;

import java.io.IOException;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.InvalidFormatException;

public class SentenceExtractor {
	
	SentenceDetectorME _sdetector;

	// Constructor
	public SentenceExtractor() throws IOException {

		// Load models for Sentence Detector
		System.out.println("Loading models for Sentence Detector...");
		_sdetector = new SharedSentenceDetector(
				"./models/sentdetect/EnglishSD.bin.gz");
	}

	// Return String representation of sentences
	public String getSentenceString(String[] sentences) {
		StringBuilder sb = new StringBuilder();
		for (int si = 0; si < sentences.length; si++)
			sb.append("Sentence #" + si + ": " + sentences[si] + "\n");
		return sb.toString();
	}
	
	// Main method to extract sentences
	public String[] process(String para) {

		// Extract sentences
		return _sdetector.sentDetect(para.toString());
	}	
	
	//////////////////////////////////////////////////////////////////
	//                              Tests
	//////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws IOException {
		
		SentenceExtractor extractor = new SentenceExtractor();
		String para1 = "Mr. Fleming met Mrs. Fleming. Colorless dreams swim through steel.";
		String para2 = "Australia is a country. Tokyo is a city; it has many buildings.";
		System.out.println("----------------------\n");
		String[] sentences1 = extractor.process(para1);
		System.out.println(extractor.getSentenceString(sentences1));
		System.out.println("----------------------\n");
		String[] sentences2 = extractor.process(para2);
		System.out.println(extractor.getSentenceString(sentences2));
		System.out.println("----------------------\n");
	}
}
