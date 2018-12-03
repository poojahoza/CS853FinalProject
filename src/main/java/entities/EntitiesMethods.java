package main.java.entities;

import main.java.entities.EntitiesSearcher;
import main.java.entities.EntitiesUtils;
import main.java.util.constants;

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

    public Map<String, Map<String, Integer>> getBigramBM25entities(Map<String,String> outline_cbor){

        Map<String, Map<String,String[]>> entities = new LinkedHashMap<String, Map<String, String[]>>();
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();

        EntitiesSearcher entity_searcher = null;
        try {
            entity_searcher = new EntitiesSearcher("BigramIndex");
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

    public Map<String, Map<String, Integer>> getBigramBM25QueryExpansionEntities(Map<String,String> outline_cbor){

        Map<String, Map<String, Integer>> bigram_BM25_ranking_entities = new LinkedHashMap<String, Map<String, Integer>>();
        Map<String, String> expanded_cbor = new LinkedHashMap<String, String>();

        EntitiesUtils entity_ranking = new EntitiesUtils();
        if(constants.lmQueryDocPair.containsKey("BigramJM")){
            entity_ranking.getSDMRankedEntitiesByCount(constants.lmQueryDocPair.get("BigramJM"), bigram_BM25_ranking_entities);
        }

        expanded_cbor = entity_ranking.expandQueryWithEntities(bigram_BM25_ranking_entities, outline_cbor);

        Map<String, Map<String, Integer>> final_ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        final_ranked_entities = getBigramBM25entities(expanded_cbor);

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

    public  Map<String, Map<String, Integer>> getSDMLaPlaceentities(){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        return entity_ranking.getSDMLaPlaceRankedEntities();
    }

    public  Map<String, Map<String, Integer>> getSDMJMentities(){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        return entity_ranking.getSDMJMRankedEntities();
    }

    public  Map<String, Map<String, Integer>> getSDMDirchletentities(){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        return entity_ranking.getSDMDirchletRankedEntities();
    }

    public  Map<String, Map<String, Integer>> getSDMBM25etentities(){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        return entity_ranking.getSDMBM25RankedEntities();
    }

    public Map<String, Map<String, Integer>> getUnigramLaPlaceentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("UnigramLaplace")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("UnigramLaplace"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getBigramLaPlaceentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("BigramLaplace")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("BigramLaplace"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getWindowBigramLaPlaceentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("WindowLaplace")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("WindowLaplace"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getUnigramJMentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("UnigramJM")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("UnigramJM"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getBigramJMentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("BigramJM")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("BigramJM"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getWindowJMentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("WindowJM")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("WindowJM"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getUnigramDrichletentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("UnigramDrichlet")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("UnigramDrichlet"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getBigramDrichletentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("BigramDrichlet")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("BigramDrichlet"));
        }
        return ranked_entities;
    }

    public Map<String, Map<String, Integer>> getWindowDrichletentities() {
        Map<String, Map<String, Integer>> ranked_entities = new LinkedHashMap<String, Map<String, Integer>>();
        if(constants.lmQueryDocPair.containsKey("WindowDrichlet")){
            EntitiesUtils entity_ranking = new EntitiesUtils();
            ranked_entities = entity_ranking.getRankedEntitiesByCount(constants.lmQueryDocPair.get("WindowDrichlet"));
        }
        return ranked_entities;
    }


    public void writeEntitiesToFile(Map<String, Map<String, Integer>> entities, String output_file_name){
        EntitiesUtils entity_ranking = new EntitiesUtils();
        entity_ranking.writeEntitiesRankingFile(entities, output_file_name);
    }
}