package nlp.opennlp;

import java.io.IOException;

/**
 * @author Debashish Chakraborty
 */
public class NERapp {




    public static void main(String[] args) {

        TestLabeler testLabeler = new TestLabeler();
        try {
            testLabeler.readFile("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
