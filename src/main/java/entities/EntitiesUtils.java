package main.java.entities;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class EntitiesUtils{

    /*class SortedMap implements Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
            // TODO Auto-generated method stub
            return -o1.getValue().compareTo(o2.getValue());
        }
    }*/

    private Map<String, Integer> SortedMap(Map<String, Integer> entities)
    {

        Map<String, Integer> sorted_entities = new LinkedHashMap<>();
        entities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(e -> sorted_entities.put(e.getKey(), e.getValue()));

        return sorted_entities;

    }

    /*The method returns the ranked entities based on the count of the occurence of the entities in the
    * resultant paragraphs*/
    Map<String, Map<String, Integer>> getRankedEntitiesByCount(Map<String, Map<String,String[]>> entities){

        Map<String, Map<String, Integer>> ranking_entities = new LinkedHashMap<String, Map<String, Integer>>();

        for(Map.Entry<String,Map<String,String[]>> queryMap: entities.entrySet()){

            Map<String,String[]> entityMap = queryMap.getValue();

            for(Map.Entry<String,String[]> entity:entityMap.entrySet()){

                String[] entities_list = entity.getValue();

                for(String e: entities_list){
                    if(ranking_entities.containsKey(queryMap.getKey())){

                        Map<String, Integer> query_entity = ranking_entities.get(queryMap.getKey());

                        if(query_entity.containsKey(e)){
                            query_entity.put(e, query_entity.get(e)+1);
                        }
                        else{
                            query_entity.put(e, 1);
                        }
                    }else{
                        Map<String,Integer> temp = new LinkedHashMap<String,Integer>();
                        temp.put(e, 1);
                        ranking_entities.put(queryMap.getKey(),temp);
                    }

                }

            }
        }

        /*List<Map.Entry<String, Integer>> entity_entry = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(entity_entry, new SortedMap());

        Map<String, Map<String, Integer>> sortedEntitiesMap = new LinkedHashMap<String, <String, Integer>>();
        for(Map.Entry<String, Integer> et: entity_entry){
            sortedEntitiesMap.put(et.getKey(), et.getValue());
        }*/
        Map<String, Map<String, Integer>> sortedEntitiesMap = new LinkedHashMap<String, Map<String, Integer>>();

        for(Map.Entry<String,Map<String, Integer>> queryMap: ranking_entities.entrySet()){
            Map<String, Integer> entity_entry = SortedMap(queryMap.getValue());
            sortedEntitiesMap.put(queryMap.getKey(), entity_entry);
        }
        return sortedEntitiesMap;
    }

    public Map<String, String> expandQueryWithEntities(Map<String, Map<String, Integer>> entities, Map<String, String> outlineCbor){

        Map<String, String> expanded_queries = new LinkedHashMap<String, String>();

        for(Map.Entry<String, String> cbor_query: outlineCbor.entrySet()){
            String expanded_query = cbor_query.getValue();
            if(entities.containsKey(cbor_query.getKey())){
                for(Map.Entry<String, Map<String, Integer>> entityMap: entities.entrySet()){
                        Iterator<Map.Entry <String, Integer>> entity_iterator = entityMap.getValue().entrySet().iterator();
                        int i = 0;
                        while(entity_iterator.hasNext() && i < 5){
                            expanded_query += entity_iterator.next().getValue();
                            i += 1;
                        }
                }
            }
            expanded_queries.put(cbor_query.getKey(), expanded_query);
        }

        return expanded_queries;
    }

    void writeEntitiesRankingFile(Map<String, Map<String, Integer>> entities, String output_file_name){
        List<String> rankings = new ArrayList<String>();

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

        for(Map.Entry<String, Map<String, Integer>> m:entities.entrySet())
        {

            int i = 0;
            for(Map.Entry<String, Integer> q: m.getValue().entrySet()) {
                StringBuilder entityId = new StringBuilder("enwiki:");
                entityId.append(q.getKey().replace(" ", "%20"));
                i += 1;

                rankings.add(m.getKey() + " Q0 " + entityId + " " + String.valueOf(i) + " " + String.valueOf(q.getValue()) + " " + "Team 3" + "-" + "EntityRetrieval");
            }

        }
        try {
            Files.write(file, rankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}