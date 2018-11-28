package main.java;
import java.io.IOException;

import main.java.indexer.BigramIndexBuilder;
import main.java.indexer.IndexBuilder;
import main.java.searcher.SDMSearcher;
import main.java.searcher.base;
import main.java.searcher.Searcher;
import main.java.util.constants;
import main.java.util.Util;
import java.util.Map;

public class projectMain
{
    private static void  usage()
    {
        System.out.println("args[0] --> Paragraph CBOR Absolute Path");
        System.out.println("args[1] --> Outlines CBOR Absolute Path");
        System.out.println("args[2] --> Article  Qrel Absolute Path");
        System.exit(-1 );
    }
    public static void main(String[] args) throws IOException
    {

        if( args.length < 3 )
        {
            usage();
        }

        else
        {


            constants.setIndexFileName(args[0]);
            constants.setDirectoryName(System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file");

            constants.setBigramDirectory(System.getProperty("user.dir")+System.getProperty("file.separator")+"BigramIndexed_file");

            constants.setWindowDirectory(System.getProperty("user.dir")+System.getProperty("file.separator")+"UnBigram_file");

            constants.setOutlineCbor(args[1]);
            constants.setQrelPath(args[2]);

            //Create the new lucene Index
            IndexBuilder defaultIndex = new IndexBuilder();
            defaultIndex.getIndexWriter();

            BigramIndexBuilder BigramIndex = new BigramIndexBuilder();
            BigramIndex.getIndexWriter("BigramIndex");

            BigramIndexBuilder windowIndex = new BigramIndexBuilder();
            windowIndex.getIndexWriter("WindowIndex");

            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

            Searcher BM25Searcher = new Searcher();
            BM25Searcher.writeRankings(p);

            System.out.println("-----------------------------------------------------------------------------");

            SDMSearcher SDM_Laplace = new SDMSearcher("SDM_with_Laplace_smoothing");
            SDM_Laplace.setLaplaceSDM();

            SDMSearcher SDM_JM = new SDMSearcher("SDM_with_JM_smoothing");
            SDM_JM.setJMSDM();

            SDMSearcher SDM_Drichlet = new SDMSearcher("SDM_with_Drichlet_smoothing");
            SDM_Drichlet.setDrichletSDM();

            SDMSearcher SDM_BM25 = new SDMSearcher("SDM_with_BM25");
            SDM_BM25.setBM25SDM();
















        }

    }
}