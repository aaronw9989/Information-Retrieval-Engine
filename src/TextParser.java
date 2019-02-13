

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TextParser {

    private HashMap<Integer, String> documentIdDictionary;
    private HashMap<Integer, String> wordDictionary;
    private HashMap<String, Integer> invertedWordDictionary;
    private ArrayList<String> stopWords;
    private HashMap<Integer, ArrayList<String>> docIdsAndTokens;
    private TreeMap<Integer, ArrayList<String>> titleOnly;
    private TreeMap<Integer, ArrayList<String>> descriptionAndTitle;
    private TreeMap<Integer, ArrayList<String>> narrativeAndTitle;


    public TextParser() {
        documentIdDictionary= new HashMap<> ();
        wordDictionary = new HashMap<>();
        invertedWordDictionary = new HashMap<>();
        stopWords = new ArrayList<> ();
        docIdsAndTokens = new HashMap<>();
        titleOnly = new TreeMap<>();
        descriptionAndTitle = new TreeMap<>();
        narrativeAndTitle = new TreeMap<>();
        // Load the stop word into an array list
        loadStopWords();
    }


    /*
    * Takes a file path as a string argument, opens the file, reads the text, cleans the tokens
    * and removes stop words.
    * Tokens are stored in the hash-map docIdsAndTokens, where docIds are used as the key
    * and tokens are stored as an array-list of strings.
    * */
    public void readTextTokens(String filePath) {

        ArrayList<String> tokenList = new ArrayList<>();
        ArrayList<String> emptyArrayList;
        ArrayList<String> documentTokensTemp;

        File inFile;
        Scanner sc = new Scanner(System.in);

        String textLine;

        Boolean isText = false;
        Boolean beginText = false;
        Pattern textRegex = Pattern.compile("<TEXT>");
        Matcher textMatcher;

        Boolean endText = false;
        Pattern notTextRegex = Pattern.compile("</TEXT>");
        Matcher notTextMatcher;

        Boolean isDocId = false;
        Pattern docIdRegex = Pattern.compile("(<DOCNO>FT911-)[0-9]+(</DOCNO>)");
        Matcher docIdMatcher;

        Integer docIdNumber = 0;
        String strDocId;

        try {
            inFile = new File(filePath);
            sc = new Scanner(inFile);
            //System.out.printf("Document %s \n", documentNames[i]);

        } catch (Exception ex) {
            System.out.printf("Warning file did not open! %s", filePath);
        }

        // While the file contains more line read them in one at a time
        while (sc.hasNextLine()) {

            // Read in the next line as a string
            textLine = sc.nextLine();

            // Test if text is a document id number
            docIdMatcher = docIdRegex.matcher(textLine);
            isDocId = docIdMatcher.matches();

            // If text is a document id number then add it to the
            // document id dictionary and create a space for it in the
            // docIds and tokens hashmap
            if (isDocId) {
                textLine = textLine.replace("<DOCNO>", "");
                textLine = textLine.replace("</DOCNO>", "");
                strDocId = textLine.replace("FT911-", "");
                docIdNumber = Integer.parseInt(strDocId);
                documentIdDictionary.put(docIdNumber, textLine);
                // Insert an empty array list of strings into map
                emptyArrayList = new ArrayList<>();
                docIdsAndTokens.put(docIdNumber, emptyArrayList);
                //System.out.printf("doc name %s, doc num %d \n", textLine, docIdNumber);
            }

            // Test if line is </TEXT>, the ending of a text area
            notTextMatcher = notTextRegex.matcher(textLine);
            endText = notTextMatcher.matches();

            // If this is the end of the text area then clean the tokens and
            // add them to token list
            if (endText) {
                isText = false;
                documentTokensTemp = new ArrayList<>(cleanTokens(tokenList));
                docIdsAndTokens.put(docIdNumber, documentTokensTemp);
                tokenList.clear();
            }

            // If text area then add it to the line to the token list
            if(isText){
                tokenList.add(textLine);
            }

            // Test if line is <TEXT>, the beginning of a text area
            textMatcher = textRegex.matcher(textLine);
            beginText = textMatcher.matches();
            if (beginText) {
                isText = true;
            }
        }
    }


    /*
    * Cleans document tokens and returns them as an ArrayList
    * */
    public ArrayList<String> cleanTokens(ArrayList<String> dirtyTokens) {
        ArrayList<String> cleanTokens = new ArrayList<>();
        String tempString;
        String stemmedToken;
        String[] tokenList;

        for(String textLine : dirtyTokens) {
            // 1.  Convert text to lower case.
            // 2.  Remove numbers and words that contain numbers
            // 3.  Split on characters which are not a-z
            // (i.e., split on punctuation, whitespace or any special characters)
            tokenList = textLine.toLowerCase().replaceAll("\\w*\\d\\w*", "").trim().split("\\s*[^a-z]\\s*");

            for(String token : tokenList) {

                tempString = token;
                if(!stopWords.contains(tempString) && tempString.length() > 1) {
                    //Stem each token before adding
                    stemmedToken = stemToken(tempString);
                    cleanTokens.add(stemmedToken);
                }
            }
        }
        return cleanTokens;
    }

    /*
    *
    * Cleans a single word token entered from system user.
    * */
    public String cleanToken(String token) {
        String cleanedToken;

        // convert the word to lower case
        cleanedToken = token.toLowerCase();
        // stem the token
        cleanedToken = stemToken(cleanedToken);

        return cleanedToken;
    }

    /*
     *
     * Takes a string and stems it using porter stemmer algorithm
     * */
    private String stemToken(String token) {
        String stem = "";
        Porter stemmer = new Porter();

        if(token.length() > 0) {
            try {
                stem = stemmer.stripAffixes(token);
                //if(token.contains("predictor")) {System.out.println(token + " "+ stem);}
                //System.out.println(stem);
            } catch (StringIndexOutOfBoundsException ex) {
                System.out.printf("Error! %s Token = %s \n", ex, token);
            }
        }
        return stem;
    }

    public void createWordDictionary() {

        TreeSet<String> wordTreeSet = new TreeSet<>();

        // Add the stemmed tokens to a tree set
        // This removes duplicate tokens and alphabetizes them
        for(Integer key : docIdsAndTokens.keySet()) {
            for(String token : docIdsAndTokens.get(key)) {
                wordTreeSet.add(token);
            }
            //System.out.printf("Token = %s \n", token);
        }

        Integer wordId = 1;
        for(String token : wordTreeSet) {
            wordDictionary.put(wordId, token);
            //System.out.printf("wordId: %d token: %s \n", wordId, token);
            wordId++;
        }
        //System.out.println("===========================");
        //System.out.printf("Word Dictionary Size: %d \n", wordDictionary.size());
    }


    /*
     * Creates an array list of stop words from stopwords.txt
     **/
    private void loadStopWords() {
        String word;
        try {
            File inFile = new File("./src/stopwordlist.txt");
            Scanner sc = new Scanner(inFile);

            while(sc.hasNext()) {
                word = sc.next();
                stopWords.add(word);
            }
            sc.close();
        } catch (Exception ex){
            System.out.println("Error file did not open! stopwordslist.txt");
        }
    }

    public void printDocSizes(){
        int numWords;
        for(Integer docId : docIdsAndTokens.keySet()){
            numWords = docIdsAndTokens.get(docId).size();
            System.out.printf("docId: %d numWords: %d \n", docId, numWords);
        }
    }

    /*
     *
     * */
    public void createInvertedWordDictionary() {
        for(Integer key : wordDictionary.keySet()) {
            invertedWordDictionary.put(wordDictionary.get(key), key);
        }
    }

    /*
    *
    * Query the inverted word diction to check if word is in system and also get word id.
    * */
    public Integer queryInvertedWordDictionary(String word) {

        Integer wordId = 0;

        if(invertedWordDictionary.containsKey(word)) {
            wordId = invertedWordDictionary.get(word);
        }

        return wordId;
    }

    public int getNumberOfDocuments() {
        int numDocuments = docIdsAndTokens.size();
        return numDocuments;
    }


    public void readQueryText() {

        ArrayList<String> tokenList = new ArrayList<>();
        String filePath = "./topics.txt";
        File inFile;
        Scanner sc = new Scanner(System.in);
        String textLine;
        String strQueryId;
        int queryIdNumber = 0;
        String titleLine;
        boolean endOfQuery = false;

        ArrayList<String> emptyArrayList;
        ArrayList<String> dirtyTitle;
        ArrayList<String> cleanTitle;
        ArrayList<String> dirtyDescription;
        ArrayList<String> cleanDescription;
        ArrayList<String> dirtyNarrative;
        ArrayList<String> cleanNarrative;

        // Query number regex
        Pattern queryIdRegex = Pattern.compile("(<num> Number: )[0-9]+(.)*");
        Matcher queryIdMatcher;

        // Title regex
        Pattern titleRegex = Pattern.compile("(<title>)(.)*");
        Matcher titleMatcher;

        // Description regex
        // <desc> Description:
        Pattern descriptionRegex = Pattern.compile("(<desc> Description:)(.)*");
        Matcher descriptionMatcher;

        // Narrative regex
        // <narr> Narrative:
        Pattern narrativeRegex = Pattern.compile("(<narr> Narrative:)(.)*");
        Matcher narrativeMatcher;

        // Open topics.txt
        try {
            inFile = new File(filePath);
            sc = new Scanner(inFile);

        // While the file contains more line read them in one at a time
        while (sc.hasNextLine()) {

            // Read in the next line as a string
            textLine = sc.nextLine();

            // Test if text is a query id number
            queryIdMatcher = queryIdRegex.matcher(textLine);

            // Test if text is a title
            titleMatcher = titleRegex.matcher(textLine);

            // Test if text is a description
            descriptionMatcher = descriptionRegex.matcher(textLine);


            if (queryIdMatcher.matches()) {
                strQueryId = textLine.replaceAll("[^\\d.]", "");
                queryIdNumber = Integer.parseInt(strQueryId);
                // Insert an empty array list of strings in treemap
                emptyArrayList = new ArrayList<>();
                titleOnly.put(queryIdNumber, emptyArrayList);
                System.out.printf("Number %d \n", queryIdNumber);
            }

            if (titleMatcher.matches()) {
                titleLine = textLine.replace("<title>", "");
                System.out.println(titleLine);
                dirtyTitle = new ArrayList<>();
                dirtyTitle.add(titleLine);
                //System.out.println(titleLine);
                cleanTitle = cleanTokens(dirtyTitle);
                //System.out.println(cleanTitle);
                titleOnly.put(queryIdNumber, cleanTitle);

                dirtyTitle.clear();
            }

            if (descriptionMatcher.matches()) {

                dirtyDescription = new ArrayList<>();

                while (sc.hasNextLine()) {

                    textLine = sc.nextLine();
                    narrativeMatcher = narrativeRegex.matcher(textLine);

                    if (narrativeMatcher.matches()) {

                        dirtyNarrative = new ArrayList<>();

                        while (sc.hasNextLine()) {

                            textLine = sc.nextLine();

                            if (textLine.contains("</top>")) {
                                cleanNarrative = cleanTokens(dirtyNarrative);
                                narrativeAndTitle.put(queryIdNumber, cleanNarrative);
                                endOfQuery = true;
                                break;
                            }
                            System.out.println(textLine);
                            dirtyNarrative.add(textLine);
                        }
                    }

                    if (endOfQuery) {
                        cleanDescription = cleanTokens(dirtyDescription);
                        descriptionAndTitle.put(queryIdNumber, cleanDescription);
                        endOfQuery = false;
                        break;
                    }

                    System.out.println(textLine);
                    dirtyDescription.add(textLine);
                }
            }
        }
        sc.close();

        for(Integer docId : titleOnly.keySet()) {

            ArrayList tempNarrative = narrativeAndTitle.get(docId);
            ArrayList tempDescription = descriptionAndTitle.get(docId);

            tempNarrative.addAll(titleOnly.get(docId));
            tempDescription.addAll(titleOnly.get(docId));

            narrativeAndTitle.put(docId, tempNarrative);
            descriptionAndTitle.put(docId, tempDescription);

        }

        } catch (Exception ex) {
            System.out.printf("Warning file did not open! %s", filePath);
        }
    }

    public TreeMap<Integer, ArrayList<String>> getTitleOnly() {
        return titleOnly;
    }

    public TreeMap<Integer, ArrayList<String>> getDescriptionAndTitle() {
        return descriptionAndTitle;
    }

    public TreeMap<Integer, ArrayList<String>> getNarrativeAndTitle() {
        return narrativeAndTitle;
    }

    public HashMap<Integer, ArrayList<String>> getDocIdsAndTokens() {
        return docIdsAndTokens;
    }

    public HashMap<Integer, String> getWordDictionary() {
        return wordDictionary;
    }

    public HashMap<Integer, String> getDocumentIdDictionary(){
        return documentIdDictionary;
    }

    public HashMap<String, Integer> getInvertedWordDictionary() {
        return invertedWordDictionary;
    }
}
