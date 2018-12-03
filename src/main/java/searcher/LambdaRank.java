package main.java.searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.util.Util;
import main.java.util.constants;

/**
 * Class generates the lambdaMART features file used in training the model for lambdaBM25
 *
 */
public class LambdaRank {

    private Map<String, Map<String, Map<String, float[]>>> ranking_pairs = new LinkedHashMap<String, Map<String, Map<String, float[]>>>();
    private Map<String,Map<String,Integer>> qrel_data;
    private Map<String, Float> QIDToFloat;
    private Map<Float, String> floatToQid;

    /**
     * Constructor that sets the training data
     * @param qrel
     */
    public LambdaRank(Map<String,Map<String,Integer>> qrel){
        qrel_data = qrel;
       
    }

    /**
     * Created the rankings
     * @throws IOException
     */
    public void generateRanklibFile() throws IOException{
        
    	//Prep a ranking doc for the model to train on
    	getRankings(true);
        this.createQIDMapToFloat();
        rewriteQrel();
        writeRankingDoc("rankingDoc.txt");
        
        //Prep the testDoc for testing the training model
        ranking_pairs = new LinkedHashMap<String, Map<String, Map<String, float[]>>>();
        getRankings(false);
        this.createQIDMapToFloat();
        writeRankingDoc("testDoc.txt");
        
        //TODO: Implement evaluator.java from ranklib to produce ranklib file automatically.
        
        //Modifies the produced ranking file from ranklib 
        String indriInput = "C:\\Users\\VaughanCoder\\GitWorkspace\\cs853FinalProject\\testIndri.txt";
        String indriOutput = "C:\\\\Users\\\\VaughanCoder\\\\GitWorkspace\\\\cs853FinalProject\\\\testIndri2.txt";
         undoRankedDoc(indriInput, indriOutput);
    }
    
    /**
     * Pulls the qrel relevancy from the qrel data for a given doc associated with the given query
     * @param query_id
     * @param doc_id
     * @return
     */
    private int getQrelRelevancy(String query_id, String doc_id){
        if(qrel_data.containsKey(query_id)){
            Map<String,Integer> temp = qrel_data.get(query_id);

            if(temp.containsKey(doc_id)){
                return temp.get(doc_id);
            }
            return -1;
        }
        return -1;
    }

    /**
     * Receives the ranking function, query and rank for each document, generating a ranking pair for the document/function
     * @param function_key
     * @param query_key
     * @param doc_id
     * @param rank
     */
    private void createRankingPair(String function_key, String query_key, String doc_id, float[] rank)
    {
    	
    	   if(ranking_pairs.containsKey(query_key))// query_key
           {
               Map<String, Map<String, float[]>> extract = ranking_pairs.get(query_key); //query_key
               
               if(extract.containsKey(doc_id)){ //function_key //doc_id
            	   
                   Map<String, float[]> function_extract = extract.get(doc_id);//function_key //doc_id
                   
                   if(!function_extract.containsKey(function_key)) { //function_key
                	   
                       function_extract.put(function_key, rank); 
                       extract.put(doc_id, function_extract);
                       
                       ranking_pairs.put(query_key, extract);
                       
                   }else {
                	   System.out.println("Error");
                   }
               }
               else{
                   
                   Map<String, float[]> function_temp = new LinkedHashMap<String, float[]>();
                  
                   function_temp.put(function_key, rank);
                   extract.put(doc_id, function_temp);

                   ranking_pairs.put(query_key, extract);
               }

           }
           else
           {
        	   
               Map<String, Map<String, float[]>> doc_temp = new LinkedHashMap<String, Map<String, float[]>>();
               Map<String, float[]> function_temp = new LinkedHashMap<String, float[]>();

               function_temp.put(function_key, rank);
               doc_temp.put(doc_id, function_temp);
               
               ranking_pairs.put(query_key, doc_temp);
               
           }
    }

