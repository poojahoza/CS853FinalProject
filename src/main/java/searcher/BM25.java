package main.java.searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
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

        public void setmethodName(String mName)
        {
            this.methodName= mName;
            this.output_file_name = "output_"+ methodName+"_ranking.txt";
        }

        public IndexSearcher getSearcher()
        {
            return searcher;
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
     * @apiNote Returns the DocID associated with the paraID
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

}
