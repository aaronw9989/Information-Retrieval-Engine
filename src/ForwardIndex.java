
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;



public class ForwardIndex {

    private HashMap<Integer, HashMap<Integer, Integer>> fwdInd;
    private TreeMap<Integer, HashMap<Integer, Integer>> sortedForwardIndex;

    public ForwardIndex() {
        this.fwdInd = new HashMap<>();
        this.sortedForwardIndex = new TreeMap<>();
    }

    /*
    * Adds new words to the forward index
    * */
    public void addWord(Integer docId, Integer wordId) {
        Integer temp;
        if(fwdInd.containsKey(docId)) {
            //fwdInd.get(docId);
            if(fwdInd.get(docId).containsKey(wordId)){
                temp = fwdInd.get(docId).get(wordId);
                temp++;
                fwdInd.get(docId).put(wordId, temp);
            }
            else {
                fwdInd.get(docId).put(wordId, 1);
            }
        }
        else {
            HashMap<Integer, Integer> wordMap = new HashMap<>();
            wordMap.put(wordId, 1);
            fwdInd.put(docId, wordMap);
        }
    }

    /*
    *
    * Creates the forward index using an inverted word dictionary and
    * a data structure that maps doc id's to the tokens contained in that
    * document.
    * */
    public void createForwardIndex(HashMap<String, Integer> wordDic,
                                   HashMap<Integer, ArrayList<String>> docIdToTokenList )
    {
        ArrayList<String> temp = new ArrayList<>();

        for(Integer docId : docIdToTokenList.keySet()) {
            temp = docIdToTokenList.get(docId);
            Integer wordId = 0;

            for (String token : temp) {
                wordId = wordDic.get(token);
                addWord(docId, wordId);
            }
        }
    }

    public void printTokenInDocument(Integer docId) {

        HashMap<Integer, Integer> tempDocHashMap = fwdInd.get(docId);

        for(Integer tokenId : tempDocHashMap.keySet()) {
            System.out.printf("tokenId: %d tokenCount: %d \n", tokenId, tempDocHashMap.get(tokenId));
        }
    }

    public Integer getWordCount(Integer docId, Integer wordId) {
        Integer wordCount = 0;

        if(!fwdInd.containsKey(docId)) {
            return wordCount;
        }

        if(!fwdInd.get(docId).containsKey(wordId)){
            return wordCount;
        }

        else{
            wordCount = fwdInd.get(docId).get(wordId);
        }
        return wordCount;
    }


    /*
    * Writes the entire forward index to an output file
    * Used for testing and troubleshoot the program
    * */
    public void writeDataToFile(){

        HashMap<Integer, Integer> tempIndex;
        Integer wordFreq = 0;
        String tokenOutput;
        String docIdString;

        // Create a sorted forward index using tree map for
        // easily readable output
        for(Integer docId : fwdInd.keySet()){
            HashMap<Integer, Integer> temp = new HashMap<>();
            temp = fwdInd.get(docId);
            sortedForwardIndex.put(docId, temp);
        }

        try {
            PrintWriter writer = new PrintWriter("forward_index_file.txt");

            // Write to output inverted index keys and values
            for(Integer docId : sortedForwardIndex.keySet()) {
                tempIndex = sortedForwardIndex.get(docId);
                docIdString = String.format("docId: %d ; ", docId);
                writer.print(docIdString);
                for(Integer wordId : tempIndex.keySet()) {
                    wordFreq = tempIndex.get(wordId);
                    // tokenOutput = String.format(
                    // "docId: %-10s wordId: %-10d wordFreq: %-10d \n", docId, wordId, wordFreq);
                    tokenOutput = String.format(
                        "%d:%d ; ", wordId, wordFreq);
                    writer.print(tokenOutput);
                }
                writer.print("\n");
            }

            writer.close();
        } catch(FileNotFoundException IOException) {
            System.out.println("Error! ");
        }

    }

    public HashMap<Integer, HashMap<Integer, Integer>> getFwdInd() {
        return fwdInd;
    }

    /*
    *
    * */
    public int getIndexSize(){
        int size = 0;

        for(Integer docId : fwdInd.keySet()) {
            for(Integer wordId: fwdInd.get(docId).keySet()) {
                size += fwdInd.get(docId).size();
            }
        }

        return size;
    }
}
