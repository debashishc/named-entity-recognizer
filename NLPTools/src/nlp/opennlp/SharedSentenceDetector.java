/** An helper class to build the Sentence Extractor
 * 
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 */
package nlp.opennlp;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.sentdetect.SentenceDetectorME;

class SharedSentenceDetector extends SentenceDetectorME {
	/**
	 * Loads a new sentence detector using the model specified by the model
	 * name.
	 * 
	 * @param modelName
	 *            The name of the maxent model trained for sentence detection.
	 * @throws IOException
	 *             If the model specified can not be read.
	 */
	public SharedSentenceDetector(String modelName) throws IOException {
		super((new SuffixSensitiveGISModelReader(new File(modelName)))
				.getModel());
		this.useTokenEnd = true;
	}
}
