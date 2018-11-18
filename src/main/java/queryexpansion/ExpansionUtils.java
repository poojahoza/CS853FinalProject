package main.java.queryexpansion;

/*Import statements from the CS853 package*/

import main.java.searcher.BM25;
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


public class ExpansionUtils
{
        private IndexReader indexReader  = null;
        private BM25 EXP_BM25= null;
        private Properties PROP= null;
        private Map<String, ArrayList<Double>> GLOVE = null;
        private int MAX_DOC = 5;
        private int MAX_TERM=10;

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
            PROP = this.returnProp();

        }

        void setMaxTerm(int max)
        {
            this.MAX_TERM = max;
        }

        void setMaxDoc(int max)
        {
            this.MAX_DOC = max;
        }

        private void readVector()
        {
            GLOVE = this.readWordVectors(PROP.getProperty("glovefile-50d"));
        }

        private void ensureLoadGlove()
        {
            if(GLOVE == null)
            {
                this.readVector();
            }
        }

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


        public void DisplayVectors(Map<String,ArrayList<Double>> vec)
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

    String[] getStopList()
    {
        return array;
    }

    double getIDF(String s) throws  IOException
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

    private String[] processQuery(String query)
    {
        return query.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
    }

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

    private ArrayList<String> getTopTermsPerQueryTerm(ArrayList<String> processed,String OriginalQueryTerms)
    {
        ensureLoadGlove();
        String[] split = OriginalQueryTerms.split(" ");
        ArrayList<String> topTerms = new ArrayList<>(Arrays.asList(split));

        for(String oTerms:processQuery(OriginalQueryTerms))
        {

            Map<String,Double> eachQueryTerm = new LinkedHashMap<>();
            if(!GLOVE.containsKey(oTerms) || STOP_WORDS.contains(oTerms)) {continue;}

            ArrayList<Double> v1 = GLOVE.get(oTerms);

            for(String cTerms:processed)
            {
                if(GLOVE.containsKey(cTerms))
                {
                    ArrayList<Double> v2 = GLOVE.get(cTerms);
                    Double val = CosineSimilarity(v1,v2);
                    eachQueryTerm.put(cTerms,val);
                }
             }
                /*Concatenate each term*/
                ArrayList<String> temp = getTopK(eachQueryTerm);
                for(String s:temp)
                {
                    topTerms.add(s);

                }
        }
        return topTerms;
    }

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
                if(GLOVE.containsKey(cTerms))
                {
                    ArrayList<Double> v2 = GLOVE.get(cTerms);
                    Double val = CosineSimilarity(v1,v2);
                    topTerms.put(cTerms,val);
                }
            }
        }

        Map<String, Double> result2 = new LinkedHashMap<>();
        topTerms.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));

        return result2;
    }


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

        String getTopKTerms(Map<String,Integer> docList,String QueryTerms)
        {
             StringBuilder build = conCatTopParas(docList,MAX_DOC);
             ArrayList<String> processed = processDocument(build);

             Map<String,Double> res = getTopTerms(processed,QueryTerms);
             return getTopK(res,QueryTerms.toLowerCase());
        }

        String getTopKTermsPerQuery(Map<String,Integer> docList,String QueryTerms)
        {
            StringBuilder build = conCatTopParas(docList,MAX_DOC);
            ArrayList<String> processed = processDocument(build);

            ArrayList<String> res = getTopTermsPerQueryTerm(processed,QueryTerms);
            return ArrayListInToString(res);
        }

}

