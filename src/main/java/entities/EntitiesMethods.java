package main.java.entities;

import main.java.entities.EntitiesSearcher;
import main.java.entities.EntitiesUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class EntitiesMethods{

    public Map<String, Map<String, Integer>> getBM25entities(Map<String,String> outline_cbor){

        Map<String, Map<String,String[]>> entities = new LinkedHashMap<String, Map<String, String[]>>();
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();

        EntitiesSearcher entity_searcher = null;
        try {
            entity_searcher = new EntitiesSearcher();
        } catch (IOException e) {
            e.printStackTrace();
        }
        entities = entity_searcher.getBM25Result(outline_cbor);

        EntitiesUtils entity_ranking = new EntitiesUtils();
        ranked_entities = entity_ranking.getRankedEntitiesByCount(entities);

        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getBM25QueryExpansionEntities(Map<String,String> outline_cbor){

        Map<String, String> expanded_cbor = new LinkedHashMap<String, String>();

        Map<String, Map<String, Integer>> initial_ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        initial_ranked_entities = getBM25entities(outline_cbor);


        EntitiesUtils entity_ranking = new EntitiesUtils();
        expanded_cbor = entity_ranking.expandQueryWithEntities(initial_ranked_entities, outline_cbor);

        Map<String, Map<String, Integer>> final_ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        final_ranked_entities = getBM25entities(expanded_cbor);

        return final_ranked_entities;
    }

    public Map<String, Map<String, Integer>> getBM25entitiesAsBody(Map<String,String> outline_cbor){

        Map<String, Map<String,String[]>> entities = new LinkedHashMap<String, Map<String, String[]>>();
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();

        EntitiesSearcher entity_searcher = null;
        try {
            entity_searcher = new EntitiesSearcher("entitiesIndex");
        } catch (IOException e) {
            e.printStackTrace();
        }
        entities = entity_searcher.getBM25Result(outline_cbor);

        EntitiesUtils entity_ranking = new EntitiesUtils();
        ranked_entities = entity_ranking.getRankedEntitiesByCount(entities);

        return ranked_entities;
    }

    public void writeEntitiesToFile(Map<String, Map<String, Integer>> entities, String output_file_name){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        entity_ranking.writeEntitiesRankingFile(entities, output_file_name);
    }
}