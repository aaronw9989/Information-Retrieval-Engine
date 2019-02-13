
import javax.xml.soap.Text;
import java.io.*;
import java.util.*;

/**
 * Author: Aaron R. Williams
 * Date: 13 February 2019
 *
 * This aim of this project is to create an Information Retrieval Engine, that can be used to retrieve documents
 * relevant to the user entered query. It includes the following main components: text parser, forward index,
 * inverted index and query processor. In summary, this IR engine reads in a set of documents, tokenizes the text,
 * creates a word dictionary, file dictionary, forward index and inverted index.
 *
 */



public class Main {

    private static TextParser textParser = new TextParser();
    private static ForwardIndex forwardIndex = new ForwardIndex();
    private static InvertedIndex invertedIndex = new InvertedIndex();
    private static QueryProcessor queryProcessor = new QueryProcessor();
    private static int numDocs = 0;
    private static Scanner kb = new Scanner(System.in);

    // String array of document paths
    private static String[] docNames = {"./src/ft911/ft911_1", "./src/ft911/ft911_2", "./src/ft911/ft911_3",
            "./src/ft911/ft911_4", "./src/ft911/ft911_5", "./src/ft911/ft911_6",
            "./src/ft911/ft911_7", "./src/ft911/ft911_8", "./src/ft911/ft911_9",
            "./src/ft911/ft911_10", "./src/ft911/ft911_11", "./src/ft911/ft911_12",
            "./src/ft911/ft911_13", "./src/ft911/ft911_14","./src/ft911/ft911_15"};



    public static void main(String[] args) {
        double sTime, eTime, duration;


        // Opens text document, clean tokens then create file and word dictionaries
        sTime = System.nanoTime();
        parseDocumentText();
        eTime = System.nanoTime();
        duration = (eTime - sTime);
        System.out.printf("Seconds to parse doc: %.2f \n", (duration/1000000)/1000);

        // Creates the forward index
        // sTime = System.nanoTime();
        makeForwardIndex();
        // eTime = System.nanoTime();
        // duration = (eTime - sTime);
        // System.out.printf("Seconds to create forward index: %.2f \n", (duration/1000000)/1000);

        // Creates the inverted index
        // sTime = System.nanoTime();
        makeInvertedIndex();
        // eTime = System.nanoTime();
        // duration = (eTime - sTime);
        // System.out.printf("Seconds to create inverted index: %.2f \n\n", (duration/1000000)/1000);

        // Get the number of documents in the document collection
        numDocs = textParser.getNumberOfDocuments();

        // Allows user to query the inverted index
        // queryInvertedIndexMenu();

        // Setup the query processor
        makeQueryProcessor();

        // Allows user to query documents
        // queryOptionsMenu();

        // close the scanner at the end of the program
        kb.close();
    }


    private static void parseDocumentText() {

        HashMap<Integer, ArrayList<String>> docIdsAndTokens = new HashMap<>();

        for(String filePath : docNames) {
            textParser.readTextTokens(filePath);
        }

        docIdsAndTokens = textParser.getDocIdsAndTokens();

       //textParser.printDocSizes();
       textParser.createWordDictionary();
       textParser.createInvertedWordDictionary();
       writeOutputFile(textParser.getWordDictionary());

    }

    private static void makeForwardIndex() {

        HashMap<String, Integer> invertedWordDic = textParser.getInvertedWordDictionary();

        // Create the forward index
        forwardIndex.createForwardIndex(textParser.getInvertedWordDictionary(), textParser.getDocIdsAndTokens());
        int size = forwardIndex.getIndexSize();
        System.out.printf("Forward index size: %d \n", size);

        // Write data to output file
        forwardIndex.writeDataToFile();
    }

    private static void makeInvertedIndex() {

        HashMap<Integer, HashMap<Integer, Integer>> fwdIndexCopy = forwardIndex.getFwdInd();

        // Create the inverted index by passing a copy of the forward index
        invertedIndex.createInvertedIndex(fwdIndexCopy);
        int size = forwardIndex.getIndexSize();
        System.out.printf("Inverted index size: %d \n", size);

        // Write data to output file
        invertedIndex.writeDataToFile();
    }

