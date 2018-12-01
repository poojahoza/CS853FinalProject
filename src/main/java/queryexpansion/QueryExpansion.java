package main.java.queryexpansion;

import java.io.IOException;
import java.util.*;


public class QueryExpansion
{
        private ExpansionUtils EXP = null;
        private BM25 bm25 = null;
        private Map<String,String> OUTLINE = null;
        private Evaluation EVAL=null;
        private Map<String,Map<String,Integer>> qrel=null;


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
            qrel= EXP.getQREL();
            EVAL = new Evaluation(qrel);
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
            System.out.println("Query Expansion => MAP@queryExpansion = "+ EVAL.map(bm25.getRankings(ExpandedTerms)));
            System.out.println("Query Expansion => RPREC@queryExpansion = "+ EVAL.rprec(bm25.getRankings(ExpandedTerms)));
            System.out.println(" ");
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
            EXP.printMessage();
            System.out.println("Called runBM25");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);

            Map<String,Map<String,Integer>> re = bm25.getRankings(OUTLINE);
            System.out.println("Query Expansion =>MAP@BM25 = "+ EVAL.map(re));
            System.out.println("Query Expansion =>RPREC@BM25 = "+ EVAL.rprec(re));
            System.out.println(" ");
            bm25.writeRankings(OUTLINE);
        }

    /**
     * Gets TOP k terms per query term
     * @param k
     */
    private void prfIndividualRunner(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsPerQuery(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            Map<String,Map<String,Integer>> re = bm25.getRankings(ExpandedTerms);
            System.out.println("Query Expansion => MAP@PerQueryExpansion = "+ EVAL.map(re));
            System.out.println("Query Expansion => RPREC@PerQueryExpansion = "+ EVAL.rprec(re));
            System.out.println(" ");
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPrfIndividual(String mName,int k)
        {
            System.out.println("Called runPrfIndividual");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualRunner(k);
        }

    /**
     * Used the TOP k IDF terms as Expanded terms and writes to file
     * @param k
     */

    private void prfIndividualRunnerIDF(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());

            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsPerQueryHighIDF(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }
            Map<String,Map<String,Integer>> re = bm25.getRankings(ExpandedTerms);
            System.out.println("Query Expansion => MAP@IDF = "+ EVAL.map(re));
            System.out.println("Query Expansion => RPREC@IDF = "+ EVAL.rprec(re));
            System.out.println(" ");
            bm25.writeRankings(ExpandedTerms);
        }

        public void runPrfIndividualIDF(String mName,int k)
        {
            System.out.println("Called runPRFIndividualIDF");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualRunnerIDF(k);
        }

    /**
     * THis will execute necessary the Query Expansion using top k highest DF as the expanded terms and
     * then writes to file.
     * @param k
     */

       private void prfIndividualRunnerDF(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());
            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsPerQueryHighDF(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }

            Map<String,Map<String,Integer>> re = bm25.getRankings(ExpandedTerms);
            System.out.println("Query Expansion => MAP@DF = "+ EVAL.map(re));
            System.out.println("Query Expansion => RPREC@DF = "+ EVAL.rprec(re));
            System.out.println(" ");
            bm25.writeRankings(ExpandedTerms);

        }

    /**
     *
     * @param mName
     * @param k
     * Runs the DF method
     */

        public void runPrfIndividualDF(String mName,int k)
            {
                System.out.println("Called runPRFIndividualDF");
                bm25.setmethodName(mName);
                EXP.setMaxTerm(k);
                prfIndividualRunnerDF(k);
            }

    /**
     *
     * @param k
     * Performs Index Elimination by the high IDF terms.
     */
       private void prfIndividualIndexElimination(int k)
        {
            Map<String, String> ExpandedTerms = new LinkedHashMap<>(OUTLINE.size());
            Map<String,Map<String,Integer>> prf = bm25.getRankings(OUTLINE);

            int c=0;

            for(Map.Entry<String,Map<String,Integer>> InitSet: prf.entrySet())
            {
                String expanded= EXP.getTopKTermsIndexElimin(InitSet.getValue(),OUTLINE.get(InitSet.getKey()));
                ExpandedTerms.put(InitSet.getKey(),expanded);
            }

            Map<String,Map<String,Integer>> re = bm25.getRankings(ExpandedTerms);
            System.out.println("Query Expansion => MAP@IndexElimiation = "+ EVAL.map(re));
            System.out.println("Query Expansion => RPREC@IndexElimination  = "+ EVAL.rprec(re));
            System.out.println(" ");
            bm25.writeRankings(ExpandedTerms);
            EXP.printMessage();
        }

        /**
         *
         * @param mName
         * @param k
         * Performs Index Elimination
         */
        public void runPrfIndexElimination(String mName,int k)
        {
            System.out.println("Called prfIndexElimination");
            bm25.setmethodName(mName);
            EXP.setMaxTerm(k);
            prfIndividualIndexElimination(k);
        }


}
