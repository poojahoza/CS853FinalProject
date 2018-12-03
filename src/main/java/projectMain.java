package main.java;

import java.io.IOException;

import main.java.indexer.IndexBuilder;
import main.java.queryexpansion.QueryExpansion;
import main.java.searcher.LambdaRank;
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
        String dest;
        if( args.length < 2 )
        {
            usage();
        }
        else
        {
            //dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            //dest="//home//team3//indexed_file";
        	dest = "C:\\Users\\VaughanCoder\\GitWorkspace\\CS853Docs";
            constants.setDirectoryName(dest);

            /*constants.setOutlineCbor(args[0]);
            constants.setQrelPath(args[1]);
            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);*/

           /* QueryExpansion q= new QueryExpansion(p);

            q.runBM25("BM25",10);
            q.runPRF("PRF",10);
            q.runPrfIndividual("PRF_PER_QUERY_TERM",10);
            q.runPrfIndividualIDF("PRF_IDF",100);
            q.runPrfIndividualDF("PRF_DF",10);*/
            
        	constants.setIndexFileName(args[0]);

            constants.setOutlineCbor(args[1]);
      
            constants.setQrelPath(args[2]);
            constants.setQrelOutputPath(args[3]);
            constants.setTestOutlineCbor(args[4]);
			Map<String,Map<String,Integer>> qrel = Util.createQrelMap(constants.QREL_PATH);

			/*IndexBuilder Index1 = new IndexBuilder();
			Index1.getIndexWriter();
*/
			
			//create the new lucene Index for bigram
		   /* LuceneIndexer Index2 = new LuceneIndexer();
		    Index2.getIndexWriter(true);*/
            
            
            LambdaRank LR = new LambdaRank(qrel);
		       LR.generateRanklibFile();

        }
    }
}