    /**
     * Group rankings by each ranking method
     * @throws IOException
     */
    private void getRankings(boolean isTrain) throws IOException {
    	Map<String,String> p = null;
    	if(!isTrain) {
    		 p = Util.readOutline(constants.OUTLINE_CBOR);
    	}else {
    		 p = Util.readOutline(constants.TRAIN_OUTLINE_CBOR);
    	}
        // Ranking Pair for Term Frequency

        LambdaRankFeatureSearcher lambdaTF = new LambdaRankFeatureSearcher("TF");
        lambdaTF.setTF();
        lambdaTF.writeRankings(p);

        callcreateRankingPair("TF");

        //Ranking pair for IDF

        LambdaRankFeatureSearcher lambdaIDF = new LambdaRankFeatureSearcher("IDF");
        lambdaIDF.setIDF();
        lambdaIDF.writeRankings(p);

        callcreateRankingPair("IDF");

        //Ranking pair for document length

        LambdaRankFeatureSearcher lambdaDoclen = new LambdaRankFeatureSearcher("DocLen");
        lambdaDoclen.setDocLen();
        lambdaDoclen.writeRankings(p);

        callcreateRankingPair("DocLen");

        }
    
    /**
     * Collects the function, queryID and relevancy to process each document ranking pair using the create ranking pair function
     * @param function_key
     */
    private void callcreateRankingPair(String function_key){

        for (Map.Entry<String, Map<String,Integer>> Query : constants.queryDocPair.entrySet()) {
            String queryID = Query.getKey();
            Map<String, Integer> docIDRank = Query.getValue();
            for (Map.Entry<String, Integer> document : docIDRank.entrySet()) {

                int relevancy = (getQrelRelevancy(queryID, document.getKey()) == 1 ? 1 : 0);
                float docval= (float)1.0 /document.getValue();
               createRankingPair(function_key, queryID, document.getKey(), new float[]{docval, relevancy});

            }
        }
    }
    
