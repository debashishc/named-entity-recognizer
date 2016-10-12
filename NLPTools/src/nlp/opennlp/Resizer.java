package nlp.opennlp;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 */
public class Resizer {

    public static void main(String[] args) throws IOException {

        String inputPath = "./conll2002/esp.train";
//        String inputPath = "./test_files_final/after_test.txt";
        String outputPath1 = "./conll2002/esp.train_1";
        String outputPath2 = "./conll2002/esp.train_2";
        String outputPath3 = "./conll2002/esp.train_3";


        File file = new File(inputPath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        File outfile1 = new File(outputPath1);
        BufferedWriter bw1 = new BufferedWriter(new FileWriter(outfile1));

        File outfile2 = new File(outputPath2);
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(outfile2));

        File outfile3 = new File(outputPath3);
        BufferedWriter bw3 = new BufferedWriter(new FileWriter(outfile3));

        ArrayList<String> stringArrayList = new ArrayList<>();

        String line;
        String body = "";
        while((line = br.readLine()) != null) {
            String[] columns = line.split("\t");
            body += columns[0]+" ";

        }

//        System.out.println(body);



        SentenceExtractor extractor = new SentenceExtractor();
        String[] sentences1 = extractor.process(body);

        System.out.println(sentences1.length);

    }

}
