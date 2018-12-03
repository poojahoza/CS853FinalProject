package main.java.util;


import java.util.LinkedHashMap;
import java.util.Map;

public class constants
{
    public static String FILE_NAME="";
    public static String DIRECTORY_NAME="";
    public static String ENTITY_DIRECTORY_NAME = "";
    public static String ENTITY_BIGRAM_DIRECTORY_NAME = "";
    public static String DIRECTORY_NAME_WITH_ENTITY_FIELD = "";
    public static String BIGRAM_DIRECTORY = "";
    public static String WINDOW_DIRECTORY = "";
    public static Map<String, Map<String,Integer>> queryDocPair = new LinkedHashMap<String, Map<String, Integer>>();

    public static Map<String, String> methodRunfile = new LinkedHashMap<>();
    public static Map<String, Map<String, Map<String, String[]>>> lmQueryDocPair = new LinkedHashMap<>();
    public static Map<String, Map<String, Map<String, Float>>> sdmQueryDocPair = new LinkedHashMap<>();
    public static Map<String, Map<String, Map<String, Float>>> baseQuerydocPairScore = new LinkedHashMap<>();
    public static Map<String, Map<String, Map<String, Integer>>> baseQuerydocPairRank = new LinkedHashMap<>();
    public static final int windowSize = 6;

    public final static String Prop = "C:\\Users\\amith\\IdeaProjects\\CS853FinalProject\\src\\main\\java\\config.properties";
    //public final static String Prop = "//home//ar1184//CS853FinalProject//src//main//java//config.properties";

    public static String OUTLINE_CBOR="";
    public static String TRAIN_OUTLINE_CBOR="";
    public static String QREL_PATH="";

    public static float lambda = 0.99f;
    public static float Mu = 1000;

    public static String QREL_OUTPUT_PATH="";


    public static Map<String, Map<String,Integer>> queryDocPairRead;
    public static void setIndexFileName(String s)
    {
        FILE_NAME = s;
    }
    public static void setDirectoryName(String d) { DIRECTORY_NAME= d; }
    public static void setBigramDirectory(String d) {BIGRAM_DIRECTORY = d; }
    public static void setWindowDirectory(String d) {WINDOW_DIRECTORY = d;}
    public static void setOutlineCbor(String d)
    {
        OUTLINE_CBOR = d;
    }
    public static void setTrainOutlineCbor(String d)
    {
    	TRAIN_OUTLINE_CBOR = d;
    }
    
    public static void setQrelPath(String d){ QREL_PATH= d;}

    public static void setEntityDirectoryName(String e){ ENTITY_DIRECTORY_NAME = e;}
    public static void setEntityBigramDirectoryName(String e){ ENTITY_BIGRAM_DIRECTORY_NAME = e;}
    public static void setDirectoryNameWithEntityField(String de){ DIRECTORY_NAME_WITH_ENTITY_FIELD = de;}

    public static void setQrelOutputPath(String d){ QREL_OUTPUT_PATH= d;}

}
