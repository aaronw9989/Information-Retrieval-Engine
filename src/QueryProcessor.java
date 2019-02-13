

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.lang.Math.*;


public class QueryProcessor {

    private TreeMap<Integer, Integer> documentFrequency;
    private TreeMap<Integer, Double> invertedDocumentFrequency;
    private TreeMap<Integer, TreeMap<Integer, Double>> documentWeightMatrix;
    private TreeMap<Integer, TreeMap<Integer, Double>> titleWeightMatrix;
    private TreeMap<Integer, TreeMap<Integer, Double>> titleAndDesWeightMatrix;
    private TreeMap<Integer, TreeMap<Integer, Double>> titleAndNarWeightMatrix;
    private TreeMap<Integer, TreeMap<Integer, Integer>> titleOnly;
    private TreeMap<Integer, TreeMap<Integer, Integer>>  titleAndDescription;
    private TreeMap<Integer, TreeMap<Integer, Integer>>  titleAndNarrative;
    private TreeMap<Integer, TreeMap<Integer, Integer>> queryTermFreq;
    private HashMap<String, Integer> wordDic;
    private TreeMap<Integer, Double> documentEuclidianLengths;

    public QueryProcessor() {
        this.documentFrequency = new TreeMap<>();
        this.invertedDocumentFrequency = new TreeMap<>();
        this.documentWeightMatrix = new TreeMap<>();
        this.titleWeightMatrix = new TreeMap();
        this.titleAndDesWeightMatrix = new TreeMap();
        this.titleAndNarWeightMatrix = new TreeMap();
        this.titleOnly = new TreeMap<>();
        this.titleAndDescription = new TreeMap<>();
        this.titleAndNarrative = new TreeMap<>();
        this.wordDic = new HashMap<>();
        this.documentEuclidianLengths = new TreeMap<>();
    }

    /*
     *
     *
     */
    public void createDocumentWeightMatrix(TreeMap<Integer, TreeMap<Integer, Integer>> invInd, int numDocs) {

        double df = 0;
        double idf = 0;
        double totalNumDocs = numDocs;
        double termFreq = 0;
        double weight = 0;
        TreeMap<Integer, Double> tempIndex;

        // Creates the document frequency matrix
        for (Integer wordId : invInd.keySet()) {
            // Finds the number of documents a term occurs in
            // and adds is to the df matrix
            documentFrequency.put(wordId, invInd.get(wordId).size());
        }

        // Creates the inverse document frequency matrix
        // Note: Integer and Double and immutable data types,
        //       ergo, idf must be calculated using doubles.
        for (Integer wordId : documentFrequency.keySet()) {
            // get the doc freq and convert it to double
            df = documentFrequency.get(wordId);
            // Calculate the idf
            idf = Math.log10(totalNumDocs / df);
            Double idf_ = idf;
            // Add the idf to to the matrix
            invertedDocumentFrequency.put(wordId, idf_);
        }

        int tf = 0;
        // Weight matrix for each term in each document
        // using the inverted index
        for (Integer wordId : invInd.keySet()) {
            for (Integer docId : invInd.get(wordId).keySet()) {
                // get the idf for the term
                idf = invertedDocumentFrequency.get(wordId);
                // get the term freq for the term in the current document
                tf = invInd.get(wordId).get(docId);
                weight = tf * idf;
                Double weight_t_d = weight;

                tempIndex = new TreeMap<>();

                tempIndex.put(docId, weight_t_d);

                documentWeightMatrix.put(wordId, tempIndex);

            }
        }
    }


    /*
    * Creates the query weight matrix
    * */
    public void createQueryWeightMatrix() {

        TreeMap<Integer, Double> tempIndex;
        double idf;
        double tf_t_q;
        double weight;

        // Set up the title query matrix
        for(Integer topicId : titleOnly.keySet()) {
            for(Integer wordId : titleOnly.get(topicId).keySet()) {

                // get the idf for the term
                idf = invertedDocumentFrequency.get(wordId);

                // get the term freq for the term in the current document
                tf_t_q = titleOnly.get(topicId).get(wordId);

                weight = tf_t_q * idf;
                Double weight_t_q = weight;

                tempIndex = new TreeMap<>();
                tempIndex.put(wordId, weight_t_q);
                titleWeightMatrix.put(topicId, tempIndex);
            }
        }

        // Set up the title and description query matrix
        for(Integer topicId : titleAndDescription.keySet()) {
            for(Integer wordId : titleAndDescription.get(topicId).keySet()) {
                // get the idf for the term
                idf = invertedDocumentFrequency.get(wordId);
                // get the term freq for the term in the current document
                tf_t_q = titleAndDescription.get(topicId).get(wordId);
                weight = tf_t_q * idf;
                Double weight_t_q = weight;

                tempIndex = new TreeMap<>();

                tempIndex.put(topicId, weight_t_q);

                titleAndDesWeightMatrix.put(wordId, tempIndex);
            }
        }

        // Set up the title and narrative matrix
        for(Integer topicId : titleAndNarrative.keySet()) {
            for(Integer wordId : titleAndNarrative.get(topicId).keySet()) {
                // get the idf for the term
                idf = invertedDocumentFrequency.get(wordId);
                // get the term freq for the term in the current document
                tf_t_q = titleAndNarrative.get(topicId).get(wordId);
                weight = tf_t_q * idf;
                Double weight_t_q = weight;

                tempIndex = new TreeMap<>();

                tempIndex.put(topicId, weight_t_q);

                titleAndNarWeightMatrix.put(wordId, tempIndex);
            }
        }
    }

