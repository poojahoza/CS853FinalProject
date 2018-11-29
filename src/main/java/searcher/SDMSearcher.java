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
    private Map<String, Map <String, Map<String, Float>>> query_doc_pair = new LinkedHashMap<>();
    private Map<String, Map <String, Map<String, Float>>> query_ordered_doc_pair = new LinkedHashMap<>();
    public SDMSearcher(String methodName) throws IOException{
        this.methodName = methodName;
        output_file_name = "output_"+ methodName+"_ranking.txt";
    }
    public  void writeRankings(int variant) throws IOException {
        Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

        switch (variant) {
            // Laplace smoothing
            case 1:

                Map<String, Map<String, Float>> QDPULapace= Util.readRunFile2(constants.methodRunfile.get("UnigramLaplace"));
                createQueryDocPair(QDPULapace);


                Map<String, Map<String, Float>> QDPBLapace= Util.readRunFile2(constants.methodRunfile.get("BigramLaplace"));
                createQueryDocPair(QDPBLapace);


                Map<String, Map<String, Float>> QDPWLapace= Util.readRunFile2(constants.methodRunfile.get("WindowLaplace"));
                createQueryDocPair(QDPWLapace);
                reRank();
                writeRanking();


                break;

            // JM smoothing
            case 2:

                Map<String, Map<String, Float>> QDPUJM= Util.readRunFile2(constants.methodRunfile.get("UnigramJM"));
                createQueryDocPair(QDPUJM);


                Map<String, Map<String, Float>> QDPBJM= Util.readRunFile2(constants.methodRunfile.get("BigramJM"));
                createQueryDocPair(QDPBJM);


                Map<String, Map<String, Float>> QDPWJM= Util.readRunFile2(constants.methodRunfile.get("WindowJM"));
                createQueryDocPair(QDPWJM);
                reRank();
                writeRanking();


                break;
             // Drichlet smoothing
            case 3:

                Map<String, Map<String, Float>> QDPUDR= Util.readRunFile2(constants.methodRunfile.get("UnigramDrichlet"));
                createQueryDocPair(QDPUDR);


                Map<String, Map<String, Float>> QDPBDR= Util.readRunFile2(constants.methodRunfile.get("BigramDrichlet"));
                createQueryDocPair(QDPBDR);


                Map<String, Map<String, Float>> QDPWDR= Util.readRunFile2(constants.methodRunfile.get("WindowDrichlet"));
                createQueryDocPair(QDPWDR);
                reRank();
                writeRanking();


                break;
                // BM25
            case 4:


                Map<String, Map<String, Float>> QDPUBM25= Util.readRunFile2(constants.methodRunfile.get("BM25Searcher"));
                createQueryDocPair(QDPUBM25);


                Map<String, Map<String, Float>> QDPBBM25= Util.readRunFile2(constants.methodRunfile.get("BigramBM25"));
                createQueryDocPair(QDPBBM25);


                Map<String, Map<String, Float>> QDPWBM25= Util.readRunFile2(constants.methodRunfile.get("WindowBM25"));
                createQueryDocPair(QDPWBM25);
                reRank();
                writeRanking();


                break;

                // Lapalce smoothing Inverse Ranking
            case 5:
                Map<String, Map<String, Integer>> QDPULapaceRR= Util.readRunFile(constants.methodRunfile.get("UnigramLaplace"));
                createQueryDocPair(createRevereseRank(QDPULapaceRR));


                Map<String, Map<String, Integer>> QDPBLapaceRR= Util.readRunFile(constants.methodRunfile.get("BigramLaplace"));
                createQueryDocPair(createRevereseRank(QDPBLapaceRR));


                Map<String, Map<String, Integer>> QDPWLapaceRR= Util.readRunFile(constants.methodRunfile.get("WindowLaplace"));
                createQueryDocPair(createRevereseRank(QDPWLapaceRR));
                reRank();
                writeRanking();


                break;
             // JM smoothing Inverse Ranking
            case 6:
                Map<String, Map<String, Integer>> QDPUJMRR = Util.readRunFile(constants.methodRunfile.get("UnigramJM"));
                createQueryDocPair(createRevereseRank(QDPUJMRR));


                Map<String, Map<String, Integer>> QDPBJMRR = Util.readRunFile(constants.methodRunfile.get("BigramJM"));
                createQueryDocPair(createRevereseRank(QDPBJMRR));


                Map<String, Map<String, Integer>> QDPWJMRR = Util.readRunFile(constants.methodRunfile.get("WindowJM"));
                createQueryDocPair(createRevereseRank(QDPWJMRR));
                reRank();
                writeRanking();


             // Drichlet smoothing reveres Rank
            case 7:
                Map<String, Map<String, Integer>> QDPUDRRR= Util.readRunFile(constants.methodRunfile.get("UnigramDrichlet"));
                createQueryDocPair(createRevereseRank(QDPUDRRR));


                Map<String, Map<String, Integer>> QDPBDRRR= Util.readRunFile(constants.methodRunfile.get("BigramDrichlet"));
                createQueryDocPair(createRevereseRank(QDPBDRRR));


                Map<String, Map<String, Integer>> QDPWDRRR= Util.readRunFile(constants.methodRunfile.get("WindowDrichlet"));
                createQueryDocPair(createRevereseRank(QDPWDRRR));
                reRank();
                writeRanking();


            // BM25 reverse Rank
            case 8:
                Map<String, Map<String, Integer>> QDPUBM25RR= Util.readRunFile(constants.methodRunfile.get("BM25Searcher"));
                createQueryDocPair(createRevereseRank(QDPUBM25RR));


                Map<String, Map<String, Integer>> QDPBBM25RR= Util.readRunFile(constants.methodRunfile.get("BigramBM25"));
                createQueryDocPair(createRevereseRank(QDPBBM25RR));


                Map<String, Map<String, Integer>> QDPWBM25RR= Util.readRunFile(constants.methodRunfile.get("WindowBM25"));
                createQueryDocPair(createRevereseRank(QDPWBM25RR));
                reRank();
                writeRanking();


                break;

            default:
                break;
        }

    }

    private Map<String, Map<String, Float>> createRevereseRank(Map<String, Map<String, Integer>> map){

        Map<String, Map<String, Float>> reverseRankMap = new LinkedHashMap<>();
        Map<String, Float> temp = new LinkedHashMap<>();
        float size = 0;
        for(Map.Entry<String, Map<String, Integer>> outerMap : map.entrySet()){
            Map<String, Integer> InnerMap = outerMap.getValue();
            String queryId = outerMap.getKey();
            size = InnerMap.size();
            for(Map.Entry<String, Integer> innermap: InnerMap.entrySet()){

                String docId = innermap.getKey();
                float rank = (float)innermap.getValue();

                float reverseRank = (size - rank) / size;

                temp.put(docId, reverseRank);
            }
            reverseRankMap.put(queryId, temp);
        }
        return reverseRankMap;
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


        if(query_doc_pair.containsKey(this.methodName)) {
            Map<String, Map<String, Float>> outer = query_doc_pair.get(this.methodName);
            if (outer.containsKey(queryId)){
                Map<String, Float> inner = outer.get(queryId);
                if (inner.containsKey(docId)) {
                    Float docScore = inner.get(docId) + score;
                    inner.put(docId, docScore);
                    outer.put(queryId, inner);
                    query_doc_pair.put(this.methodName, outer);

                } else {
                    inner.put(docId, score);
                    outer.put(queryId, inner);
                    query_doc_pair.put(this.methodName,outer);
                }
            } else {
                Map<String, Float> inner = new LinkedHashMap<>();
                inner.put(docId, score);
                outer.put(queryId, inner);
            }
        }
        else{
            Map<String, Map<String, Float>> outer = new LinkedHashMap<>();
            Map<String, Float> inner = new LinkedHashMap<>();
            inner.put(docId, score);
            outer.put(queryId,inner);
            query_doc_pair.put(this.methodName,outer);
        }
    }
    private void reRank(){
        Map<String, Map<String, Float>> temp = new LinkedHashMap<>();
        for(Map.Entry<String, Map<String, Float>> mp: query_doc_pair.get(this.methodName).entrySet()){
             Map<String, Float> SortedDoc= sortByValue(mp.getValue());
             temp.put(mp.getKey(), SortedDoc);
             query_ordered_doc_pair.put(this.methodName, temp);
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
                for(Map.Entry<String, Map<String, Float>> mp :query_ordered_doc_pair.get(this.methodName).entrySet()) {
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

    public void setLaplaceSDMRR() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(5);
    }

    public void setJMSDMRR() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(6);
    }

    public void setDrichletSDMRR() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(7);
    }

    public void setBM25SDMRR() throws IOException{
        System.out.println(this.methodName + " is being called");
        this.writeRankings(8);
    }
    public Map<String, Map<String, Float>>  get_query_doc_pair(){
        return query_ordered_doc_pair.get(this.methodName);
    }
    public String getMethodName(){
        return this.methodName;
    }
    public void print(){
        System.out.println(query_ordered_doc_pair);
    }
    }
