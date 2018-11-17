package main.java.queryexpansion;


import main.java.searcher.BM25;

import java.io.IOException;
import java.util.*;

public class QueryExpansion
{
        private String METHOD_NAME="";
        private ExpansionUtils EXP = null;
        private BM25 bm25 = null;
        private Map<String,String> OUTLINE = null;
        private ArrayList<String> STOP_WORDS = null;
        /*
            RANK_K to retrieve the initial candidate set
        */
        private final int RANK_K = 100;



        private  QueryExpansion()
        {
            try
            {
                bm25 = new BM25();
            }catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            EXP = new ExpansionUtils();
            STOP_WORDS = new ArrayList<>(Arrays.asList(EXP.getStopList()));
        }

        public QueryExpansion(String mName,Map<String,String> OUTLINE)
        {
            this();
            this.OUTLINE = OUTLINE;
            this.METHOD_NAME = mName;
        }



        private String nearestWords(String queryTerm) throws IOException
        {
            StringBuilder s= new StringBuilder();
            String[] tokens = queryTerm.split(" ");
            for(String tok:tokens)
            {
                if(!STOP_WORDS.contains(tok))
                {
                        System.out.println(tok + "----->"+ EXP.returnIDF(tok));
                }

            }
            return null;
        }

        private Map<String,String> returnNN() throws IOException
        {
            Map<String,String> newQuery = new LinkedHashMap<>();
            for(Map.Entry<String,String> q :OUTLINE.entrySet())
            {
                    nearestWords(q.getValue().toLowerCase());
            }
            return newQuery;
        }

        public void KNN() throws IOException
        {
            returnNN();
        }

    /**
     * @apiNote Run the initial query using BM25 and search for the terms and returns the expanded Query items
     * in <QueryID, Expanded Terms> HashMap
     */
        private void prfRunner(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded = EXP.getTopKTerms(InitSet.getValue(),k,OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }

            for(Map.Entry<String,String> g:ExpandedTerms.entrySet())
            {
                System.out.println("Query ID"+ g.getKey()+ "   ->  "+ g.getValue());

            }


        }
        public void PRF(int k)
        {
            bm25.setK(RANK_K);
            prfRunner(k);
        }

}
