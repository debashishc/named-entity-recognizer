package nlp.opennlp;

import java.io.*;
import java.util.*;

/**
 * @author Debashish Chakraborty
 * @version 1.0
 */
public class NER_extractor {

    public static String outputPath = "./test_files/final.txt";

    public static ArrayList<TemplateRow> loadData(String filepath) {
        ArrayList<TemplateRow> elementList = new ArrayList<>();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split("\t");

                // Felipe	NC	B-PER	B-PER
                if (strings.length != 4)
                    continue;



                String word = strings[0];
                String tag = strings[1];
                String predicted_chunk = strings[3];

                TemplateRow element = new TemplateRow(word, tag);
                element.setChunk(predicted_chunk);

                elementList.add(element);
                //System.out.println(linearr[0] + " " + linearr[1] + " " + linearr[2]);

                bufferedReader.close();
        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return elementList;
    }

    public static ArrayList<TemplateRow> load_data(String inputPath) {
        File file = new File(inputPath);
        String body = "";
        ArrayList<TemplateRow> elementList = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                Scanner scanner = new Scanner(line);
                String word = "";
                String tag = "";

                while (scanner.hasNextLine()) {
                    String[] strings = scanner.nextLine().split("\t"); // split  on tab
                    word = strings[0]; // read the word
                    tag = strings[3]; // read the tag

                    String predicted_chunk = strings[3];

                    TemplateRow element = new TemplateRow(word, tag);
                    element.setChunk(predicted_chunk);

                    elementList.add(element);
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

    public static ArrayList<TemplateRow> createElementList(Chunker.Chunking chunkedData) {

        ArrayList<TemplateRow> elementList = new ArrayList<>();

        String[][] tokens = chunkedData._tokens;
        String[][][] taggings = chunkedData._taggings;
        ArrayList<ArrayList<ArrayList<String>>> chunkLabels = chunkedData._chunkLabels;

        // Adding word, tag to object Element
        for (int si = 0; si < tokens.length; si++) {
            String[] sentence = tokens[si];
            for (int wi = 0; wi < sentence.length; wi++) {
                String word = sentence[wi];
                if (!word.equals(".")) {
                    String tag = taggings[si][0][wi];
                    TemplateRow element = new TemplateRow(word, tag);
                    elementList.add(element);
                }

            }
        }

        // Adding chunklable to object Element
        int elementIndex = 0;
        for (ArrayList<ArrayList<String>> chunkSentence : chunkLabels) {
            //System.out.println(chunkSentence);
            for (ArrayList<String> chunkWordGroup : chunkSentence) {
                for (String chunk : chunkWordGroup) {
                    //System.out.println(chunk);
                    TemplateRow element = elementList.get(elementIndex);
                    element.setChunk(chunk);
                    elementIndex++;
                }
            }
            //System.out.println();
        }

        // Uncomment to print out the elements
        /*for (Element element : elementList)
		{
			System.out.println(element.word + " " + element.tag + " " + element.chunk);
		}
		*/
        return elementList;

    }

    public static void writeToFile(ArrayList<TemplateRow> elementList, String filepath) throws IOException {
        PrintWriter pw = new PrintWriter(filepath);
        for (TemplateRow element : elementList) {
            pw.println(element.word + "\t" + element.tag + "\t" + element.chunk);
        }
        pw.close();

    }

    public static void printMap(Map<String, Integer> map) {

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

    }

    @SuppressWarnings("unchecked")
    public static void clusterTemplateRows(ArrayList<TemplateRow> elementList) throws FileNotFoundException {
        ArrayList<String> perList = new ArrayList<>();
        ArrayList<String> locList = new ArrayList<>();
        ArrayList<String> orgList = new ArrayList<>();

        for (int i = 0; i < elementList.size(); i++) {
            TemplateRow rootTemplateRow = elementList.get(i);
            String rootWord = rootTemplateRow.getWord();
            String rootChunk = rootTemplateRow.getChunk();

            // CLustering and joining words based on chunks
            if (rootChunk.equals("B-LOC")) {
                int ci = i;
                String wordarr = rootWord;

                while (true) {
                    ci++;
                    TemplateRow element = elementList.get(ci);
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
                    TemplateRow element = elementList.get(ci);
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
                    TemplateRow element = elementList.get(ci);
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
        Map<String, Integer> orgMap = new HashMap<String, Integer>();

        for (String temp : orgList) {
            Integer count = orgMap.get(temp);
            orgMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Hashing Location
        Map<String, Integer> locMap = new HashMap<String, Integer>();

        for (String temp : locList) {
            Integer count = locMap.get(temp);
            locMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Hashing Person
        Map<String, Integer> perMap = new HashMap<String, Integer>();

        for (String temp : perList) {
            Integer count = perMap.get(temp);
            perMap.put(temp, (count == null) ? 1 : count + 1);
        }

        // Print out Sorted Maps
        // Print sorted Organisations

        PrintWriter pw = new PrintWriter(outputPath);
//        for (TemplateRow element : elementList) {
//            pw.println(element.word + "\t" + element.tag + "\t" + element.chunk);


        pw.println("Location:");
        Map<String, Integer> locTreemap = new TreeMap<>(locMap);
        Object[] l = locTreemap.entrySet().toArray();
        for (Object e : l) {
            pw.println("\t" + ((Map.Entry<String, Integer>) e).getKey() + " : " + ((Map.Entry<String, Integer>) e).getValue());
        }
        pw.println("\n");


        pw.println("Organisation:");
        Map<String, Integer> orgTreemap = new TreeMap<>(orgMap);
        Object[] o = orgTreemap.entrySet().toArray();
        for (Object e : o) {
            pw.println("\t" + ((Map.Entry<String, Integer>) e).getKey() + " : " + ((Map.Entry<String, Integer>) e).getValue());
        }
        pw.println("\n");


        pw.println("Person:");
        Map<String, Integer> perTreemap = new TreeMap<>(perMap);
        Object[] p = perTreemap.entrySet().toArray();
        for (Object e : p) {
            pw.println("\t" + ((Map.Entry<String, Integer>) e).getKey() + " : " + ((Map.Entry<String, Integer>) e).getValue());
        }
//        }
        pw.close();

    }

    public static void main(String[] args) throws IOException {


//        String para = TestLabeler.readFile("./test_files/testSet.txt");
//        Chunker chunker = new Chunker();
//        Chunker.Chunking chunks = chunker.process(para, 1);
//        ArrayList<TemplateRow> elementList = createElementList(chunks);
//
//        writeToFile(elementList, "./test_files/output_labeled_test.txt"); // Writing the elementList to a file formatted for CRF++

//        ArrayList<TemplateRow> loadedTemplateRowList = loadData("./test_files/after_test.txt");
//        clusterTemplateRows(loadedTemplateRowList);

        ArrayList<TemplateRow> loadedTemplateRowList = load_data("./test_files/after_test.txt");
        clusterTemplateRows(loadedTemplateRowList);


    }
}
