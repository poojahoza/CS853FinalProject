package main.java.searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

import java.util.LinkedHashMap;

import java.util.Map;

public class LambdaRankFeatureSearcher extends Searcher{
	
	
	 public LambdaRankFeatureSearcher() throws IOException
     {
         super();
      
     }
	 
	 public LambdaRankFeatureSearcher(String methodName) throws IOException
	    {
	        this();
	        this.methodName= "tf_idf_"+methodName;
	        //output_file_name = "output_"+ methodName+"_ranking.txt";
	    }
	 
	 
	 private void  setFeatureSimilarityBase(int tfidf_variant)
	    {

	        SimilarityBase sb;
	        switch(tfidf_variant)
	        {
	        case 1:
	        	sb = new BM25TfSimilarity();
	        	 this.searcher.setSimilarity(sb);
                 break;
	        case 2:
	        	sb = new BM25IDFSimilarity();
	        	 this.searcher.setSimilarity(sb);
                break;
	        case 3:
	        	sb = new BM25DocumentLength();
	        	 this.searcher.setSimilarity(sb);
                break;
	        
	        }
	    }
	 
	 //Sets for features using similarity Base classes
	 
	 public void setTF()
	    {
	     System.out.println("Setting term frequency feature");
		 this.setFeatureSimilarityBase(1);
	    }
	 
	 public void setIDF()
	    {
	     System.out.println("Setting inverse document frequency feature");
		 this.setFeatureSimilarityBase(2);
	    }
	 
	 public void setDocLen()
	 {
		 System.out.println("Setting document length feature");
		 this.setFeatureSimilarityBase(3);
		 
	 }
	 
	 
	 
	/**
	 * A raw term frequency factor for use in the lambdaBM25 algorithm to tune the document length feature 
	 * @author VaughanCoder
	 *
	 */
	private class BM25TfSimilarity extends SimilarityBase{ 
		 @Override
	     protected float score(BasicStats stats, float freq, float docLen)
	     {
	         float TF = (1+ (float) Math.log10(freq));
	         return ((TF)/(float)Math.sqrt(docLen));
	     }
	
	     @Override
	     public String toString()
	     {
	         return null;
	     }
	}
	

	/**
	 * A raw IDF factor for use in the lambdaBM25 algorithm to tune the idf feature 
	 * @author VaughanCoder
	 *
	 */
	private class BM25IDFSimilarity extends SimilarityBase{ 
		 @Override
	     protected float score(BasicStats stats, float freq, float docLen)
	     {
	         float IDF =(float) Math.log10(((float) stats.getNumberOfDocuments()/ (float) stats.getDocFreq()));
	         
	         return IDF;
	     }
	
	     @Override
	     public String toString()
	     {
	         return null;
	     }
	}
	
	/**
	 * A raw Document length factor for use in the lambdaBM25 algorithm to tune the document length feature
	 * @author VaughanCoder
	 *
	 */
	private class BM25DocumentLength extends SimilarityBase{
		@Override
	     protected float score(BasicStats stats, float freq, float docLen)
	     {
	         float doclen = (float) Math.sqrt(docLen);
	         return doclen;
	     }
	
	     @Override
	     public String toString()
	     {
	         return null;
	     }
	}
	
}