    /*
    * Prints menu for user to either query a word in the inverted index or exit the program
    * */
    private static void queryInvertedIndexMenu() {

        System.out.println("Welcome to Project-2! Use menu options below to query inverted index.\n");

        int user_option = 0;

        while(user_option != 2) {
            System.out.println("\n-- Menu Options -- \n1. Query Inverted Index \n2. Exit Program \n");
            System.out.printf("Enter Option: ");
            // get user input object
            user_option = kb.nextInt();
            // consume the nextLine token
            kb.nextLine();
            // if user selects 1 query inverted index
            if(user_option == 1){
                queryInvertedIndex();
            }
        }
    }

    /*
    * Queries the inverted index using the user's input string
    * */
    private static void queryInvertedIndex() {

        TreeMap<Integer, Integer> wordIndex;

        String word;
        String token;
        Integer wordId = 0;
        Integer wordFreq = 0;

        System.out.printf("Enter Sting to Query: ");

        // get user input
        word = kb.nextLine();

        // clean the user word entered
        token = textParser.cleanToken(word);

        //System.out.printf("Cleaned token %s ", token);

        // Check if word is in the dictionary
        wordId = textParser.queryInvertedWordDictionary(token);

        if(wordId == 0) {
            System.out.println("Error word not in system!");
        }
        else {
            wordIndex = invertedIndex.queryInvertedIndex(wordId);
            System.out.printf("Word: %s, WordId: %d \n", token, wordId);

            int numWords = 0;
            for(Integer docId : wordIndex.keySet()) {
                wordFreq = wordIndex.get(docId);
                System.out.printf("docId: %d : freq %d; ", docId, wordFreq);
                numWords++;
                // print new line every ten words
                if(numWords == 9) {
                    System.out.println();
                    numWords = 0;
                }
            }
            System.out.println();
        }
    }

    /*
    * Query documents
    *
    * */
    private static void queryOptionsMenu() {

        ArrayList<String> userQuery = new ArrayList<>();

        System.out.println("Welcome to Project-3! Use menu options below to query inverted index.\n");

        int user_option = 0;

        while(user_option != 2) {
            System.out.println("\n-- Menu Options -- \n1. Query Using Title \n2. Exit Program \n");
            System.out.printf("Enter Option: ");
            // get user input object
            user_option = kb.nextInt();
            // consume the nextLine token
            kb.nextLine();
            // if user selects 1 query inverted index
            if(user_option == 1){
               queryUsingTitle();
            }
        }
    }

    private static void makeQueryProcessor() {

        // Pre-process topics.txt
        textParser.readQueryText();

        // Pass the word dictionary to the query processor
        queryProcessor.setWordDic(textParser.getInvertedWordDictionary());

        // Creates the document weight matrix
        queryProcessor.createDocumentWeightMatrix(invertedIndex.getInvInd(), numDocs);

        // Create the tf matrices for the quires
        queryProcessor.createTitleTF(textParser.getTitleOnly());
        queryProcessor.createTitleDescriptionTF(textParser.getTitleOnly());
        queryProcessor.createTitleNarrativeTF(textParser.getTitleOnly());

        // Create the query weight matrix
        queryProcessor.createQueryWeightMatrix();

        queryProcessor.writeDataToFile();
    }

    private static void queryUsingTitle() {

    }

    /*
     * Output file dictionary and word dictionary to output_file.txt
     **/
    private static void writeOutputFile(HashMap<Integer, String> wordDic) {

        String tokenOutput;
        String fileOutput;

        try {
            PrintWriter writer = new PrintWriter("output_file.txt");
            // Write to output word dictionary and keys
            for(Integer key : wordDic.keySet()) {
                tokenOutput = String.format("%-30s %-10d \n", wordDic.get(key), key);
                writer.print(tokenOutput);
            }
            writer.close();
        } catch(FileNotFoundException IOException) {
            System.out.println("Error! ");
        }
    }
}
