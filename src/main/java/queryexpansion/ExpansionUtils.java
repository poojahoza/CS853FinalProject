package main.java.queryexpansion;

/*Import statements from the CS853 package*/

import main.java.searcher.BM25;
import main.java.util.Util;
import main.java.util.constants;
import org.apache.lucene.index.*;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.store.FSDirectory;

/*Import statements from the java*/
import java.io.*;

import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


public class ExpansionUtils
{
        private IndexReader indexReader  = null;
        private BM25 EXP_BM25= null;
        //private Properties PROP= null;
        private Map<String, ArrayList<Double>> GLOVE = null;
        private Map<String,Map<String,Integer>> QREL=null;

        /*some K values*/
        private int MAX_DOC = 5;
        private int MAX_TERM=10;
        private int MAX_IDF_TERM=5;

        /* PATH to GLOVE FILES*/
        private final String glovefile_50d="//home//team3//glove_word_embeddings//glove.6B.50d.txt";
        private final String glovefile_100d="//home//team3//glove_word_embeddings//glove.6B.100d.txt";
        private final String glovefile_200d="//home//team3//glove_word_embeddings//glove.6B.200d.txt";
        private final  String glovefile_300d="//home//team3//glove_word_embeddings//glove.6B.300d.txt";

//    private final String glovefile_50d="D:\\glove.6B\\glove.6B.50d.txt";
//    private final String glovefile_100d="D:\\glove.6B\\glove.6B.100d.txt";
//    private final String glovefile_200d="D:\\glove.6B\\glove.6B.200d.txt";
//    private final  String glovefile_300d="D:\\glove.6B\\glove.6B.300d.txt";

