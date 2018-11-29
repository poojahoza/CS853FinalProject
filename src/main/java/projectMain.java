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

            constants.methodRunfile.put("BM25Searcher" , BM25Searcher.getOutputFileName());

            System.out.println("-----------------------------------------------------------------------------");

            base uniLapalce = new base("UnigramLaplace");
            uniLapalce.setUnigramLaplace();
            uniLapalce.writeRankings(p);

            constants.methodRunfile.put("UnigramLaplace", uniLapalce.getOutputFileName());

            base BiLaplace = new base("BigramLaplace");
            BiLaplace.setBigramLaplace();
            BiLaplace.writeRankings(p);

            constants.methodRunfile.put("BigramLaplace", BiLaplace.getOutputFileName());

            base windowLaplace = new base("WindowLaplace");
            windowLaplace.setWindowLaplace();
            windowLaplace.writeRankings(p);

            constants.methodRunfile.put("WindowLaplace", windowLaplace.getOutputFileName());

            base uniJM = new base("UnigramJM");
            uniJM.setUnigramJM();
            uniJM.writeRankings(p);

            constants.methodRunfile.put("UnigramJM", uniJM.getOutputFileName());

            base BiJM = new base("BigramJM");
            BiJM.setBigramJM();
            BiJM.writeRankings(p);

            constants.methodRunfile.put("BigramJM", BiJM.getOutputFileName());

            base windowJm = new base("WindowJM");
            windowJm.setWindowJM();
            windowJm.writeRankings(p);

            constants.methodRunfile.put("WindowJM", windowJm.getOutputFileName());

            base uniDrichlet = new base("UnigramDrichlet");
            uniDrichlet.setUnigramDirichlet();
            uniDrichlet.writeRankings(p);

            constants.methodRunfile.put("UnigramDrichlet" , uniDrichlet.getOutputFileName());

            base BiDrichlet = new base("BigramDrichlet");
            BiDrichlet.setBigramDirichlet();
            BiDrichlet.writeRankings(p);

            constants.methodRunfile.put("BigramDrichlet", BiDrichlet.getOutputFileName());

            base windowDrichlet = new base("WindowDrichlet");
            windowDrichlet.setWindowDritchlet();
            windowDrichlet.writeRankings(p);

            constants.methodRunfile.put("WindowDrichlet", windowDrichlet.getOutputFileName());

            base BiBM25 = new base("BigramBM25");
            BiBM25.setBigramBM25();
            BiBM25.writeRankings(p);

            constants.methodRunfile.put("BigramBM25", BiBM25.getOutputFileName());

            base windowBM25 = new base("WindowBM25");
            windowBM25.setWindowBM25();
            windowBM25.writeRankings(p);

            constants.methodRunfile.put("WindowBM25", windowBM25.getOutputFileName());


            SDMSearcher SDM_Laplace = new SDMSearcher("SDM_with_Laplace_smoothing");
            SDM_Laplace.setLaplaceSDM();

            SDMSearcher SDM_JM = new SDMSearcher("SDM_with_JM_smoothing");
            SDM_JM.setJMSDM();

            SDMSearcher SDM_Drichlet = new SDMSearcher("SDM_with_Drichlet_smoothing");
            SDM_Drichlet.setDrichletSDM();

            SDMSearcher SDM_BM25 = new SDMSearcher("SDM_with_BM25");
            SDM_BM25.setBM25SDM();

            SDMSearcher SDM_LaplaceRR = new SDMSearcher("SDM_with_Laplace_smoothing_reverse_rank");
            SDM_LaplaceRR.setLaplaceSDMRR();

            SDMSearcher SDM_JMRR = new SDMSearcher("SDM_with_JM_smoothing_reverse_rank");
            SDM_JMRR.setJMSDMRR();

            SDMSearcher SDM_DrichletRR = new SDMSearcher("SDM_with_Drichlet_smoothing_reverse_rank");
            SDM_DrichletRR.setDrichletSDMRR();

            SDMSearcher SDM_BM25RR = new SDMSearcher("SDM_with_BM25_reverse_rank");
            SDM_BM25RR.setBM25SDMRR();














        }

    }
}