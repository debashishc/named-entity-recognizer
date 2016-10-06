package nlp.opennlp;

import java.io.*;

/**
 * @author Debashish Chakraborty
 */
public class TestLabeler {


    /**
     * Reads the content of the file and returns a string object containing the body of the text
     *
     * @param inputPath input file path
     * @return the text of the file
     * @throws IOException
     */
    public static String readFile(String inputPath) throws IOException {

        File file = new File(inputPath);
        String body = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null){
                body += line;
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException ie){
            System.out.println("The specified file cannot be found.");
        }

        return body;
    }

    /**
     * Perform POSTagging on text and label each token with a POS
     *
     * @param text a string object containing text (of a file)
     * @return a part of speech tagging object containing the tokens and respective taggings
     * @throws IOException
     */
    public static POSTagger.POSTagging tagText(String text) throws IOException {

        POSTagger posTagger = new POSTagger();
        POSTagger.POSTagging posTagging = posTagger.process(text, 1);

        return posTagging;
    }

    public static void writeFile(POSTagger.POSTagging posTagging, String outputPath) throws IOException {

        File outputFile = new File(outputPath);

        String[/*sent*/][/*word*/] _tokens = posTagging._tokens;
        String[/*sent*/][/*tag*/][/*word*/] _taggings = posTagging._taggings;

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile, false));

            for (int si = 0; si < _taggings.length; si++) {
                for (int ti = 0; ti < _taggings[si].length; ti++) {
                    for (int wi = 0; wi < _taggings[si][ti].length; wi++) {
                        bufferedWriter.write(_tokens[si][wi] + "\t\t");
                        bufferedWriter.write(_taggings[si][ti][wi] + "\t\t" + "O" + "\n");
                    }
                }
                bufferedWriter.write("\n");
            }

        bufferedWriter.close();
    }


    public static void main(String[] args) throws IOException {

        String inputPath = "./test.txt";
        // todo: change it to ".test" file before testing later
        String outputPath = "./labeled_test.txt";

//        System.out.println(new File(".").getCanonicalPath());

        String body = readFile(inputPath);
        POSTagger.POSTagging posTagging = tagText(body);
        writeFile(posTagging, outputPath);


    }

}