        private  String[] array = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already",
                  "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway",
                  "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind",
                  "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could",
                  "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty",
                  "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fifty", "fill", "find", "fire", "first", "five",
                  "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence",
                  "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc",
                  "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile",
                  "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next",
                  "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other",
                  "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed",
                  "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something",
                  "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there",
                  "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout",
                  "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was",
                  "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether",
                  "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours",
                  "yourself", "yourselves", "the"};

        private ArrayList<String> STOP_WORDS = new ArrayList<>(Arrays.asList(array));

        public ExpansionUtils()
        {
            indexReader = getIndexReader();
            try
            {
                EXP_BM25 = new BM25();
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            //PROP = this.returnProp();
            readQREL();
        }

        void printMessage()
        {
            System.out.println("//////////////////////////////////////////////////////////////////////////");
            System.out.println("////////////////////////// Query Expansion ///////////////////////////////");
            System.out.println("//////////////////////////////////////////////////////////////////////////");
        }

        void setMaxTerm(int max)
        {
            this.MAX_TERM = max;
        }

        void readQREL()
        {
            QREL = Util.readRunFile(constants.QREL_PATH);
        }


        Map<String,Map<String,Integer>> getQREL()
        {
            return QREL;
        }

    /**
     * Change the MAX_DOC that needs to be processed in PRF
     * @param max
     */
    void setMaxDoc(int max)
        {
            this.MAX_DOC = max;
        }

    /**
     * Read into the HashMap
     */
    private void readVector()
        {
            System.out.println("Loading Wording Vector......");
            //GLOVE = this.readWordVectors(PROP.getProperty("glovefile-200d"));
            GLOVE = this.readWordVectors(glovefile_200d);
        }

    /**
     * Any method that needs Word Vectors calls this method to ensure Word embeddings are loaded in memory.
     */
    private void ensureLoadGlove()
        {
            if(GLOVE == null)
            {
                this.readVector();
            }
        }

    /**
     *  Reads the Word Vector, given any filename dimension
     * @param fname
     * @return
     */

    private Map<String, ArrayList<Double>> readWordVectors(String fname)
        {

           BufferedReader br = null;
           Map<String, ArrayList<Double>> vector = new LinkedHashMap<>();
           try {
               br = new BufferedReader(new FileReader(new File(fname)));
           } catch (FileNotFoundException e) {
               System.out.println(e.getMessage());
           }

           String line;
           try {
               while ((line = br.readLine()) != null) {
                   String[] vec = line.split(" ");
                   ArrayList<Double> arr = new ArrayList<>();
                   for (String s : vec) {
                       if (!s.equals(vec[0])) {
                           arr.add(Double.parseDouble(s));
                       }
                   }
                   vector.put(vec[0], arr);
               }
           } catch (NullPointerException | IOException n) {
               System.out.println(n.getMessage());
           }
           return vector;
       }

    /**
     * Helper method
     * @param vec
     */

    private void DisplayVectors(Map<String,ArrayList<Double>> vec)
        {
            for(Map.Entry<String,ArrayList<Double>> m:vec.entrySet())
            {
                System.out.println(m.getKey());
                int i=0;
                for(Double A:m.getValue())
                {
                    ++i;
                    System.out.println(i+"<--------->"+A);
                }
            }
        }

    /**
     *
     * @param vec
     * Helper method
     */

    public void DisplayRunResult(Map<String,String> vec)
        {
            for(Map.Entry<String,String> m:vec.entrySet())
            {
                System.out.println(m.getKey()+ "---->"+ m.getValue());
            }
        }

    /**
     *
     * @param v1
     * @param v2
     * @return Dot product without normalization
     */

        private double DotProduct(ArrayList<Double> v1,ArrayList<Double> v2)
        {
            assert(v1.size() == v2.size());
            double val = 0.0;

            for(int i=0;i< v1.size();i++)
            {
                val+=  v1.get(i) * v2.get(i);
            }
            return val;
        }

    /**
     *
     * @param v1
     * @param v2
     * @return Computes the Cosine Similarity by taking two Vectors as input. This is normalized version.
     */

       private double CosineSimilarity(ArrayList<Double> v1,ArrayList<Double> v2)
        {
            assert(v1.size() == v2.size());
            double val = 0.0;
            int size = v1.size();
            double x_vector=0.0;
            double y_vector=0.0;

            for(int i=0;i< size ;i++)
            {
                val+=  v1.get(i) * v2.get(i);
                x_vector+= Math.pow(v1.get(i),2);
                y_vector+= Math.pow(v2.get(i),2);
            }
            return val/(Math.sqrt(x_vector) * Math.sqrt(y_vector));
        }

    /**
     *
     * @return Prop value for the config file
     */

    private Properties returnProp()
        {
            Properties prop = new Properties();
            InputStream input = null;

            try {
                input = new FileInputStream(constants.Prop);
                prop.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return prop;
        }

    /**
     *
     * @return Returns the IndexReader object, this is needed to access all the information from the Index
     */
    private IndexReader getIndexReader()
         {
            IndexReader index =null;
            try
            {
                index = DirectoryReader.open(FSDirectory.open(Paths.get(constants.DIRECTORY_NAME)));
            }catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            return index;
        }

    /**
     *
     * @param myTerm
     * @param queryBoost
     * @return BasicStats object which has all the information
     * @throws IOException
     * Ack- This code is taken from the stackover flow website and change according to my needs.
     *      < https://stackoverflow.com/questions/31327126/accessing-terms-statistics-in-lucene-4> </>
     */

        private BasicStats getBasicStats(Term myTerm, float queryBoost) throws IOException
        {
            String fieldName = myTerm.field();

            CollectionStatistics collectionStats = new CollectionStatistics(
                    "body",
                    indexReader.maxDoc(),
                    indexReader.getDocCount(fieldName),
                    indexReader.getSumTotalTermFreq(fieldName),
                    indexReader.getSumDocFreq(fieldName)
            );

            TermStatistics termStats = new TermStatistics(
                    myTerm.bytes(),
                    indexReader.docFreq(myTerm),
                    indexReader.totalTermFreq(myTerm)
            );

            BasicStats myStats = new BasicStats(fieldName, queryBoost);
            assert collectionStats.sumTotalTermFreq() == -1 || collectionStats.sumTotalTermFreq() >= termStats.totalTermFreq();
            long numberOfDocuments = collectionStats.maxDoc();

            long docFreq = termStats.docFreq();
            long totalTermFreq = termStats.totalTermFreq();

            if (totalTermFreq == -1) {
                totalTermFreq = docFreq;
            }

            final long numberOfFieldTokens;
            final float avgFieldLength;

            long sumTotalTermFreq = collectionStats.sumTotalTermFreq();

            if (sumTotalTermFreq <= 0) {
                numberOfFieldTokens = docFreq;
                avgFieldLength = 1;
            } else {
                numberOfFieldTokens = sumTotalTermFreq;
                avgFieldLength = (float)numberOfFieldTokens / numberOfDocuments;
            }

            myStats.setNumberOfDocuments(numberOfDocuments);
            myStats.setNumberOfFieldTokens(numberOfFieldTokens);
            myStats.setAvgFieldLength(avgFieldLength);
            myStats.setDocFreq(docFreq);
            myStats.setTotalTermFreq(totalTermFreq);
            return myStats;
    }

    /**
     *
     * @return Returns the StopList
     */

    String[] getStopList()
    {
        return array;
    }

    /**
     *
     * @param s
     * @return Returns the IDF for a String. It constructs the Terms
     * @throws IOException
     */
    private double getIDF(String s) throws  IOException
    {
        Term t= new Term("body",s);
        BasicStats b = getBasicStats(t,1);
        double d = 0.0;
        try
        {
            d = Math.log(b.getNumberOfDocuments()/b.getDocFreq());
        } catch (ArithmeticException e)
        {
            return 0.0;
        }
        return d;
    }

    /**
     *
     * @param s
     * @return Returns the DF for a term passed
     * @throws IOException
     */

    private long getDF(String s) throws  IOException
    {
        Term t= new Term("body",s);
        BasicStats b = getBasicStats(t,1);
        long d = 0;

        if(b.getDocFreq()!=0)
        {
            d = b.getDocFreq();
            return d;
        }

        return d;
    }

    /**
     *
     * @param q
     * @param OriginalQueryTerms
     * @return Another method for TopK.
     */

    private String getTopK(Map<String,Double> q,String OriginalQueryTerms)
    {
        int c = 0;
        String[] split = OriginalQueryTerms.split(" ");
        ArrayList<String> uniqueList=new ArrayList<>(Arrays.asList(split));

        for(Map.Entry<String,Double> m:q.entrySet())
        {
            c++;
            if(!uniqueList.contains(m.getKey()))
            {
                uniqueList.add(m.getKey());
            }
            if(c == MAX_TERM) break;
        }
        StringBuilder build = new StringBuilder();

        for(String s:uniqueList)
        {
            build.append(s);
            build.append(" ");
        }

        return build.toString();
    }

    /**
     *
     * @param q
     * @return return TOP element
     */
    private ArrayList<String> getTopK(Map<String,Double> q)
    {
        Map<String, Double> sorted = new LinkedHashMap<>();
            q.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
            int c=0;
            ArrayList<String> res= new ArrayList<>();

            for(Map.Entry<String,Double> s:sorted.entrySet())
            {
                c++;
                res.add(s.getKey());
                if(c == MAX_TERM) break;
            }
        return res;
    }

    /**
     *
     * @param query
     * @return A Split to remove punctuations , lower case.
     */

    private String[] processQuery(String query)
    {
        return query.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
    }

    /**
     *
     * @param query
     * @return A split to convert to lower case
     */
    private String[] processOriginalTerms(String query)
    {
        return query.toLowerCase().split(" ");
    }

    /**
     *
     * @param sb
     * @return ArrayList, Removes the stop words and duplicates and return unique items, lower case of the document passed
     */

    private ArrayList<String> processDocument(StringBuilder sb)
    {
        String[] data = processQuery(sb.toString());
        ArrayList<String> processedData= new ArrayList<>();
        for(String s:data)
        {
            if(!STOP_WORDS.contains(s))
            {
                if(!processedData.contains(s))
                    processedData.add(s);
            }
        }
        return processedData;
    }

    /**
     *
     * @param list
     * @return returns a String from ArrayList
     */

    private String ArrayListInToString(ArrayList<String> list)
    {
        StringBuilder sb= new StringBuilder();
        for(String s:list)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     *
     * @param processed
     * @param OriginalQueryTerms
     * @return Use the GLOVE word vectors to calculate the nearest words
     */

    private ArrayList<String> getTopTermsPerQueryTerm(ArrayList<String> processed,String OriginalQueryTerms)
    {
        ensureLoadGlove();
        //String[] split = OriginalQueryTerms.split(" ");
        String[] split=processOriginalTerms(OriginalQueryTerms);
        ArrayList<String> topTerms = new ArrayList<>(Arrays.asList(split));

        for(String oTerms:processQuery(OriginalQueryTerms))
        {

            Map<String,Double> eachQueryTerm = new LinkedHashMap<>();
            if(!GLOVE.containsKey(oTerms) || STOP_WORDS.contains(oTerms)) {continue;}

            ArrayList<Double> v1 = GLOVE.get(oTerms);

            for(String cTerms:processed)
            {
                if(GLOVE.containsKey(cTerms) && !oTerms.equals(cTerms))
                {
                    ArrayList<Double> v2 = GLOVE.get(cTerms);
                    Double val = CosineSimilarity(v1,v2);
                    eachQueryTerm.put(cTerms,val);
                }
             }
                /*Concatenate each term*/
            if(!eachQueryTerm.isEmpty())
            {
                ArrayList<String> temp = getTopK(eachQueryTerm);
                for(String s:temp)
                {
                    topTerms.add(s);

                }

            }

        }
        return topTerms;
    }


    /**
     *
     * @param q
     * @return Sorted MAP. Helper method
     */

    private Map<String,Double> sortMAP(Map<String,Double> q)
    {

        Map<String, Double> sorted = new LinkedHashMap<>();
        q.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

        return sorted;

    }

    /**
     *
     * @param q
     * @param n
     * @return Sorted MAP. Helper method
     */
    private Map<String,Long> sortMAP(Map<String,Long> q, int n)
    {
        Map<String, Long> sorted = new LinkedHashMap<>();
        q.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

        return sorted;

    }

    /**
     *
     * @param processed
     * @param OriginalQueryTerms
     * @return returns the Top Terms calculated
     */

    private Map<String,Double> getTopTerms(ArrayList<String> processed,String OriginalQueryTerms)
    {
        ensureLoadGlove();
        Map<String,Double> topTerms = new LinkedHashMap<>();
        for(String oTerms:processQuery(OriginalQueryTerms))
        {
            if(!GLOVE.containsKey(oTerms)|| STOP_WORDS.contains(oTerms)) {continue;}

            ArrayList<Double> v1 = GLOVE.get(oTerms); //Original vector

            for(String cTerms:processed)
            {
                if(GLOVE.containsKey(cTerms) && !oTerms.equals(cTerms))
                {
                    ArrayList<Double> v2 = GLOVE.get(cTerms);
                    Double val = CosineSimilarity(v1,v2);
                    topTerms.put(cTerms,val);
                }
            }
        }

        return sortMAP(topTerms);
    }

    /**
     *
     * @param docList
     * @param k
     * @return This concatenates the Top k Paragraphs and returns the String builder object
     */

    private StringBuilder conCatTopParas(Map<String,Integer> docList,int k)
    {
        int k_value = docList.size() > k ? k : docList.size();
        int counter =0;
        StringBuilder build = new StringBuilder();
        for(Map.Entry<String,Integer> doc : docList.entrySet())
        {
            counter++;
            String docString = EXP_BM25.getDocument(doc.getValue());
            build.append(docString);
            build.append(" ");
            if(counter == k_value) break;
        }
        return build;
    }

    /**
     *
     * @param q
     * @param OriginalQueryTerms
     * @return A top K IDF terms. This takes the nearest words as input and reorders them according to
     *         their highest IDF score.
     */

        private ArrayList<String> IDF(ArrayList<String> q,String OriginalQueryTerms)
        {
            Map<String,Double> unsorted = new LinkedHashMap<>();

            for(String s:q)
            {
                double val=0.0;
                try {
                    val = getIDF(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                unsorted.put(s,val);
            }
            Map<String,Double> sorted = sortMAP(unsorted);

            String[] split=processOriginalTerms(OriginalQueryTerms);
            ArrayList<String> topTerms = new ArrayList<>(Arrays.asList(split));
            int c=0;
            for(Map.Entry<String,Double> m:sorted.entrySet())
            {
                c++;
                topTerms.add(m.getKey());
                if(c==MAX_TERM) break;
            }

                 return topTerms;
        }

    /**
     *
     * @param q
     * @param OriginalQueryTerms
     * @return Takes the top k nearest words and reorders them according to their document frequency
     */

        private ArrayList<String> DF(ArrayList<String> q,String OriginalQueryTerms)
        {
            Map<String,Long> unsorted = new LinkedHashMap<>();

            for(String s:q)
            {
                long val=0;
                try {
                    val = getDF(s);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                unsorted.put(s,val);
            }

            Map<String,Long> sorted = sortMAP(unsorted,1);

            String[] split= processOriginalTerms(OriginalQueryTerms);
            ArrayList<String> topTerms = new ArrayList<>(Arrays.asList(split));
            int c=0;
            for(Map.Entry<String,Long> m:sorted.entrySet())
            {
                c++;
                topTerms.add(m.getKey());
                if(c==MAX_TERM) break;
            }

            return topTerms;
        }

    /**
     *
     * @param docList
     * @param QueryTerms
     * @return Returns the Expanded Terms for the given query and returns the Expanded query as String.
     *          This calculates the nearest words as whole (not per term basis and returns the top K )
     */

    String getTopKTerms(Map<String,Integer> docList,String QueryTerms)
        {
             StringBuilder build = conCatTopParas(docList,MAX_DOC);
             ArrayList<String> processed = processDocument(build);

             Map<String,Double> res = getTopTerms(processed,QueryTerms);
             return getTopK(res,QueryTerms.toLowerCase());
        }

    /**
     *
     * @param docList
     * @param QueryTerms
     * @return Returns the Expanded Terms for the given query and returns the Expanded query as String.
     *      *   This calculates the nearest words for each individual query term
     */

    String getTopKTermsPerQuery(Map<String,Integer> docList,String QueryTerms)
        {
            StringBuilder build = conCatTopParas(docList,MAX_DOC);
            ArrayList<String> processed = processDocument(build);

            ArrayList<String> res = getTopTermsPerQueryTerm(processed,QueryTerms);
            return ArrayListInToString(res);
        }

    /**
     *
     * @param docList
     * @param QueryTerms
     * @return Returns the Expanded Terms for the given query and returns the Expanded query as String.
     *      *          This calculates the nearest words per query terms, reorders them on highest to lowest IDF and
     *                  returns the top K.
     */

    String getTopKTermsPerQueryHighIDF(Map<String,Integer> docList,String QueryTerms)
        {
            StringBuilder build = conCatTopParas(docList,MAX_DOC);
            ArrayList<String> processed = processDocument(build);
            ArrayList<String> topTerms = getTopTermsPerQueryTerm(processed,QueryTerms);
            ArrayList<String> res = IDF(topTerms,QueryTerms);
            return ArrayListInToString(res);
        }

    /**
     *
     * @param docList
     * @param QueryTerms
     * @return Returns the Expanded Terms for the given query and returns the Expanded query as String.
     *        This calculates the nearest words per query terms, reorders them on highest to lowest DF and
     *        returns the top K.
     */

     String getTopKTermsPerQueryHighDF(Map<String,Integer> docList,String QueryTerms)
        {
            StringBuilder build = conCatTopParas(docList,MAX_DOC);
            ArrayList<String> processed = processDocument(build);
            ArrayList<String> topTerms = getTopTermsPerQueryTerm(processed,QueryTerms);
            ArrayList<String> res = DF(topTerms,QueryTerms);
            return ArrayListInToString(res);
        }

}

