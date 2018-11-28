package main.java.indexer;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import main.java.util.constants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BigramIndexBuilder extends IndexBuilder {

    private IndexWriter indexWriter;

    public BigramIndexBuilder(){
        super();
    }


    public void getIndexWriter(String indexType) throws IOException {


        Directory indexDir;
        IndexWriterConfig config;
        if (indexWriter == null)
        {
            switch(indexType){

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

                case "WindowIndex" :
                    //Get the path of the index
                    indexDir = FSDirectory.open(Paths.get(constants.WINDOW_DIRECTORY));

                    //Create the configuration for the index
                    config = new IndexWriterConfig(new StandardAnalyzer());
                    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

                    //Create the IndexWriter
                    indexWriter = new IndexWriter(indexDir, config);

                    //Parse the paragraphs and return the indexwriter with the corpus indexed
                    parseParagraph2(indexWriter);
                    break;

                default:
                    break;
                }

            }
        }

    private void parseParagraph2(IndexWriter indexWriter) throws IOException
    {

        // this function should take care of the Reading the CBOR file and indexing it
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
            for(int i = 0; i < terms.size() ; i++) {
                doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
                for(int j = 0; j < constants.windowSize; j++) {
                    doc.add(new Field("body", terms.get(i % terms.size()) + " " + terms.get((i + j) % terms.size()), contentType));
                    doc.add(new Field("body", terms.get((i + j) % terms.size()) + " " + terms.get(i % terms.size()), contentType));
                }
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

