package nlp.opennlp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * @author Debashish Chakraborty
 *
 * @version 1.0
 */
public class NERapp {

    static HashMap<String, Integer> loc = new HashMap<String, Integer>();
    static HashMap<String, Integer> org = new HashMap<String, Integer>();
    static HashMap<String, Integer> per = new HashMap<String, Integer>();
    static HashMap<String, Integer> misc = new HashMap<String, Integer>();


    // Extract word and corresponding tag and assign to required dictionary
    public static void editResultText(String string) {

        Scanner scanner = new Scanner(string);
        String word = "";
        String tag = "";

        while (scanner.hasNextLine()) {
            String[] strings = scanner.nextLine().split("\t"); // split  on tab
            word = strings[0]; // read the word
            tag = strings[3]; // read the tag
        }

        scanner.close();


        if (tag.toUpperCase().endsWith("ORG"))
            hash(word, org);
        else if (tag.toUpperCase().endsWith("LOC"))
            hash(word, loc);
        else if (tag.toUpperCase().endsWith("PER"))
            hash(word, per);
        else if (tag.toUpperCase().endsWith("MISC"))
            hash(word, misc);

    }

    // initiate the count if the word is not there, otherwise increase the count by one
    public static void hash(String word, HashMap<String, Integer> hashMap) {
        if (hashMap.containsKey(word)) {
            hashMap.put(word, hashMap.get(word) + 1);
        } else {
            hashMap.put(word, 1);
        }

    }

    private static void display(String s, HashMap<String, Integer> hm, BufferedWriter bw) throws IOException {
        bw.write(s + "\n");
        Map<String, Integer> treeMap = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            bw.write("\t" + key + "\t" + value + "\n");
        }
        bw.write("\n");
    }


    public static void read_print(String inputPath) {
        File file = new File(inputPath);
        String body = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                editResultText(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException ie) {
            System.out.println("The specified file cannot be found.");
        }

    }


    public static void main(String[] args) throws IOException {

        String inputPath = "./predicted2.txt";
        // todo: change it to ".test" file before testing later
        String outputPath = "./NER_result.txt";

        File outf = new File(outputPath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outf));
//        TestLabeler testLabeler = new TestLabeler();
//        try {
//            testLabeler.readFile("");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        String body = TestLabeler.readFile(inputPath);

//        editResultText("La\tDA\tB-LOC\tB-LOC\n" );

        read_print(inputPath);

        display("Location", loc, bw);
        display("Organization", org, bw);
        display("Person", per, bw);
        display("Misc", misc, bw);

        bw.close();




    }


}
