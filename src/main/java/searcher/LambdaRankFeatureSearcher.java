package main.java.searcher;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

import java.io.IOException;

/**
 * 
 * An implementation of the Searcher class that utilizes the features present in BM25
 *
 */
public class LambdaRankFeatureSearcher extends Searcher{
	
	/** 
	 * Constructor inherited from Searcher
	 */
	 public LambdaRankFeatureSearcher() throws IOException
     {
         super();
      
     }
	 
	 /**
	  * Takes in methodname
	  * @param methodName
	  * @throws IOException
	  */
	 public LambdaRankFeatureSearcher(String methodName) throws IOException
	    {
	        this();
	        this.methodName= "LambdaRank "+methodName;
	    }
	 
	 
	 private void  setFeatureSimilarityBase(int tfidf_variant)
	    {

	        SimilarityBase sb;
	        switch(tfidf_variant)
	        {
	        //Switch on BM25 Similariy type 
	        
	        //TF
	        case 1:
	        	sb = new BM25TfSimilarity();
	        	 this.searcher.setSimilarity(sb);
                 break;
	        //IDF
	        case 2:
	        	sb = new BM25IDFSimilarity();
	        	 this.searcher.setSimilarity(sb);
                break;
	        
            //Doc Length
	        case 3:
	        	sb = new BM25DocumentLength();
	        	 this.searcher.setSimilarity(sb);
                break;
	        
	        }
	    }
	 
	 //Set Term Frequency as the similarity type
	 public void setTF()
	    {
	     System.out.println("Setting term frequency feature");
		 this.setFeatureSimilarityBase(1);
	    }
	 
	 //Set IDF as the similarity type
	 public void setIDF()
	    {
	     System.out.println("Setting inverse document frequency feature");
		 this.setFeatureSimilarityBase(2);
	    }
	 
	 //Set Document Length as the similarity type
	 public void setDocLen()
	 {
		 System.out.println("Setting document length feature");
		 this.setFeatureSimilarityBase(3);
		 
	 }
	 
	 
	 
	/**
	 * A raw term frequency factor for use in the lambdaBM25 algorithm to tune the document length feature 
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
	 */
	private class BM25IDFSimilarity extends SimilarityBase{ 
		 @Override
	     protected float score(BasicStats stats, float freq, float docLen)
	     {
	         float IDF =(float) Math.log10(((float) stats.getNumberOfDocuments() - (float) stats.getDocFreq()+.5f)/ ((float) stats.getDocFreq()+.5f));
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