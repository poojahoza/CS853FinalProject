package main.java.searcher;

import main.java.util.Util;
import main.java.util.constants;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class SDMSearcher extends Searcher{
    private Map<String, Map<String, Float>> query_doc_pair = new LinkedHashMap<>();
    private Map<String, Map<String, Float>> query_ordered_doc_pair = new LinkedHashMap<>();
    public SDMSearcher(String methodName) throws IOException{
        this.methodName = methodName;
        output_file_name = "output_"+ methodName+"_ranking.txt";
    }
    public  void writeRankings(int variant) throws IOException {
        Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

        switch (variant) {
            // Laplace smoothing
            case 1:
                base uniLapalce = new base("UnigramLaplace");
                uniLapalce.setUnigramLaplace();
                uniLapalce.writeRankings(p);
                Map<String, Map<String, Float>> QDPULapace= Util.readRunFile2(uniLapalce.output_file_name);
                createQueryDocPair(QDPULapace);

                base BiLaplace = new base("BigramLaplace");
                BiLaplace.setBigramLaplace();
                BiLaplace.writeRankings(p);
                Map<String, Map<String, Float>> QDPBLapace= Util.readRunFile2(BiLaplace.output_file_name);
                createQueryDocPair(QDPBLapace);

                base windowLaplace = new base("WindowLaplace");
                windowLaplace.setWindowLaplace();
                windowLaplace.writeRankings(p);
                Map<String, Map<String, Float>> QDPWLapace= Util.readRunFile2(windowLaplace.output_file_name);
                createQueryDocPair(QDPWLapace);
                reRank();
                writeRanking();
                break;

            // JM smoothing
            case 2:
                base uniJM = new base("UnigramJM");
                uniJM.setUnigramJM();
                uniJM.writeRankings(p);
                Map<String, Map<String, Float>> QDPUJM= Util.readRunFile2(uniJM.output_file_name);
                createQueryDocPair(QDPUJM);

                base BiJM = new base("BigramJM");
                BiJM.setBigramJM();
                BiJM.writeRankings(p);
                Map<String, Map<String, Float>> QDPBJM= Util.readRunFile2(BiJM.output_file_name);
                createQueryDocPair(QDPBJM);

                base windowJm = new base("WindowJM");
                windowJm.setWindowJM();
                windowJm.writeRankings(p);
                Map<String, Map<String, Float>> QDPWJM= Util.readRunFile2(windowJm.output_file_name);
                createQueryDocPair(QDPWJM);
                reRank();
                writeRanking();
                break;
             // Drichlet smoothing
            case 3:
                base uniDrichlet = new base("UnigramDrichlet");
                uniDrichlet.setUnigramDirichlet();
                uniDrichlet.writeRankings(p);
                Map<String, Map<String, Float>> QDPUDR= Util.readRunFile2(uniDrichlet.output_file_name);
                createQueryDocPair(QDPUDR);

                base BiDrichlet = new base("BigramDrichlet");
                BiDrichlet.setBigramDirichlet();
                BiDrichlet.writeRankings(p);
                Map<String, Map<String, Float>> QDPBDR= Util.readRunFile2(BiDrichlet.output_file_name);
                createQueryDocPair(QDPBDR);

                base windowDrichlet = new base("WindowDrichlet");
                windowDrichlet.setWindowDritchlet();
                windowDrichlet.writeRankings(p);
                Map<String, Map<String, Float>> QDPWDR= Util.readRunFile2(windowDrichlet.output_file_name);
                createQueryDocPair(QDPWDR);
                reRank();
                writeRanking();
                break;
                // BM25
            case 4:
                Searcher BM25Searcher = new Searcher();
                BM25Searcher.writeRankings(p);
                Map<String, Map<String, Float>> QDPUBM25= Util.readRunFile2(BM25Searcher.output_file_name);
                createQueryDocPair(QDPUBM25);

                base BiBM25 = new base("BigramBM25");
                BiBM25.setBigramBM25();
                BiBM25.writeRankings(p);
                Map<String, Map<String, Float>> QDPBBM25= Util.readRunFile2(BiBM25.output_file_name);
                createQueryDocPair(QDPBBM25);

                base windowBM25 = new base("WindowBM25");
                windowBM25.setWindowBM25();
                windowBM25.writeRankings(p);
                Map<String, Map<String, Float>> QDPWBM25= Util.readRunFile2(windowBM25.output_file_name);
                createQueryDocPair(QDPWBM25);
                reRank();
                writeRanking();
                break;
            default:
                break;
        }

    }

    private void createQueryDocPair( Map<String, Map<String, Float>> map){

        for(Map.Entry<String, Map<String, Float>> mp : map.entrySet()){
            String queryId = mp.getKey();
            Map<String, Float> temp = mp.getValue();
            for(Map.Entry<String, Float> m : temp.entrySet())
                createRankingpair(queryId, m.getKey(), m.getValue());
        }
    }
    private void createRankingpair(String queryId, String docId, Float score){

        if(query_doc_pair.containsKey(queryId)){
            Map<String, Float> temp = query_doc_pair.get(queryId);
            if(temp.containsKey(docId)){
               Float docScore = temp.get(docId) + score;
               temp.put(docId, docScore);
               query_doc_pair.put(queryId, temp);
            }
            else {
                temp.put(docId, score);
                query_doc_pair.put(queryId, temp);
            }
        }
        else{
            Map<String, Float> docScore = new LinkedHashMap<>();
            docScore.put(docId, score);
            query_doc_pair.put(queryId,docScore);
        }
    }
    private void reRank(){
        for(Map.Entry<String, Map<String, Float>> mp: query_doc_pair.entrySet()){
             Map<String, Float> SortedDoc= sortByValue(mp.getValue());
             query_ordered_doc_pair.put(mp.getKey(), SortedDoc);
        }
    }
    // function to sort hashmap by values
    private Map<String, Float> sortByValue(Map<String, Float> hm)
    {
        Map<String, Float> sorted = hm
                                      .entrySet()
                                      .stream()
                                      .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                      .collect(
                                              toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                                      LinkedHashMap::new));

        return sorted;
    }

    private void writeRanking(){

        Path file = Paths.get(output_file_name);

        try {
            if(output_file_name != null){

                File e = new File(output_file_name);
                if (e.exists()) {
                    e.delete();
                }
                Files.createFile(file);
            }
            else{
                System.out.println("Output file name is null. Please check");
                System.exit(1);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

            try {
                List<String> rankings = new ArrayList<String>();
                for(Map.Entry<String, Map<String, Float>> mp :query_ordered_doc_pair.entrySet()) {
                    Map<String, Float> docSCore = mp.getValue();
                    String queryId = mp.getKey();
                    int ind = 0;
                    for (Map.Entry<String, Float> m : docSCore.entrySet()) {
                        String docId = m.getKey();
                        String score = String.valueOf(m.getValue());
                        String paraRank = String.valueOf(++ind);
                        rankings.add(queryId + " Q0 " + docId + " " + paraRank + " " + score + " "+teamName + "-" + methodName);

                    }
                }
                Files.write(file, rankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
     public void setLaplaceSDM() throws IOException{
         System.out.println(this.methodName + " is being called");
         this.writeRankings(1);
     }
    public void setJMSDM() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(2);
    }
    public void setDrichletSDM() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(3);
    }
    public void setBM25SDM() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(4);
    }
    }
