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

public class base extends Searcher {

    public base() throws IOException{

        super();
    }
    public base(String methodName) throws IOException{
        this();
        if(methodName.contains("Unigram")){
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.DIRECTORY_NAME_WITH_ENTITY_FIELD))));
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
}
