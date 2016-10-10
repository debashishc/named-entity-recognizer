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
        ArrayList<TemplateRow> elementList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                Scanner scanner = new Scanner(line);

                while (scanner.hasNextLine()) {
                    String[] strings = scanner.nextLine().split("\t"); // split  on tab
                    String word = strings[0]; // read the word
                    String tag = strings[3]; // read the tag

                    String predicted_chunk = strings[3];

                    TemplateRow row = new TemplateRow(word, tag);
                    row.setChunk(predicted_chunk);

                    elementList.add(row);
                }

                scanner.close();
                line = bufferedReader.readLine();
            }

            bufferedReader.close();

        } catch (IOException ie) {
            System.out.println("The specified file cannot be found.");
        }

        return elementList;

    }


    private static void printTreeMap(Map<String, Integer> map, PrintWriter pw) {

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            pw.println("\t" + entry.getKey() + " : " + entry.getValue());
        }

    }

    public static void clusterTemplateRows(ArrayList<TemplateRow> rowArrayListList, String outputPath) throws FileNotFoundException {
        ArrayList<String> perList = new ArrayList<>();
        ArrayList<String> locList = new ArrayList<>();
        ArrayList<String> orgList = new ArrayList<>();
        ArrayList<String> miscList = new ArrayList<>();


        for (int i = 0; i < rowArrayListList.size(); i++) {
            TemplateRow rootTemplateRow = rowArrayListList.get(i);
            String rootWord = rootTemplateRow.getWord();
            String rootChunk = rootTemplateRow.getChunk();

            // CLustering and joining words based on chunks
            if (rootChunk.equals("B-LOC")) {
                int ci = i;
                String wordarr = rootWord;

                while (true) {
                    ci++;
                    TemplateRow element = rowArrayListList.get(ci);
                    String chunk = element.getChunk();
                    if (chunk.equals("I-LOC")) {
                        String word = element.getWord();
                        wordarr = wordarr + " " + word;
                    } else
                        break;
                }
                locList.add(wordarr);
            }

            if (rootChunk.equals("B-PER")) {
                int ci = i;
                String wordarr = rootWord;

                while (true) {
                    ci++;
                    TemplateRow element = rowArrayListList.get(ci);
                    String chunk = element.getChunk();
                    if (chunk.equals("I-PER")) {
                        String word = element.getWord();
                        wordarr = wordarr + " " + word;
                    } else
                        break;
                }
                perList.add(wordarr);
            }

            if (rootChunk.equals("B-ORG")) {
                int ci = i;
                String wordarr = rootWord;

                while (true) {
                    ci++;
                    TemplateRow element = rowArrayListList.get(ci);
                    String chunk = element.getChunk();
                    if (chunk.equals("I-ORG")) {
                        String word = element.getWord();
                        wordarr = wordarr + " " + word;
                    } else
                        break;
                }
                orgList.add(wordarr);
            }

            if (rootChunk.equals("B-MISC")) {
                int ci = i;
                String wordarr = rootWord;

                while (true) {
                    ci++;
                    TemplateRow element = rowArrayListList.get(ci);
                    String chunk = element.getChunk();
                    if (chunk.equals("I-MISC")) {
                        String word = element.getWord();
                        wordarr = wordarr + " " + word;
                    } else
                        break;
                }
                miscList.add(wordarr);
            }
        }

        // Hash word and frequency of word
        // Hashing Organisation
        Map<String, Integer> orgMap = new HashMap<>();

        for (String temp : orgList) {
            Integer count = orgMap.get(temp);
            orgMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Hashing Location
        Map<String, Integer> locMap = new HashMap<>();

        for (String temp : locList) {
            Integer count = locMap.get(temp);
            locMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Hashing Person
        Map<String, Integer> perMap = new HashMap<>();

        for (String temp : perList) {
            Integer count = perMap.get(temp);
            perMap.put(temp, (count == null) ? 1 : count + 1);
        }

        Map<String, Integer> miscMap = new HashMap<>();

        for (String temp : miscList) {
            Integer count = miscMap.get(temp);
            miscMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Sort dictionaries by keys, printing out sorted dictionaries

        PrintWriter pw = new PrintWriter(outputPath);

        pw.println("Location:");
        Map<String, Integer> locTreemap = new TreeMap<>(locMap);
        printTreeMap(locTreemap, pw);
        pw.println("\n");

        pw.println("Miscellaneous:");
        Map<String, Integer> miscTreemap = new TreeMap<>(miscMap);
        printTreeMap(miscTreemap, pw);
        pw.println("\n");


        pw.println("Organisation:");
        Map<String, Integer> orgTreemap = new TreeMap<>(orgMap);
        printTreeMap(orgTreemap, pw);
        pw.println("\n");


        pw.println("Person:");
        Map<String, Integer> perTreemap = new TreeMap<>(perMap);
        printTreeMap(perTreemap, pw);
        pw.close();

    }

    public static void main(String[] args) throws IOException {

        String outputPath = "./test_files_final/final1.txt";

        ///////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<TemplateRow> loadedTemplateRowList = load_data("./test_files_final/after_test1.txt");
        clusterTemplateRows(loadedTemplateRowList, outputPath);


    }
}
