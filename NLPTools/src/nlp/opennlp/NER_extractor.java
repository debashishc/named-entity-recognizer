package nlp.opennlp;

import java.io.*;
import java.util.*;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 *
 * Displays all unique recognized named entities (one entity per line) and their frequencies after
 * 1) organising them alphabetically first with respect to the NER tag and then with respect to the extracted text,
 * 2) counting the frequency of each unique NER-text line, and
 * 3) removing duplicates
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



    public static void printWordsToFile(ArrayList<TemplateRow> rowArrayList, String outputPath) throws FileNotFoundException {

        ArrayList<String> perList = new ArrayList<>();
        ArrayList<String> locList = new ArrayList<>();
        ArrayList<String> orgList = new ArrayList<>();
        ArrayList<String> miscList = new ArrayList<>();

        Map<String, ArrayList<String>> featureMap = new TreeMap<>();
        featureMap.put("LOC", locList);
        featureMap.put("MISC",miscList);
        featureMap.put("ORG",orgList);
        featureMap.put("PER",perList);

        for (int i = 0; i < rowArrayList.size(); i++) {
            for (String thisChunk: featureMap.keySet())  {
                TemplateRow rootTemplateRow = rowArrayList.get(i);
                String rootWord = rootTemplateRow.getWord();
                String rootChunk = rootTemplateRow.getChunk();

                // Clustering and joining words based on chunks
                // e.g. check if rootChunk is "B-PER" and then "I-PER"
                if (rootChunk.equals("B-" + thisChunk)) {
                    int ci = i;
                    String words = rootWord;

                    while (true) {
                        ci++;
                        TemplateRow row = rowArrayList.get(ci);
                        String chunk = row.getChunk();
                        if (chunk.equals("I-" + thisChunk)) {
                            String word = row.getWord();
                            words = words + " " + word;
                        } else
                            break;
                    }
                    featureMap.get(thisChunk).add(words);
                }
            }
        }

        // Printing out sorted dictionaries
        PrintWriter printWriter = new PrintWriter(outputPath);

        printWriter.println("Location:");
        printSortedMap(locList, printWriter);
        printWriter.println("\n");

        printWriter.println("Miscellaneous:");
        printSortedMap(miscList, printWriter);
        printWriter.println("\n");

        printWriter.println("Organisation:");
        printSortedMap(orgList, printWriter);
        printWriter.println("\n");

        printWriter.println("Person:");
        printSortedMap(perList, printWriter);

        printWriter.close();

    }

    public static void main(String[] args) throws IOException {

        String outputPath = "./test_files_final/final-edited.txt";
        ArrayList<TemplateRow> loadedTemplateRowList = load_data("./test_files_final/after_test.txt");
        printWordsToFile(loadedTemplateRowList, outputPath);
    }
}