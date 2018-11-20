package main.java.queryexpansion;


import main.java.searcher.BM25;

import java.io.IOException;
import java.util.*;

public class QueryExpansion
{
        private ExpansionUtils EXP = null;
        private BM25 bm25 = null;
        private Map<String,String> OUTLINE = null;


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
        }

        public QueryExpansion(Map<String,String> OUTLINE)
        {
            this();
            this.OUTLINE = OUTLINE;
        }


        private void prfRunner(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);
            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded = EXP.getTopKTerms(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPRF(String mName,int k)
        {
            System.out.println("Called runPRF");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfRunner(k);
        }

        public void runBM25(String mName,int k)
        {
            System.out.println("Called runBM25");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            bm25.writeRankings(OUTLINE);
        }

        private void prfIndividualRunner(int k)
        {


            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsPerQuery(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPrfIndividual(String mName,int k)
        {
            System.out.println("Called runPrfIndividual");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualRunner(k);
        }

        private void prfIndividualRunnerIDF(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsPerQueryHighIDF(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPrfIndividualIDF(String mName,int k)
        {
            System.out.println("Called runPRFIndividualIDF");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualRunnerIDF(k);
        }


        private void prfIndividualRunnerDF(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            int c=0;
            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {

                String expanded= EXP.getTopKTermsPerQueryHighDF(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPrfIndividualDF(String mName,int k)
        {
            System.out.println("Called runPRFIndividualDF");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualRunnerDF(k);
        }

}
