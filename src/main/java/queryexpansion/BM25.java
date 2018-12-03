package main.java.queryexpansion;
import main.java.searcher.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

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

public class BM25 extends Searcher
{

        /*Data it holds after performing the private search*/
        private Map<String, Map<String,Integer>> ranks;
        private int k;


        public BM25() throws IOException
        {
            super();
            k =100;
            this.ranks = new LinkedHashMap<String, Map<String, Integer>>();
        }

        public BM25(int k) throws IOException
        {
            super();
            this.ranks = new LinkedHashMap<String, Map<String, Integer>>();
            this.k=k;
            output_file_name = "BM25";
        }

        public void setmethodName(String mName)
        {
            this.methodName= mName;
            this.output_file_name = "output_"+ methodName+"_ranking.txt";
        }

        public IndexSearcher getSearcher()
        {
            return searcher;
        }


        public Map<String,Map<String,Integer>> getRankings(Map<String,String> out)
        {
            if(ranks == null)
            {
                this.runRanking(out);
            }
            else {
                ranks.clear();
                this.runRanking(out);
            }
            return ranks;
        }

        public int getK()
        {
            return k;
        }

        public String getMethodname()
        {
            return this.methodName;
        }

          private void createRankingQueryDocPair(String outer_key, String inner_key, Integer docID)
          {
                if(ranks.containsKey(outer_key))
                {
                    Map<String, Integer> extract = ranks.get(outer_key);
                    extract.put(inner_key, docID);
                }
                else
                {
                    Map<String,Integer> temp = new LinkedHashMap<>();
                    temp.put(inner_key, docID);
                    ranks.put(outer_key,temp);
                }
            }

    /**
     *
     * @param scoreDocs
     * @param queryId
     * @throws IOException
     * @apiNote This contains MAP of query ID, paraID and the DOC ID associated with Para.
     */

    private void updateRankings(ScoreDoc[] scoreDocs, String queryId) throws IOException
    {
                for(ScoreDoc s:scoreDocs)
                {
                    Document rankedDoc = searcher.doc(s.doc);
                    String paraId = rankedDoc.getField("id").stringValue();
                    createRankingQueryDocPair(queryId, paraId, s.doc);
                }
    }

        private void runRanking(Map<String,String> out)
        {
            for(Map.Entry<String,String> m:out.entrySet())
            {
                try
                {
                    TopDocs topDocuments = this.performSearch(m.getValue(),this.k);
                    ScoreDoc[] scoringDocuments = topDocuments.scoreDocs;
                    this.updateRankings(scoringDocuments, m.getKey());
                }
                catch (ParseException | IOException io)
                {
                        System.out.println(io.getMessage());
                }

            }
        }

    	    protected List<String> getRankings(ScoreDoc[] scoreDocs, String queryId)
	    	    throws IOException
            {

                List<String> rankings = new ArrayList<String>();


                for(int ind=0; ind<scoreDocs.length; ind++){

                    //Get the scoring document
                    ScoreDoc scoringDoc = scoreDocs[ind];

                    //Create the rank document from searcher
                    Document rankedDoc = searcher.doc(scoringDoc.doc);
    //				System.out.println(searcher.explain(queryObj, scoringDoc.doc));

                    //Print out the results from the rank document
                    String docScore = String.valueOf(scoringDoc.score);
                    String paraId = rankedDoc.getField("id").stringValue();
                    //String paraBody = rankedDoc.getField("body").stringValue();

                    String paraRank = String.valueOf(ind+1);
                    rankings.add(queryId + " Q0 " + paraId + " " + paraRank + " " + docScore + " "+teamName + "-" + methodName);
                    createRankingQueryDocPair(queryId, paraId, Integer.valueOf(paraRank));
                }

                return rankings;
	    }

        public TopDocs returnTopDocs(String qID)
        {
                TopDocs t = null;
                try
                {
                     t = this.performSearch(qID,this.k);
                }
                catch (ParseException | IOException io)
                {
                    System.out.println(io.getMessage());
                }

            return t;
        }

        public void setK(int k)
        {
            this.k = k;
        }

        public String getDocument(int docID)
        {
            String docString=null;
            try
            {
                Document rankedDoc = searcher.doc(docID);
                docString = rankedDoc.getField("body").stringValue();
            }
            catch (IOException io)
            {
                    System.out.println(io.getMessage());
            }
            return docString;
        }

    //@Override
    public void writeRankings(Map<String,String> p)
    {
        Path file = Paths.get(output_file_name);

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
                TopDocs searchDocs = this.performSearch(m.getValue(), 100);
                ScoreDoc[] scoringDocuments = searchDocs.scoreDocs;
                List<String> formattedRankings = this.getRankings(scoringDocuments, m.getKey());
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
    }

}
