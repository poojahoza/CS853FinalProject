package main.java;

import java.io.IOException;


import main.java.queryexpansion.QueryExpansion;
import main.java.util.constants;
import main.java.util.Util;
import java.util.Map;

public class projectMain
{
    private static void  usage()
    {
        System.out.println("args[0] --> Outlines CBOR Absolute Path");
        System.out.println("args[1] --> Article  Qrel Absolute Path");
        System.exit(-1 );
    }
    public static void main(String[] args) throws IOException
    {
        String query_exp_dest;
        if( args.length < 2 )
        {
            usage();
        }
        else
        {
            //dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            query_exp_dest="//home//team3//indexed_file";
            constants.setDirectoryName(query_exp_dest);

            constants.setOutlineCbor(args[0]);
            constants.setQrelPath(args[1]);


            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

            QueryExpansion q= new QueryExpansion(p);

            q.runBM25("BM25",10);
            q.runPRF("PRF",10);
            q.runPrfIndividual("PRF_PER_QUERY_TERM",10);
            q.runPrfIndividualIDF("PRF_IDF",100);
            q.runPrfIndividualDF("PRF_DF",100);
            q.runPrfIndexElimination("Index_ELIM",100);
        }
    }
}