    /*
    * Finds the euclidian length of each document in the collection.
    * */
    public void calculateEuclidianLength(TreeMap<Integer, TreeMap<Integer, Integer>> fwdInd) {

        double wordCount = 0;
        double numSquared = 0;
        double sumOfSquares = 0;
        double euclidianLength = 0;

        for(Integer docId : fwdInd.keySet()) {
            for(Integer wordId : fwdInd.get(docId).keySet()) {
                wordCount = fwdInd.get(docId).get(wordId);
                numSquared = Math.pow(wordCount, 2);
                sumOfSquares += numSquared;
            }
            euclidianLength = Math.sqrt(sumOfSquares);
            Double eucLen = euclidianLength;
            documentEuclidianLengths.put(docId, eucLen);
            euclidianLength = 0;
        }
    }

    /*
     * Compute the cosine similarity scores
     * */
    public void computeCosineSimilarity() {

        // Calculate the dot product of each document and query
        // Then normalize by dividing by the euclidian length of the document
        // Finally rank the documents by their score and output the results.
    }

    /*
     * Creates the query term frequency list
     * */
    private TreeMap<Integer, TreeMap<Integer, Integer>> createQueryTermFreq(
            TreeMap<Integer, ArrayList<String>> topicIdToTokenList)
    {
        ArrayList<String> temp;
        queryTermFreq = new TreeMap();

        for(Integer topicNum : topicIdToTokenList.keySet()) {
            temp = topicIdToTokenList.get(topicNum);
            Integer wordId = 0;
            System.out.println(topicNum);
            for (String token : temp) {

                if(wordDic.containsKey(token)){
                    wordId = wordDic.get(token);
                } else {
                    while(!wordDic.containsKey(token)){
                        token = token.substring(0, token.length() - 1);
                        System.out.println(token);
                    }
                    wordId = wordDic.get(token);
                }
                addWord(topicNum, wordId);
            }
        }
        return queryTermFreq;
    }

     /* Adds new words to query term frequency
     */
    private void addWord(Integer topicNum, Integer wordId) {
        Integer temp;
        if(queryTermFreq.containsKey(topicNum)) {
            try {
                if(queryTermFreq.get(topicNum).containsKey(wordId)){
                    temp = queryTermFreq.get(topicNum).get(wordId);
                    temp++;
                    queryTermFreq.get(topicNum).put(wordId, temp);
                }
                else {
                    queryTermFreq.get(topicNum).put(wordId, 1);
                }
            }catch(NullPointerException ex){
                System.out.println(ex);
            }
        }
        else {
            TreeMap<Integer, Integer> wordMap = new TreeMap<>();
            wordMap.put(wordId, 1);
            queryTermFreq.put(topicNum, wordMap);
        }
    }

    /*
     *
     */
    public void writeDataToFile() {
        String tokenOutput;
        TreeMap<Integer, Double> tempIndex;
        double word_weight = 0;
        double freq = 0;

        try {
            PrintWriter writer = new PrintWriter("query_processor_file.txt");

            // Write to output
            /*for (Integer wordId : documentFrequency.keySet()) {

                Integer df = documentFrequency.get(wordId);
                Double idf = invertedDocumentFrequency.get(wordId);
                // Integer tf = termFrequency.get(wordId);

                tokenOutput = String.format(
                        "wordId: %-10s df: %-10d idf: %-10f \n", wordId, df, idf);
                writer.print(tokenOutput);
            }

            for (Integer wordId : documentWeightMatrix.keySet()) {
                tempIndex = documentWeightMatrix.get(wordId);
                for (Integer docId : tempIndex.keySet()) {
                    word_weight = tempIndex.get(docId);
                    tokenOutput = String.format(
                            "wordId: %-10s docId: %-10d weight: %-10.2f \n", wordId, docId, word_weight);
                    writer.print(tokenOutput);
                }
            }*/

            for (Integer topicId : titleWeightMatrix.keySet()) {
                for (Integer termId : titleWeightMatrix.get(topicId).keySet()) {
                    freq = titleWeightMatrix.get(topicId).get(termId);
                    tokenOutput = String.format(
                            "topicId: %-10s termId: %-10d freq: %-10.2f \n", topicId, termId, freq);
                    writer.print(tokenOutput);
                }
            }

            for(Integer topicId : titleOnly.keySet()) {
                System.out.println(topicId);
                for(Integer termId : titleOnly.get(topicId).keySet())
                    System.out.printf("termId: %d freq: %d \n", termId, titleOnly.get(topicId).get(termId));
            }


            writer.close();
        } catch (FileNotFoundException IOException) {
            System.out.println("Error! ");
        }
    }

    public void createTitleTF(TreeMap<Integer, ArrayList<String>> title) {
        TreeMap<Integer, TreeMap<Integer, Integer>> tempTitle;
        /*for(Integer topicId : title.keySet()) {
            //System.out.println(topicId);
            for (String token : title.get(topicId))
                System.out.println(token);
        }*/
        tempTitle = createQueryTermFreq(title);
        titleOnly = tempTitle;
    }

    public void createTitleDescriptionTF(TreeMap<Integer, ArrayList<String>> titleDes) {
        TreeMap<Integer, TreeMap<Integer, Integer>> tempTitleDes;
        tempTitleDes = createQueryTermFreq(titleDes);
        titleAndDescription = tempTitleDes;
    }

    public void createTitleNarrativeTF(TreeMap<Integer, ArrayList<String>> titleNar) {
        TreeMap<Integer, TreeMap<Integer, Integer>> tempTitleNar;
        tempTitleNar = createQueryTermFreq(titleNar);
        titleAndNarrative = tempTitleNar;
    }

    public void setWordDic(HashMap<String, Integer> wd) {
        this.wordDic = wd;
    }


}
