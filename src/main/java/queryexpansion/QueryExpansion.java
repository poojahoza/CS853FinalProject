package main.java.queryexpansion;

import main.java.searcher.BM25;
import java.io.IOException;
import java.util.*;

public class QueryExpansion
{
        private String METHOD_NAME="";
        private Map<String, ArrayList<Double>> GLOVE = null;
        private ExpansionUtils EXP = null;
        private Properties PROP= null;
        private BM25 bm25 =null;
        private Map<String,String> OUTLINE = null;
        private ArrayList<String> STOP_WORDS = null;

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
            PROP = EXP.returnProp();
            STOP_WORDS = new ArrayList<>(Arrays.asList(EXP.getStopList()));
        }

        public QueryExpansion(String mName,Map<String,String> OUTLINE)
        {
            this();
            this.OUTLINE = OUTLINE;
            this.METHOD_NAME = mName;
        }

        private void readVector()
        {
            GLOVE = EXP.readWordVectors(PROP.getProperty("glovefile-50d"));
        }


        void getHighestIDF(TreeMap<Float,String> tree)
        {


        }


        private String nearestWords(String queryTerm) throws IOException
        {
            StringBuilder s= new StringBuilder();
            String[] tokens = queryTerm.split(" ");
            for(String tok:tokens)
            {
                if(!STOP_WORDS.contains(tok))
                {
                    System.out.println(tok +"-----> " + EXP.returnIDF(tok));
                }
            }

            return null;

        }

        private Map<String,String> returnNN() throws IOException
        {
            Map<String,String> newQuery = new LinkedHashMap<>();
            if(GLOVE==null) readVector();

            for(Map.Entry<String,String> q :OUTLINE.entrySet())
            {
                    nearestWords(q.getValue().toLowerCase());
            }
            return newQuery;
        }

        public void KNN() throws IOException
        {
            Map<String,String> newQ = returnNN();
        }





}
