package main.java.entities;

import main.java.searcher.Searcher;
import main.java.util.constants;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.Map;
import java.util.LinkedHashMap;


public class EntitiesSearcher extends Searcher{

    public EntitiesSearcher() throws IOException
    {
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.DIRECTORY_NAME_WITH_ENTITY_FIELD))));
        parser = new QueryParser("body", new StandardAnalyzer());
        output_file_name = "Output_BM25_Standard_Ranking_Entity_Field.txt";
    }

    public EntitiesSearcher(String indexMethodName) throws IOException {
        this();
        if(indexMethodName.equals("entitiesIndex")) {
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.ENTITY_DIRECTORY_NAME))));
            parser = new QueryParser("body", new EnglishAnalyzer());
            output_file_name = "Output_BM25_Entities_Ranking.txt";
        }
        else if(indexMethodName.equals("entitiesBigramIndex")) {
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.ENTITY_BIGRAM_DIRECTORY_NAME))));
            parser = new QueryParser("body", new ShingleAnalyzerWrapper(new EnglishAnalyzer(), 2, 2));
            output_file_name = "Output_BM25_Bigram_Entities_Ranking.txt";
        }
    }

    protected  Map<String, Map<String,String[]>> entitiesList = new LinkedHashMap<String, Map<String, String[]>>();

    private void createEntitiesList(String para_entities, String paragraphId, String queryId){

        String[] p_entities;
        if(para_entities.equals("")) {
            p_entities = new String[0];
        }
        else {
            p_entities = para_entities.split(",,,");
        }

        if(entitiesList.containsKey(queryId)){
            Map<String, String[]> queryMap = entitiesList.get(queryId);
            queryMap.put(paragraphId, p_entities);
        }
        else{
            Map<String,String[]> queryMap = new LinkedHashMap<String,String[]>();
            queryMap.put(paragraphId, p_entities);
            entitiesList.put(queryId, queryMap);
        }
    }




    private void getRankingDocuments(ScoreDoc[] scoreDocs, String queryId){
        for(int ind=0; ind<scoreDocs.length; ind++)
        {
            ScoreDoc scoringDoc = scoreDocs[ind];
            Document rankingDocument = null;
            try {
                rankingDocument = searcher.doc(scoringDoc.doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String paragraphId = rankingDocument.getField("id").stringValue();
            String entitieslist = rankingDocument.getField("entities").stringValue();
            createEntitiesList(entitieslist, paragraphId, queryId);
        }
    }

    public Map<String, Map<String,String[]>> getBM25Result(Map<String,String> outline_cbor){
        for(Map.Entry<String,String> data:outline_cbor.entrySet())
        {

            TopDocs resultantDocs = null;
            try {
                resultantDocs = this.performSearch(data.getValue(), 100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ScoreDoc[] scoredDocuments = resultantDocs.scoreDocs;
            getRankingDocuments(scoredDocuments, data.getKey());
            //return entitiesList;
        }
        return entitiesList;
    }
}