    /**
     * Produces a ranking document using the cbor input
     * @param fileName takes in a cbor file
     * @throws IOException
     */
    private void writeRankingDoc(String fileName) throws IOException {
      Path path = Paths.get(fileName);
      
	try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
		
		//Null check
			if(this.ranking_pairs!= null && this.ranking_pairs.size() >0) {
				
				//New: Map<Query Function, map <doc, Map< ranking, {vector and ranking float}>>>
				
				//Iterate through ranking pairs
				for(String qid: ranking_pairs.keySet()) {
					//Start ranklib format doc
					
					for(String doc: ranking_pairs.get(qid).keySet()){
						boolean targetSet = false;
						String line = " qid:" + this.QIDToFloat.get(qid) + " ";
						
						for(String rankingFunction: ranking_pairs.get(qid).get(doc).keySet()) {
							//System.out.println(ranking_pairs.get(qid).get(doc).keySet());
							float[] f = ranking_pairs.get(qid).get(doc).get(rankingFunction);
							
							//If we didnt initialize the target, put it at the front
							if(!targetSet) {
								targetSet = true;
								int targetVal = (int) f[1];
								line = targetVal + line;
							}
							
							//<target> qid:<qid> <feature>:<value> <feature>:<value> ... <feature>:<value> # <info>
							line += this.getRankingFunctionCase(rankingFunction) + ":" + f[0] + " ";
							
						}
						line += "#" + doc;
						writer.write(line);
						writer.newLine();
						
					}
				
					
					
				}
    		
			}
			

      }
    }
    

    
    
    //Ranking functions to print to feature doc.
    private String getRankingFunctionCase(String rankingFunction) {
    	String s = null;
    	switch(rankingFunction){
    	
    	//Term Frequency
    		case "TF":
    				s = "1";
    				break;
    	//Inverse Document Frequency
    		case "IDF":	
    			s = "2";
				break;
		
	    //Document Length
    		case "DocLen":	
    			s = "3";
				break;
    	}
    	return s;
    }
    
    //Generate a map based on a generated float and the QID
    private void createQIDMapToFloat() {
    	Map<String, Float> qidToFloat = new LinkedHashMap<String,Float>(); 
    	Map<Float, String> floatToQid = new LinkedHashMap<Float, String>(); 
    	
    	System.out.println("Entering QID");
    	
    	//Float id is generated based on ranking pair
    	Float f =  0.0f;
    	for(String qid: ranking_pairs.keySet()) {
    		f = f + 1.0f;
    		qidToFloat.put(qid, f);
    		floatToQid.put(f,qid);
    	}
    	this.QIDToFloat = qidToFloat;
    	this.floatToQid = floatToQid;
    	System.out.println(this.QIDToFloat);
    }
    
    /**
     * Produces a usable Qrel formatted file based on the original query id's present in the training cbor.
     * The output file modifies the query id to match the updated ranking file qid so that it is readable by ranklib
     */
    public void rewriteQrel() {
    	
    		System.out.println("Rewriting Qrel");
    		
	    	 BufferedReader file;
	    	 String inputStr = null;
	    	 
	    	 //Take in the qrel file
			try {
				file = new BufferedReader(new FileReader(constants.QREL_PATH));
		
		         String line;
		         StringBuffer inputBuffer = new StringBuffer();
	         
	         //Read in the qrel into a string file
	         while ((line = file.readLine()) != null) {
	             inputBuffer.append(line);
	             inputBuffer.append('\n');
	         }
	         
	         inputStr = inputBuffer.toString();
	         file.close();
	         
			} catch (IOException ioe) {
				
				//In case of any io issues
				System.out.println("Issue in input lambdaRank!");
			}
			
			//Process each qid based on the ranking document and the randomly generated ids for the queries
			for(String qid: ranking_pairs.keySet()) {
				System.out.println("Rewriting Qrel");

				String temp = String.valueOf(qid).replace("(","\\(").replace(")","\\)")+ "(?!\\%)";
				System.out.println(temp);
				inputStr = inputStr.replaceAll(temp, String.valueOf(QIDToFloat.get(qid)));
			}
	         
			//System.out.println(inputStr);
			try {
				
		    	  FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir")+System.getProperty("file.separator")+"lambdaRankOutput.qrels");
		          fileOut.write(inputStr.getBytes());
		          fileOut.close();
		          
		    } catch (IOException ioe) {
		    	
				//In case of any io issues
				System.out.println("Issue in output LambdaRank!");
			}

    }
    
    /**
     * Used to return qid mapped values back to original QID's for use in trec eval
     */
    private void undoRankedDoc(String indriInputFile, String indriOutputFile) {
    	System.out.println("Rewriting Ranklib indri doc");
		
   	 BufferedReader file;
   	 String inputStr = null;
   	 
   	 //Take in the qrel file
		try {
			file = new BufferedReader(new FileReader(indriInputFile));
	
			   String line;
		         StringBuffer inputBuffer = new StringBuffer();
	        
	        //Read in the qrel into a string file
	        while ((line = file.readLine()) != null) {
	            inputBuffer.append(line);
	            inputBuffer.append('\n');
	        }
	        
	        inputStr = inputBuffer.toString();
	        file.close();
	        
			} catch (IOException ioe) {
				
				//In case of any io issues
				System.out.println("Issue!");
			}
			
			System.out.println(floatToQid);
			if(inputStr != null) { 
			//Process each qid based on the ranking document and the randomly generated ids for the queries
			for(Float floatVal: floatToQid.keySet()) {
				String temp = "(?<![0-9])" + String.valueOf(floatVal).replace(".","\\.");
				//System.out.println(temp);
				inputStr = inputStr.replaceAll(temp , String.valueOf(floatToQid.get(floatVal)));
			}
	        
			//System.out.println(inputStr);
			try {
				
		    	  FileOutputStream fileOut = new FileOutputStream(indriOutputFile);
		          fileOut.write(inputStr.getBytes());
		          fileOut.close();
		          
		    } catch (IOException ioe) {
		    	
				//In case of any io issues
				System.out.println("Issue!");
			}
			}else {
				System.out.println("There was an error reading the indri file if one was already specified.");
			}
    }
    
}