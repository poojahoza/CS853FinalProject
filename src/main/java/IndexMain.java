package main.java;

import java.io.IOException;

import main.java.LuceneIndex.LuceneIndexer;
import main.java.LuceneSearch.LuceneSearcher;


/**
 * Main Class to handle the running of the method
 * @author Pooja Oza
 *
 */
public class IndexMain
{
    /**
     * Lets user know they did not use the correct file path
     */
    private static void  usage()
    {
        System.out.println("args[0] --> Paragraph CBOR Absolute Path");
        System.out.println("args[1] --> Outlines CBOR Absolute Path");
        System.out.println("args[2] --> Article  Qrel Absolute Path");
        System.exit(-1 );
    }

    /**
     * Run the queries
     * @param args file path for the corpus
     * @throws IOException if things go wrong
     */

    public static void main(String[] args) throws IOException
    {
        String dest;
        if( args.length < 3 )
        {
            usage();
        }

        else
        {
            dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            LuceneConstants.setIndexFileName(args[0]);
            LuceneConstants.setDirectoryName(dest);

            LuceneConstants.setOutlineCbor(args[1]);
            LuceneConstants.setQrelPath(args[2]);

            //Create the new lucene Index
            LuceneIndexer l = new LuceneIndexer();
            l.getIndexWriter();

            Map<String,String> p = LuceneUtil.readOutline(LuceneConstants.OUTLINE_CBOR);

            LuceneSearcher BM25Searcher = new LuceneSearcher();

            System.out.println("-----------------------------------------------------------------------------");

        }

    }

}