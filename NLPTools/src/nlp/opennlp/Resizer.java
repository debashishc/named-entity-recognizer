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

        // read from a file
        String s = br.readLine();
        while (s != null) {
            stringArrayList.add(s);
            s = br.readLine();
        }
        br.close();

        int size1 = stringArrayList.size() * 1 / 4;
        int size2 = stringArrayList.size() * 2 / 4;
        int size3 = stringArrayList.size() * 3 / 4;

        System.out.println(stringArrayList.size());
        System.out.println("size1: " +size1);
        System.out.println("size2: "+size2);
        System.out.println("size3: "+size3);

        // Pick the first "size" number of lines from the training data
        for (int i = 0; i < size1; i++) {
            bw1.write(stringArrayList.get(i) + "\n");
        }

        bw1.close();

        for (int i = 0; i < size2; i++) {
            bw2.write(stringArrayList.get(i) + "\n");
        }

        bw2.close();

        for (int i = 0; i < size3; i++) {
            bw3.write(stringArrayList.get(i) + "\n");
        }

        bw3.close();
    }


}
