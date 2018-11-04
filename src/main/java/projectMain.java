package main.java;
import java.io.IOException;

import main.java.indexer.IndexBuilder;
import main.java.searcher.BM25;
import main.java.searcher.Searcher;
import main.java.util.constants;
import main.java.util.Util;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;


import java.util.LinkedHashMap;
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
        String dest;
        if( args.length < 3 )
        {
            usage();
        }
        else
        {
            dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            constants.setIndexFileName(args[0]);
            constants.setDirectoryName("indexed_file");

            constants.setOutlineCbor(args[1]);
            constants.setQrelPath(args[2]);

            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);



            BM25 b = new BM25(100);

            Map<String,Integer> knn = new LinkedHashMap<>();

            for(Map.Entry<String, String> m:p.entrySet())
            {
                TopDocs t = b.returnTopDocs(m.getValue());
                ScoreDoc[] s =t.scoreDocs;
                for(ScoreDoc val: s)
                {
                    String ans = b.getDocument(val.doc);

                    String[] split = ans.split(" ");

                    for(String ss:split)
                    {
                       knn.put(ss,0);
                    }
                }
            }



        }

    }
}