package nlp.opennlp;

import java.io.*;
import java.util.*;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 */
public class NER_extractor {


    public static ArrayList<TemplateRow> load_data(String inputPath) {
        File file = new File(inputPath);
        ArrayList<TemplateRow> rowArrayList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                Scanner scanner = new Scanner(line);

                while (scanner.hasNextLine()) {
                    String[] strings = scanner.nextLine().split("\t"); // split  on tab
                    String word = strings[0]; // read the word
                    String tag = strings[3]; // read the tag
                    String predicted_chunk = strings[3]; // read the predicted chunk

                    TemplateRow row = new TemplateRow(word, tag);
                    row.setChunk(predicted_chunk);

                    rowArrayList.add(row);
                }

                scanner.close();
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException ie) {
            System.out.println("The specified file cannot be found.");
        }
//        System.out.println(rowArrayList);
        return rowArrayList;

    }


    //
    private static void printSortedMap(ArrayList<String> list, PrintWriter printWriter) {

        Map<String, Integer> map = new HashMap<>();

        for (String string : list) {
            Integer count = map.get(string);
            map.put(string, (count == null) ? 1 : count + 1);
        }

        Map<String, Integer> treeMap = new TreeMap<>(map);
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            printWriter.println("\t" + entry.getKey() + " : " + entry.getValue());
        }

    }

//    public static ArrayList<String> extractWordlistOnFeature(String rootChunk, String rootWord, String chunk, int index){
//        ArrayList<String> list = new ArrayList<>();
//
//        if (rootChunk.equals("B-"+chunk)) {
//            int ci = index;
//            String words = rootWord;
//
//            while (true) {
//                ci++;
//                TemplateRow row = rowArrayList.get(ci);
//                String word_chunk = row.getChunk();
//                if (word_chunk.equals("I-"+chunk)) {
//                    String word = row.getWord();
//                    words = words + " " + word;
//                } else
//                    break;
//            }
//            list.add(words);
//        }
//
//
//        return list;
//    }


    public static void printWordsToFile(ArrayList<TemplateRow> rowArrayList, String outputPath) throws FileNotFoundException {

        ArrayList<String> perList = new ArrayList<>();
        ArrayList<String> locList = new ArrayList<>();
        ArrayList<String> orgList = new ArrayList<>();
        ArrayList<String> miscList = new ArrayList<>();

        Map<String, ArrayList<String>> map = new HashMap<>();
        map.put("LOC", locList);
        map.put("MISC",miscList);
        map.put("ORG",orgList);
        map.put("PER",perList);

        List<String> features = Arrays.asList("LOC", "MISC", "ORG", "PER");

        for (int i = 0; i < rowArrayList.size(); i++) {
            for (int j=0; j<features.size(); j++) {
                String thisChunk = features.get(j);
                TemplateRow rootTemplateRow = rowArrayList.get(i);
                String rootWord = rootTemplateRow.getWord();
                String rootChunk = rootTemplateRow.getChunk();

                // Clustering and joining words based on chunks
                if (rootChunk.equals("B-"+thisChunk)) {
                    int ci = i;
                    String words = rootWord;

                    while (true) {
                        ci++;
                        TemplateRow row = rowArrayList.get(ci);
                        String chunk = row.getChunk();
                        if (chunk.equals("I-"+thisChunk)) {
                            String word = row.getWord();
                            words = words + " " + word;
                        } else
                            break;
                    }
                    map.get(thisChunk).add(words);
                }
            }
        }

//

        // Printing out sorted dictionaries
        PrintWriter pw = new PrintWriter(outputPath);

        pw.println("Location:");
        printSortedMap(locList, pw);
        pw.println("\n");

        pw.println("Miscellaneous:");
        printSortedMap(miscList, pw);
        pw.println("\n");

        pw.println("Organisation:");
        printSortedMap(orgList, pw);
        pw.println("\n");

        pw.println("Person:");
        printSortedMap(perList, pw);
        pw.close();

    }

    public static void main(String[] args) throws IOException {

        String outputPath = "./test_files_final/final-edited1.txt";
        ArrayList<TemplateRow> loadedTemplateRowList = load_data("./test_files_final/after_test.txt");
        printWordsToFile(loadedTemplateRowList, outputPath);


    }
}