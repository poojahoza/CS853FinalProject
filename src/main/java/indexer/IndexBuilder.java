
package main.java.indexer;
import jdk.nashorn.internal.parser.Token;
import main.java.util.constants;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.index.IndexOptions;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates the index for the given corpus
 * @author Team 3!
 *
 */
public class IndexBuilder
{
		private IndexWriter indexWriter;

		public IndexBuilder()
		{
			indexWriter = null;
		}

	   /**
	    * Prepares the indexwriter for use in searching later
	    * @return gets indexwriter, if it has previously been created it will return the old index writer
	    * 		 if its hasn't been created we parse the paragraph and pass back
	    * @throws IOException
	    */

	    public void getIndexWriter(String IndexType) throws IOException {

			Directory indexDir;
			IndexWriterConfig config;
	    	//If we haven't created and indexwriter yet
	        if (indexWriter == null)
	        {
	        	switch(IndexType){

					case "BigramIndex" :
						//Get the path of the index
						indexDir = FSDirectory.open(Paths.get(constants.BIGRAM_DIRECTORY));

						//Create the configuration for the index
						config = new IndexWriterConfig(new ShingleAnalyzerWrapper(new EnglishAnalyzer(), 2, 2));
						config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

						//Create the IndexWriter
						indexWriter = new IndexWriter(indexDir, config);

						//Parse the paragraphs and return the indexwriter with the corpus indexed
						parseParagraph(indexWriter);

						break;
					case "UNBigramIndex" :
						//Get the path of the index
						indexDir = FSDirectory.open(Paths.get(constants.UNBIGRAM_DIRECTORY));

						//Create the configuration for the index
						config = new IndexWriterConfig(new StandardAnalyzer());
						config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

						//Create the IndexWriter
						indexWriter = new IndexWriter(indexDir, config);

						//Parse the paragraphs and return the indexwriter with the corpus indexed
						parseParagraph2(indexWriter);
						break;
					default:
						//Get the path of the index
						indexDir = FSDirectory.open(Paths.get(constants.DIRECTORY_NAME));

						//Create the configuration for the index
						config = new IndexWriterConfig(new StandardAnalyzer());
						config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

						//Create the IndexWriter
						indexWriter = new IndexWriter(indexDir, config);

						//Parse the paragraphs and return the indexwriter with the corpus indexed
						parseParagraph(indexWriter);

						break;
				}

	        }
	   }
	    
	 /**
	  * Actually parses the paragraph from the mode parameters
	  * @param indexWriter generated indexwriter to add doc to
	  * @return indexwriter with docs added
	  */
	 private void parseParagraph(IndexWriter indexWriter)
	 {
		 
				// this function shoudl take care of the Reading the CBOR file and indexing it
	            FileInputStream fileInputStream2 = null;
	            try {
	            	fileInputStream2 = new FileInputStream(new File(constants.FILE_NAME));
	            }catch(FileNotFoundException fnf) {
	            	System.out.println(fnf.getMessage());
	     
	            }

	            int increment=0;
	            //For each of the paragraphs from the deserialized inputstream
	            for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2))
	            {

	            	  //We create a document
	            	  System.out.println("Indexing "+ p.getParaId());
	            	  Document doc = new Document();

					 FieldType contentType = new FieldType();
					 contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
					 contentType.setStored(true);
					 contentType.setTokenized(true);
					 contentType.setStoreTermVectors(true);

					//Then we add the paragraph id and the paragraph body for searching
					doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
					doc.add(new Field("body", p.getTextOnly(), contentType));
	            	  
	            	  //From here we add the document to the indexwriter

	            	  try {
	            		  indexWriter.addDocument(doc);
						  increment++;

						  //commit the Data after 50 paragraph

						  if(increment % 50 ==0)
						  {
								indexWriter.commit();
						  }
	            	  }catch(IOException ioe) {
	            		  System.out.println(ioe.getMessage());
	            	  }
	            }
			closeIndexWriter();
	            
		 }



	 /**
	  * Closes the indexwriter so that we can use it in searching
	  * @throws IOException
	  */
	 private void closeIndexWriter()
	 {
	        if (indexWriter != null)
	        {
	        	try
				{
					indexWriter.close();
				}
				catch (IOException e)
				{
					System.out.println(e.getMessage());
				}

	        }
	   }

	private void parseParagraph2(IndexWriter indexWriter) throws IOException
	{

		// this function shoudl take care of the Reading the CBOR file and indexing it
		FileInputStream fileInputStream2 = null;
		try {
			fileInputStream2 = new FileInputStream(new File(constants.FILE_NAME));
		}catch(FileNotFoundException fnf) {
			System.out.println(fnf.getMessage());

		}

		int increment=0;
		//For each of the paragraphs from the deserialized inputstream
		for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2))
		{

			//We create a document
			System.out.println("Indexing "+ p.getParaId());
			Document doc = new Document();

			FieldType contentType = new FieldType();
			contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			contentType.setStored(true);
			contentType.setTokenized(true);
			contentType.setStoreTermVectors(true);


			Analyzer analyzer = new Analyzer() {
				@Override
				protected TokenStreamComponents createComponents(String s) {
                    StandardTokenizer src = new StandardTokenizer();
                    TokenStream result = new StandardFilter(src);
                    result = new LowerCaseFilter(result);
                    result = new StopFilter(result,  StandardAnalyzer.STOP_WORDS_SET);
                    result = new PorterStemFilter(result);
                    result = new CapitalizationFilter(result);
                    return new TokenStreamComponents(src, result);
				}
			};

			TokenStream ts = analyzer.tokenStream("content",  p.getTextOnly());
			OffsetAttribute offsetattribute = ts.addAttribute(OffsetAttribute.class);
			CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

			ArrayList<String> terms = new ArrayList<>();
			ts.reset();
            while(ts.incrementToken()){
            	int startOffset = offsetattribute.startOffset();
            	int endOffset = offsetattribute.endOffset();
            	terms.add(charTermAttribute.toString());
			}

			//Then we add the paragraph id and the paragraph body for searching
			for(int i = 0; i < terms.size() - 1; i++) {
				String UNbiterm = terms.get(i + 1) + " " + terms.get(i);
				doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
				doc.add(new Field("body",UNbiterm , contentType));
			}
			//From here we add the document to the indexwriter

			try {
				indexWriter.addDocument(doc);
				increment++;

				//commit the Data after 50 paragraph

				if(increment % 50 ==0)
				{
					indexWriter.commit();
				}
			}catch(IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
		closeIndexWriter();

	}


}