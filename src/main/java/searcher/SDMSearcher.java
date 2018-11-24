package main.java.searcher;

import main.java.util.constants;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;

import java.io.IOException;
import java.nio.file.Paths;

public class SDMSearcher extends Searcher {

    public SDMSearcher() throws IOException{

        super();
    }
    public SDMSearcher(String methodName) throws IOException{
        this();
        if(methodName.contains("Bigram")){
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.BIGRAM_DIRECTORY))));
            parser = new QueryParser("body", new ShingleAnalyzerWrapper(new EnglishAnalyzer(),2, 2));
        }

        if(methodName.contains("UNBigram")){
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.UNBIGRAM_DIRECTORY))));
        }
        this.methodName = methodName;
        output_file_name = "output_"+ methodName+"_ranking.txt";
    }
    private void setSearchSimilarityBase(int SDM_Varient){

        SimilarityBase sb;
        switch(SDM_Varient){
            // Unigram Laplace
            case 1:
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
                break;
            // Unigram JM
            case 2:
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
                break;
            // Unigram Dirichlet
            case 3:
                sb = new SimilarityBase() {
                    @Override
                    protected float score(BasicStats bs, float freq, float docln) {
                        float prob_term_doc = (float)Math.log((double)(docln/ (docln + constants.Mu)) + (constants.Mu / (docln +constants.Mu)) * (bs.getNumberOfFieldTokens()));
                        return prob_term_doc;
                    }
                    @Override
                    public String toString() {
                        return null;
                    }
                };
                super.searcher.setSimilarity(sb);
                break;
            // Bigram Laplace
            case 4:
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
                break;
            // Bigram JM
            case 5:
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
                break;
            // Bigram Dirichlet
            case 6:
                sb = new SimilarityBase() {
                    @Override
                    protected float score(BasicStats bs, float freq, float docln) {
                        float prob_term_doc = (float)Math.log((double)(docln/ (docln + constants.Mu)) + (constants.Mu / (docln +constants.Mu)) * (bs.getNumberOfFieldTokens()));
                        return prob_term_doc;
                    }
                    @Override
                    public String toString() {
                        return null;
                    }
                };
                this.searcher.setSimilarity(sb);
                break;
            // Unigram BM25
            case 7 :
               break;
            // Bigram BM25
            case 8:
                break;
            // UNBigram Laplace
            case 9:
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
                break;
             // UNBigram JM
            case 10:
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
                break;
            // UNBigram Dirichlet
            case 11:
                sb = new SimilarityBase() {
                    @Override
                    protected float score(BasicStats bs, float freq, float docln) {
                        float prob_term_doc = (float)Math.log((double)(docln/ (docln + constants.Mu)) + (constants.Mu / (docln +constants.Mu)) * (bs.getNumberOfFieldTokens()));
                        return prob_term_doc;
                    }
                    @Override
                    public String toString() {
                        return null;
                    }
                };
                this.searcher.setSimilarity(sb);
                break;
             // UNBigram BM25
            case 12:
                break;
        }
    }

    //Set Laplace smoothing as the smoothing method
    public void setUnigramLaplace(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(1);

    }

    //Set Jelinek Mercer as the smoothing method
    public void setUnigramJM(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(2);

    }

    //Set Dirichlet smoothing as the smoothing method
    public void setUnigramDirichlet(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(3);

    }

    public void setBigramLaplace(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(4);
    }
    public void setBigramJM(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(5);
    }
    public void setBigramDirichlet(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(6);
    }
    public void setUnigramBM25(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(7);
    }
    public void setBigramBM25(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(8);
    }
    public void setUNBigramLaplace(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(8);
    }
    public void setUNBigramJM(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(9);
    }
    public void setUNBigramDritchlet(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(10);
    }
    public void setUNBigramBM25(){

        System.out.println(this.methodName + " is being called");
        setSearchSimilarityBase(11);
    }

}
