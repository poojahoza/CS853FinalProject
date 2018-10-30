package main.java;

import java.io.IOException;

import main.java.indexer.IndexBuilder;
import main.java.searcher.Searcher;
import main.java.util.constants;
import main.java.util.Util;

/**
 * Main Class to handle the running of the method
 * @author Pooja Oza
 *
 */

public class projectMain

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
                constants.setIndexFileName(args[0]);
                constants.setDirectoryName(dest);

                constants.setOutlineCbor(args[1]);
                constants.setQrelPath(args[2]);

                //Create the new lucene Index
                Indexer l = new IndexBuilder();
                l.getIndexWriter();

                Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

                Searcher BM25Searcher = new Searcher();
                BM25Searcher.writeRankings();

                System.out.println("-----------------------------------------------------------------------------");

            }

        }
}
