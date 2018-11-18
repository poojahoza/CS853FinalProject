package main.java;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import main.java.queryexpansion.ExpansionUtils;
import main.java.queryexpansion.QueryExpansion;
import main.java.util.constants;
import main.java.util.Util;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

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

            QueryExpansion q= new QueryExpansion(p);
            q.runPRF("PRF",10);
            q.runPrfIndividual("PRF_Individual",10);

        }
    }
}