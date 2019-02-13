
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class InvertedIndex {


    private TreeMap<Integer, TreeMap<Integer, Integer>> invInd;

    public InvertedIndex() {
        this.invInd = new TreeMap<>();
    }


    /*
    * Creates the inverted index using the forward index
    * */
    public void createInvertedIndex(HashMap<Integer, HashMap<Integer, Integer>> fwdInd) {
        HashMap<Integer, Integer> tempDocIndex;
        Integer wordFreq = 0;

        for(Integer docId : fwdInd.keySet()) {

            tempDocIndex = fwdInd.get(docId);

            for(Integer wordId : tempDocIndex.keySet()){
                wordFreq = tempDocIndex.get(wordId);
                addWordToInvertedIndex(wordId, docId, wordFreq);
            }
        }
    }

    /*
    *
    * Adds words to the inverted index.
    * If the word is not in the inverted index it is added,
    * otherwise the index for the word and document is added.
    * */
    private void addWordToInvertedIndex(Integer wordId, Integer docId, Integer freqInDoc){
        TreeMap<Integer, Integer> tempWordIndex;

        if(invInd.containsKey(wordId)) {
            tempWordIndex = invInd.get(wordId);
            if(!tempWordIndex.containsKey(docId)){
                tempWordIndex.put(docId, freqInDoc);
                invInd.put(wordId,tempWordIndex);
            }
        }
        else {
            TreeMap<Integer, Integer> newWordIndex = new TreeMap<>();

            newWordIndex.put(docId, freqInDoc);
            invInd.put(wordId, newWordIndex);
        }
    }


    /*
    * Write the entire inverted index to a file
    * Used for testing and troubleshooting program
    * */
    public void writeDataToFile(){

        TreeMap<Integer, Integer> tempIndex;
        Integer wordFreq = 0;
        String tokenOutput;
        String wordIdString;

        try {
            PrintWriter writer = new PrintWriter("inverted_index_file.txt");

            // Write to output inverted index keys and values
            for(Integer wordId : invInd.keySet()) {
                tempIndex = invInd.get(wordId);
                wordIdString = String.format("wordId: %d ; ", wordId);
                writer.print(wordIdString);

                for(Integer docId : tempIndex.keySet()) {
                    wordFreq = tempIndex.get(docId);
                    // tokenOutput = String.format(
                    //        "wordId: %-10s docId: %-10d wordFreq: %-10d \n", wordId, docId, wordFreq);
                    tokenOutput = String.format(
                            "%d:%d ; ", docId, wordFreq);
                    writer.print(tokenOutput);
                }
                writer.print("\n");
            }

            writer.close();
        } catch(FileNotFoundException IOException) {
            System.out.println("Error! ");
        }
    }

    /*
    * Searches for a word in the inverted index and returns its index
    * */
    public TreeMap<Integer, Integer> queryInvertedIndex(Integer wordId) {
        TreeMap<Integer, Integer> wordIndex = new TreeMap<>();

        if(invInd.containsKey(wordId)) {
            wordIndex = invInd.get(wordId);
        }
        else{
            System.out.println("Error word id not in inverted index!");
        }

        return wordIndex;
    }

    public int getIndexSize(){
        int size = 0;

        for(Integer wordId : invInd.keySet()) {
            for(Integer docId: invInd.get(wordId).keySet()) {
                size += invInd.get(docId).size();
            }
        }

        return size;
    }


    public TreeMap<Integer, TreeMap<Integer, Integer>> getInvInd() {
        return invInd;
    }

}
