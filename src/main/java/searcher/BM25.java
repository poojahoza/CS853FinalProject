package main.java.searcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

import java.util.LinkedHashMap;

import java.util.Map;

public class BM25 extends Searcher
{

        /*Data it holds after performing the private search*/
        private Map<String, Map<String,Integer>> ranks;
        private int k;
        private String methodname = "BM25";


        public BM25() throws IOException
        {
            super();
            k=100;
            this.ranks = new LinkedHashMap<String, Map<String, Integer>>();
        }

        public BM25(int k) throws IOException
        {
            super();
            this.ranks = new LinkedHashMap<String, Map<String, Integer>>();
            this.k=k;
            output_file_name = "BM25";
        }

        public Map<String,Map<String,Integer>> getRankings()
        {
            return ranks;
        }

        public Map<String,Map<String,Integer>> getRankings(Map<String,String> out)
        {
            this.runRanking(out);
            return ranks;
        }

        public int getK()
        {
            return k;
        }

        public String getMethodname()
        {
            return methodname;
        }

          private void createRankingQueryDocPair(String outer_key, String inner_key, Integer rank)
          {
                if(ranks.containsKey(outer_key))
                {
                    Map<String, Integer> extract = ranks.get(outer_key);
                    extract.put(inner_key, rank);
                }
                else
                {
                    Map<String,Integer> temp = new LinkedHashMap<String,Integer>();
                    temp.put(inner_key, rank);
                    ranks.put(outer_key,temp);
                }
            }

            private void updateRankings(ScoreDoc[] scoreDocs, String queryId) throws IOException
            {
                for(int ind=0; ind<scoreDocs.length; ind++)
                {

                    ScoreDoc scoringDoc = scoreDocs[ind];

                    //Create the rank document from searcher
                    Document rankedDoc = searcher.doc(scoringDoc.doc);

                    String docScore = String.valueOf(scoringDoc.score);
                    String paraId = rankedDoc.getField("id").stringValue();
                    String paraRank = String.valueOf(ind+1);
                    createRankingQueryDocPair(queryId, paraId, Integer.valueOf(paraRank));
                }
            }

        public void runRanking(Map<String,String> out)
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

}
