package nlp.opennlp;

import java.io.*;
import java.util.*;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 */
public class NER_extractor {


    private static ArrayList<TemplateRow> load_data(String inputPath) {
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

    private static ArrayList<TemplateRow> createRowList(Chunker.Chunking chunkedData) {

        ArrayList<TemplateRow> rowArrayList = new ArrayList<>();

        String[][] tokens = chunkedData._tokens;
        String[][][] taggings = chunkedData._taggings;
        ArrayList<ArrayList<ArrayList<String>>> chunkLabels = chunkedData._chunkLabels;

        // Adding word and its corresponding tag tag to each row
        for (int si = 0; si < tokens.length; si++) {
            String[] sentence = tokens[si];
            for (int wi = 0; wi < sentence.length; wi++) {
                String word = sentence[wi];
                if (!word.equals(".")) {
                    String tag = taggings[si][0][wi];
                    TemplateRow row = new TemplateRow(word, tag);
                    rowArrayList.add(row);
                }

            }
        }

        // Adding chunkable to row
        int elementIndex = 0;
        for (ArrayList<ArrayList<String>> chunkSentence : chunkLabels) {
            for (ArrayList<String> chunkWordGroup : chunkSentence) {
                for (String chunk : chunkWordGroup) {
                    TemplateRow element = rowArrayList.get(elementIndex);
                    element.setChunk(chunk);
                    elementIndex++;
                }
            }
        }

        return rowArrayList;

    }

    private static void writeToFile(ArrayList<TemplateRow> elementList, String filepath) throws IOException {
        PrintWriter pw = new PrintWriter(filepath);
        for (TemplateRow element : elementList) {
            pw.println(element.word + "\t" + element.tag + "\t" + element.chunk);
        }
        pw.close();

    }

    private static void printTreeMap(Map<String, Integer> map, PrintWriter pw) {

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            pw.println("\t" + entry.getKey() + " : " + entry.getValue());
        }

    }

    private static void clusterTemplateRows(ArrayList<TemplateRow> rowArrayListList) throws FileNotFoundException {
        ArrayList<String> perList = new ArrayList<>();
        ArrayList<String> locList = new ArrayList<>();
        ArrayList<String> orgList = new ArrayList<>();

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
        }

        // Hash word and frequency of word
        // Hashing Organisation
        Map<String, Integer> orgMap = new HashMap<>();

        for (String temp : orgList) {
            Integer count = orgMap.get(temp);
            orgMap.put(temp, (count == 0) ? 1 : count + 1);
        }

        // Hashing Location
        Map<String, Integer> locMap = new HashMap<>();

        for (String temp : locList) {
            Integer count = locMap.get(temp);
            locMap.put(temp, (count == 0) ? 1 : count + 1);
        }

        // Hashing Person
        Map<String, Integer> perMap = new HashMap<>();

        for (String temp : perList) {
            Integer count = perMap.get(temp);
            perMap.put(temp, (count == 0) ? 1 : count + 1);
        }

        // Sort dictionaries by keys, printing out sorted dictionaries

        String outputPath = "./test_files/final.txt";
        PrintWriter pw = new PrintWriter(outputPath);

        pw.println("Location:");
        Map<String, Integer> locTreemap = new TreeMap<>(locMap);
        printTreeMap(locTreemap, pw);
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


        ///////////////////////////////////////////////////////////////////////////////////////////
        String para = TestLabeler.readFile("./test_files/testSet.txt");
        Chunker chunker = new Chunker();
        Chunker.Chunking chunks = chunker.process(para, 1);
        ArrayList<TemplateRow> elementList = createRowList(chunks);

        writeToFile(elementList, "./test_files/output_labeled_test.txt"); // Writing the elementList to a file formatted for CRF++

        ///////////////////////////////////////////////////////////////////////////////////////////
//        ArrayList<TemplateRow> loadedTemplateRowList = loadData("./test_files/after_test.txt");
//        clusterTemplateRows(loadedTemplateRowList);

        ///////////////////////////////////////////////////////////////////////////////////////////
//        ArrayList<TemplateRow> loadedTemplateRowList = load_data("./test_files/after_test.txt");
//        clusterTemplateRows(loadedTemplateRowList);


    }
}
