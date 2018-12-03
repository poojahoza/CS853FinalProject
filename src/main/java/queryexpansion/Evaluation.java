package main.java.queryexpansion;

import java.util.Map;

interface Metric
{
    double map(Map<String, Map<String,Integer>> run);
    double rprec(Map<String, Map<String,Integer>> run);
}

public class Evaluation implements  Metric
{

    private Map<String, Map<String,Integer>> qrel = null;

    private boolean isRelevant(String qID, String docID){
        return qrel.get(qID).containsKey(docID);
    }


    Evaluation(Map<String, Map<String,Integer>> qrel)
    {
        this.qrel= qrel;
    }

    @Override
    public double map(Map<String, Map<String,Integer>> run)
    {
        double map=0.0;
        for(Map.Entry<String,Map<String,Integer>> outer:run.entrySet())
        {
            int total_count=0, relevant_count=0;
            double ap=0.0;

            for(Map.Entry<String,Integer> inner:outer.getValue().entrySet())
            {
                total_count+=1;

                if(isRelevant(outer.getKey(),inner.getKey()))
                {
                    relevant_count+=1;
                    try
                    {
                        ap += (relevant_count/(double)total_count);
                    }catch (ArithmeticException e)
                    {
                        ap+=0;
                    }
                }
            }
            try
            {
                map+= (ap  / (double) qrel.get(outer.getKey()).size());
            }catch (ArithmeticException e)
            {
                map+=0;
            }

        }
        return (map / (double) qrel.size());
    }

    @Override
    public double rprec(Map<String, Map<String, Integer>> run) {
        int number_of_query_processed=0;

        double pATr = 0.0;


        for (Map.Entry<String, Map<String,Integer>> Query : run.entrySet())
        {
            // To have the Precision @ R Computed for each Query.
            double res =0.0;
            //Getting the Key
            String QueryID = Query.getKey();

            // Track of the Queries Processed so Far
            number_of_query_processed+=1;

            //Getting the Relevant count from the Ground Truth
            int relevant_count = qrel.get(QueryID).size();

            //Inner value which holds the Para_ID
            Map<String,Integer> para_id = Query.getValue();

            //Number of ParaID
            int number_of_para_id = para_id.size();

            //Variable to Break the inner loop when it reaches the Relevant_count
            int BreakLoop=(number_of_para_id > relevant_count ) ? relevant_count: number_of_para_id;

            // Keep the current Iteration

            int counter =0;
            int is_relevant_counter = 0;

            for(Map.Entry<String,Integer> P_ID: para_id.entrySet())
            {
                if(isRelevant(QueryID,P_ID.getKey()))
                {
                    is_relevant_counter += 1;

                }
                counter+=1;

                if(counter == BreakLoop)
                {
                    break;
                }
            }
            try
            {
                res = (double) is_relevant_counter / relevant_count;
                pATr+= res;
            }
            catch (ArithmeticException e)
            {
                System.out.println(e.getMessage());
            }

        }
        return (pATr/number_of_query_processed);
    }

}
