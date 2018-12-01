package main.java.searcher;

import main.java.util.constants;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class base extends Searcher {

    public base() throws IOException{

        super();

    }
    public base(String methodName) throws IOException{
        this();
        if(methodName.contains("Unigram")){
           // searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.DIRECTORY_NAME_WITH_ENTITY_FIELD))));
            parser = new QueryParser("body", new StandardAnalyzer());
        }

        if(methodName.contains("Bigram")){
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.BIGRAM_DIRECTORY))));
            parser = new QueryParser("body", new ShingleAnalyzerWrapper(new EnglishAnalyzer(),2, 2));
        }

        if(methodName.contains("Window")){
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.WINDOW_DIRECTORY))));
            parser = new QueryParser("body", new ShingleAnalyzerWrapper(new EnglishAnalyzer(),2, 2));
        }
        this.methodName = methodName;
        output_file_name = "output_"+ methodName+"_ranking.txt";
    }

    private void setSearchSimilarityBase(int Varient){


        switch(Varient){
            // Unigram Laplace
            case 1:
                callLaplace();
                break;
            // Unigram JM
            case 2:
                callJM();
                break;
            // Unigram Dirichlet
            case 3:
                callDrichlet();
                break;
            // Bigram Laplace
            case 4:
                callLaplace();
                break;
            // Bigram JM
            case 5:
                callJM();
                break;
            // Bigram Dirichlet
            case 6:
                callDrichlet();
                break;
            // Bigram BM25
            case 7:
                break;
            // Window Laplace
            case 8:
                callLaplace();
                break;
             // Window JM
            case 9:
                callJM();
                break;
            // Window Dirichlet
            case 10:
                callDrichlet();
                break;
             // Window BM25
            case 11:
                break;
            default:
                break;
        }
    }

    //Set Laplace smoothing as the smoothing method
    public void setUnigramLaplace(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(1);

    }

    //Set Jelinek Mercer as the smoothing method
    public void setUnigramJM(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(2);

    }

    //Set Dirichlet smoothing as the smoothing method
    public void setUnigramDirichlet(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(3);

    }

    public void setBigramLaplace(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(4);
    }
    public void setBigramJM(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(5);
    }
    public void setBigramDirichlet(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(6);
    }
    public void setBigramBM25(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(7);
    }
    public void setWindowLaplace(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(8);
    }
    public void setWindowJM(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(9);
    }
    public void setWindowDritchlet(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(10);
    }
    public void setWindowBM25(){

        //System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(11);
    }
    private void callLaplace(){
        SimilarityBase sb;
        sb = new SimilarityBase(){
            @Override

            protected float score(BasicStats stats, float freq, float DocLen){
                float numerator = freq + 1;
                Long vocabSize = new Long(stats.getNumberOfFieldTokens());
                float denominator = DocLen + vocabSize.floatValue();
                return (float)Math.log(numerator / denominator);
            }
            @Override
            public String toString(){
                return null;
            }
        };
        this.searcher.setSimilarity(sb);
    }

    private void callJM(){
        SimilarityBase sb;
        sb = new SimilarityBase(){
            @Override
            protected float score(BasicStats stats, float freq, float DocLen){
                float prob_term_doc = ((constants.lambda*(freq/DocLen))+(1- constants.lambda)*(stats.getNumberOfFieldTokens()));
                return (float)Math.log(prob_term_doc);
            }
            @Override
            public String toString(){
                return null;
            }
        };
        this.searcher.setSimilarity(sb);
    }
    private void callDrichlet(){
        SimilarityBase sb;
        sb = new SimilarityBase() {
            @Override
            protected float score(BasicStats bs, float freq, float docln) {
                float prob_term_doc = (float)Math.log((double)(docln/ (docln + constants.Mu)) + (constants.Mu / (docln + constants.Mu)) * (bs.getNumberOfFieldTokens()));
                return prob_term_doc;
            }
            @Override
            public String toString() {
                return null;
            }
        };
        super.searcher.setSimilarity(sb);
    }
    private void createRankingQueryDocPair(String outer_key, String inner_key, Integer rank)
    {
        if(constants.queryDocPair.containsKey(outer_key))
        {
            Map<String, Integer> extract = constants.queryDocPair.get(outer_key);
            extract.put(inner_key, rank);
        }
        else
        {

            Map<String,Integer> temp = new LinkedHashMap<String,Integer>();
            temp.put(inner_key, rank);
            constants.queryDocPair.put(outer_key,temp);
        }
    }

    private void createbaseQuerydocPairScore(String queryId, String docId, Float score){

        if(constants.baseQuerydocPairScore.containsKey(this.methodName)){
            Map<String, Map<String, Float>> extract = constants.baseQuerydocPairScore.get(this.methodName);
            if(extract.containsKey(queryId)){
                Map<String, Float> query_extract = extract.get(queryId);
                query_extract.put(docId, score);
            }
            else{
                Map<String, Float> temp = new LinkedHashMap<>();
                temp.put(docId, score);
                extract.put(queryId,temp);
            }
        }
        else{
            Map<String, Map<String, Float>> query_temp = new LinkedHashMap<>();
            Map<String, Float> doc_temp = new LinkedHashMap<>();
            doc_temp.put(docId, score);
            query_temp.put(queryId, doc_temp);
            constants.baseQuerydocPairScore.put(this.methodName, query_temp);
        }
    }

    private void createbaseQuerydocPairRank(String queryId, String docId, Integer Rank){

        if(constants.baseQuerydocPairRank.containsKey(this.methodName)){
            Map<String, Map<String, Integer>> extract = constants.baseQuerydocPairRank.get(this.methodName);
            if(extract.containsKey(queryId)){
                Map<String, Integer> query_extract = extract.get(queryId);
                query_extract.put(docId, Rank);

            }
            else{
                Map<String, Integer> temp = new LinkedHashMap<>();
                temp.put(docId, Rank);
                extract.put(queryId,temp);
            }
        }
        else{
            Map<String, Map<String, Integer>> query_temp = new LinkedHashMap<>();
            Map<String, Integer> doc_temp = new LinkedHashMap<>();
            doc_temp.put(docId, Rank);
            query_temp.put(queryId, doc_temp);
            constants.baseQuerydocPairRank.put(this.methodName, query_temp);
        }
    }

    private void createEntityQueryDocPair(String outer_key, String inner_key, String entities, Map<String, Map<String,String[]>> searcher_entitiesList)
    {
        String[] p_entities;
        if(entities.equals("")) {
            p_entities = new String[0];
        }
        else {
            p_entities = entities.split(",,,");
        }

        if(searcher_entitiesList.containsKey(outer_key))
        {
            Map<String, String[]> extract = searcher_entitiesList.get(outer_key);
            extract.put(inner_key, p_entities);
        }
        else
        {

            Map<String, String[]> temp = new LinkedHashMap<String, String[]>();
            temp.put(inner_key, p_entities);
            searcher_entitiesList.put(outer_key,temp);
        }
    }


    /**
     *
     */
    private List<String> getRankings(ScoreDoc[] scoreDocs, String queryId, Map<String, Map<String,String[]>> searcher_entitiesList)
            throws IOException {

        List<String> rankings = new ArrayList<String>();


        for(int ind=0; ind<scoreDocs.length; ind++){

            //Get the scoring document
            ScoreDoc scoringDoc = scoreDocs[ind];

            //Create the rank document from searcher
            Document rankedDoc = searcher.doc(scoringDoc.doc);
//				System.out.println(searcher.explain(queryObj, scoringDoc.doc));

            //Print out the results from the rank document
            String docScore = String.valueOf(scoringDoc.score);
            Float score = scoringDoc.score;
            String paraId = rankedDoc.getField("id").stringValue();
            //System.out.println(rankedDoc.getField("entities"));
            String paraEntities = "";
            if(rankedDoc.getField("entities") != null){
                paraEntities = rankedDoc.getField("entities").stringValue();
            }
            //String paraBody = rankedDoc.getField("body").stringValue();
            String paraRank = String.valueOf(ind+1);
            rankings.add(queryId + " Q0 " + paraId + " " + paraRank + " " + docScore + " "+teamName + "-" + methodName);
            createRankingQueryDocPair(queryId, paraId, Integer.valueOf(paraRank));
            createEntityQueryDocPair(queryId, paraId, paraEntities, searcher_entitiesList);
            createbaseQuerydocPairScore(queryId, paraId, score);
            createbaseQuerydocPairRank(queryId, paraId, Integer.valueOf(paraRank));

        }

        return rankings;
    }

    /**
     * Output the rankings for Assignment 2
     * @param p Map containing the query Id and the query value
     */
    @Override
    public void writeRankings(Map<String,String> p)
    {
        Path file = Paths.get(output_file_name);
        Map<String, Map<String,String[]>> searcher_entitiesList = new LinkedHashMap<String, Map<String, String[]>>();
        System.out.println("Method Name in base search: "+methodName);

        try {
            if(output_file_name != null){
                File e = new File(output_file_name);
                if (e.exists()) {
                    e.delete();
                }
                Files.createFile(file);
            }
            else{
                System.out.println("Output file name is null. Please check");
                System.exit(1);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for(Map.Entry<String,String> m:p.entrySet())
        {
            try {
                TopDocs searchDocs = performSearch(m.getValue(), 100);

                ScoreDoc[] scoringDocuments = searchDocs.scoreDocs;
                List<String> formattedRankings = this.getRankings(scoringDocuments, m.getKey(), searcher_entitiesList);
                Files.write(file, formattedRankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        constants.lmQueryDocPair.put(this.methodName, searcher_entitiesList);
    }
}
