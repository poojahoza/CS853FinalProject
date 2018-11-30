package main.java.searcher;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.document.Document;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import main.java.util.constants;

/**
 * Implementation of the searching algorithms for basic search and term frequency search
 * @author Pooja
 * @LastMod Vaughan - 9/2 Just adding comments
 */

public class Searcher {

	 protected final String teamName = "Team 3";

	//Our searcher, parser, and query object initialization
	 protected IndexSearcher searcher = null;
	 protected QueryParser parser = null;
	 protected Query queryObj = null;
	 protected String methodName = null;
	 protected String output_file_name = null;

	 public Searcher() throws IOException
	 {
		 searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(constants.DIRECTORY_NAME))));
		 parser = new QueryParser("body", new StandardAnalyzer());
		 output_file_name = "Output_BM25_Ranking.txt";
	 }

	    /**
	     * 
	     * @param queryString
	     * @param n 
	     * @return Top documents for this search
	     * @throws IOException
	     * @throws ParseException
	     */
	    protected TopDocs performSearch(String queryString, int n)
	    throws IOException, ParseException {

	    	queryObj = parser.parse(queryString);
	        return searcher.search(queryObj, n);
	    }

		// outerkey-->Query ID
		//Inner rkey -->paraID

		private void createRankingQueryDocPair(String outer_key, String inner_key, Integer rank)
		{
			if(constants.queryDocPair.containsKey(outer_key))
			{
				Map<String, Integer> extract = constants.queryDocPair.get(outer_key);
				extract.put(inner_key, rank);
			}
			else
			{

				Map<String,Integer> temp = new LinkedHashMap<String,Integer>();
				temp.put(inner_key, rank);
				constants.queryDocPair.put(outer_key,temp);
			}
		}

		private void createEntityQueryDocPair(String outer_key, String inner_key, String entities, Map<String, Map<String,String[]>> searcher_entitiesList)
		{
			String[] p_entities;
			if(entities.equals("")) {
				p_entities = new String[0];
			}
			else {
				p_entities = entities.split(",,,");
			}

			if(searcher_entitiesList.containsKey(outer_key))
			{
				Map<String, String[]> extract = searcher_entitiesList.get(outer_key);
				extract.put(inner_key, p_entities);
			}
			else
			{

				Map<String, String[]> temp = new LinkedHashMap<String, String[]>();
				temp.put(inner_key, p_entities);
				searcher_entitiesList.put(outer_key,temp);
			}
		}

	    
	    /**
	     * 
	     */
	    private List<String> getRankings(ScoreDoc[] scoreDocs, String queryId, Map<String, Map<String,String[]>> searcher_entitiesList)
	    	    throws IOException {
	    	
	    	List<String> rankings = new ArrayList<String>();


	    	for(int ind=0; ind<scoreDocs.length; ind++){

				//Get the scoring document
				ScoreDoc scoringDoc = scoreDocs[ind];

				//Create the rank document from searcher
				Document rankedDoc = searcher.doc(scoringDoc.doc);
//				System.out.println(searcher.explain(queryObj, scoringDoc.doc));

				//Print out the results from the rank document
				String docScore = String.valueOf(scoringDoc.score);
				String paraId = rankedDoc.getField("id").stringValue();
				System.out.println(rankedDoc.getField("entities"));
				String paraEntities = "";
				if(rankedDoc.getField("entities") != null){
					paraEntities = rankedDoc.getField("entities").stringValue();
				}
				//String paraBody = rankedDoc.getField("body").stringValue();
				String paraRank = String.valueOf(ind+1);
				rankings.add(queryId + " Q0 " + paraId + " " + paraRank + " " + docScore + " "+teamName + "-" + methodName);
				createRankingQueryDocPair(queryId, paraId, Integer.valueOf(paraRank));
				createEntityQueryDocPair(queryId, paraId, paraEntities, searcher_entitiesList);
			}

	    	return rankings;
	    }
	    
	    /**
	     * Output the rankings for Assignment 2
	     * @param p Map containing the query Id and the query value
	     */
	    public void writeRankings(Map<String,String> p, String methodname)
		{
			Path file = Paths.get(output_file_name);
			Map<String, Map<String,String[]>> searcher_entitiesList = new LinkedHashMap<String, Map<String, String[]>>();
			System.out.println("Method Name : "+methodname);

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

			for(Map.Entry<String,String> m:p.entrySet())
			{
				try {
					TopDocs searchDocs = this.performSearch(m.getValue(), 100);
					
					ScoreDoc[] scoringDocuments = searchDocs.scoreDocs;
					List<String> formattedRankings = this.getRankings(scoringDocuments, m.getKey(), searcher_entitiesList);
					Files.write(file, formattedRankings, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
			constants.lmQueryDocPair.put(methodname, searcher_entitiesList);
		}
		public String getOutputFileName(){
	    	return this.output_file_name;
		}
}

