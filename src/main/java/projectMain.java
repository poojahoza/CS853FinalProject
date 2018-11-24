package main.java;
import java.io.IOException;

import main.java.indexer.IndexBuilder;
import main.java.searcher.SDMSearcher;
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

            constants.setUnbigramDirectory(System.getProperty("user.dir")+System.getProperty("file.separator")+"UnBigram_file");

            constants.setOutlineCbor(args[1]);
            constants.setQrelPath(args[2]);

            //Create the new lucene Index
            IndexBuilder defaultIndex = new IndexBuilder();
            defaultIndex.getIndexWriter("defaultIndex");

            IndexBuilder BigramIndex = new IndexBuilder();
            BigramIndex.getIndexWriter("BigramIndex");

            IndexBuilder windowIndex = new IndexBuilder();
            windowIndex.getIndexWriter("UNBigramIndex");

            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

            Searcher BM25Searcher = new Searcher();
            BM25Searcher.writeRankings(p);

            System.out.println("-----------------------------------------------------------------------------");

            SDMSearcher uniLapalce = new SDMSearcher("UnigramLaplace");
            uniLapalce.setUnigramLaplace();
            uniLapalce.writeRankings(p);

            SDMSearcher uniJM = new SDMSearcher("UnigramJM");
            uniJM.setUnigramJM();
            uniJM.writeRankings(p);

            SDMSearcher uniDrichlet = new SDMSearcher("UnigramDrichlet");
            uniDrichlet.setUnigramDirichlet();
            uniDrichlet.writeRankings(p);

            SDMSearcher BiLaplace = new SDMSearcher("BigramLaplace");
            BiLaplace.setBigramLaplace();
            BiLaplace.writeRankings(p);

            SDMSearcher BijM = new SDMSearcher("BigramJM");
            BijM.setBigramJM();
            BijM.writeRankings(p);

            SDMSearcher BiDrichlet = new SDMSearcher("BigramDrichlet");
            BiDrichlet.setBigramDirichlet();
            BiDrichlet.writeRankings(p);

            SDMSearcher uniBM25 = new SDMSearcher("UnigraBM25");
            uniBM25.setUnigramBM25();
            uniBM25.writeRankings(p);

            SDMSearcher BiBM25 = new SDMSearcher("BigramBM25");
            BiBM25.setBigramBM25();
            BiBM25.writeRankings(p);

            SDMSearcher UNBiLaplace = new SDMSearcher("UNBigramLaplace");
            UNBiLaplace.setUNBigramLaplace();
            UNBiLaplace.writeRankings(p);

            SDMSearcher UNBijm = new SDMSearcher("UNBigramJM");
            UNBijm.setUNBigramJM();
            UNBijm.writeRankings(p);

            SDMSearcher UNBiDrichlet = new SDMSearcher("UNBigramDrichlet");
            UNBiDrichlet.setUNBigramDritchlet();
            UNBiDrichlet.writeRankings(p);

            SDMSearcher UNBiBM25 = new SDMSearcher("UNBigramBM25");
            UNBiBM25.setUNBigramBM25();
            UNBiBM25.writeRankings(p);

        }

    }
}