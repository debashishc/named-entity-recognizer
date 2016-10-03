/** An helper class to build the POSTagger
 * 
 *  @author Kimi Sun (yuesun@nicta.com.au)	
 */
package nlp.opennlp;

import java.io.File;
import java.io.IOException;

import opennlp.maxent.MaxentModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.DefaultPOSContextGenerator;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.TagDictionary;

public class SharedPOSTagger extends POSTaggerME {

	  public SharedPOSTagger(String modelFile, Dictionary dict, TagDictionary tagdict) {
	      super(getModel(modelFile), new DefaultPOSContextGenerator(dict),tagdict);
	  }
	  
	  public SharedPOSTagger(String modelFile, TagDictionary tagdict) {
	    super(getModel(modelFile), new DefaultPOSContextGenerator(null),tagdict);
	}

	  public SharedPOSTagger(String modelFile, Dictionary dict) {
	    super(getModel(modelFile), new DefaultPOSContextGenerator(dict));
	  }

	  private static MaxentModel getModel(String name) {
	    try {
	      return new SuffixSensitiveGISModelReader(new File(name)).getModel();
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	      return null;
	    }
	  }
	  
	  public static void usage() {
	    System.err.println("Usage: PosTagger [-d tagdict] [-di case_insensiteve_tagdict] [-k 5] model < tokenized_sentences");
	    System.err.println("-d tagdict Specifies that a tag dictionary file should be used.");
	    System.err.println("-di tagdict Specifies that a case-insensitive tag dictionary should be used.");
	    System.err.println("-k n tagdict Specifies that the top n tagging should be reported.");
	    System.exit(1);    
	  }
	  
}