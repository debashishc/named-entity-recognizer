package nlp.opennlp;

import java.io.*;

/**
 * @author Debashish Chakraborty
 */
public class TestLabeler {

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
        }

        return body;
    }

    public static POSTagger.POSTagging tagText(String text) throws IOException {

        POSTagger posTagger = new POSTagger();
        POSTagger.POSTagging posTagging = posTagger.process(text, 3);

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
                        bufferedWriter.write(_tokens[si][wi] + "\t");
                        bufferedWriter.write(_taggings[si][ti][wi] + "\t" + "O" + "\n");
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
