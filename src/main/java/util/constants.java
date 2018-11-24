package main.java.util;


import java.util.LinkedHashMap;
import java.util.Map;

public class constants
{
    public static String FILE_NAME="";
    public static String DIRECTORY_NAME = "";
    public static String BIGRAM_DIRECTORY = "";
    public static String WINDOWED_DIRECTORY = "";
    public static Map<String, Map<String,Integer>> queryDocPair = new LinkedHashMap<String, Map<String, Integer>>();

    public static String OUTLINE_CBOR="";
    public static String QREL_PATH="";
    public static float lambda = 0.9f;
    public static float Mu = 1000;

    public static Map<String, Map<String,Integer>> queryDocPairRead;
    public static void setIndexFileName(String s)
    {
        FILE_NAME = s;
    }
    public static void setDirectoryName(String d) { DIRECTORY_NAME= d; }
    public static void setBigramDirectory(String d) {BIGRAM_DIRECTORY = d; }
    public static void setWindoewsDirectory(String d) {WINDOWED_DIRECTORY = d;}
    public static void setOutlineCbor(String d)
    {
        OUTLINE_CBOR= d;
    }
    public static void setQrelPath(String d){ QREL_PATH= d;}
}
