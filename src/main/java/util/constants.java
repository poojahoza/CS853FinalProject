package main.java.util;


import java.util.LinkedHashMap;
import java.util.Map;

public class constants
{
    public static String FILE_NAME="";
    public static String DIRECTORY_NAME="";
    public static Map<String, Map<String,Integer>> queryDocPair = new LinkedHashMap<String, Map<String, Integer>>();

    public static String OUTLINE_CBOR="";
    public static String QREL_PATH="";

    public static Map<String, Map<String,Integer>> queryDocPairRead;
    public static void setIndexFileName(String s)
    {
        FILE_NAME = s;
    }
    public static void setDirectoryName(String d)
    {
        DIRECTORY_NAME= d;
    }
    public static void setOutlineCbor(String d)
    {
        OUTLINE_CBOR= d;
    }
    public static void setQrelPath(String d){ QREL_PATH= d;}

}
