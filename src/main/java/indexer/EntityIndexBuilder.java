
package main.java.indexer;
import main.java.util.constants;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Creates the index for the given corpus
 * @author Team 3!
 *
 */
public class EntityIndexBuilder
{
    private IndexWriter entityIndexWriter;

    public EntityIndexBuilder()
    {
        entityIndexWriter = null;
    }

    /**
     * Prepares the indexwriter for use in searching later
     * @return gets indexwriter, if it has previously been created it will return the old index writer
     * 		 if its hasn't been created we parse the paragraph and pass back
     * @throws IOException
     */

    public void getEntityIndexWriter() throws IOException {

        //If we haven't created and indexwriter yet
        if (entityIndexWriter == null)
        {

            //Get the path of the index
            Directory indexDir = FSDirectory.open(Paths.get(constants.ENTITY_DIRECTORY_NAME));

            //Create the configuration for the index
            IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            //Create the IndexWriter
            entityIndexWriter = new IndexWriter(indexDir, config);

            //Parse the paragraphs and return the indexwriter with the corpus indexed
            parseEntities(entityIndexWriter);

        }
    }

    /**
     * Actually parses the paragraph from the mode parameters
     * @param indexWriter generated indexwriter to add doc to
     * @return indexwriter with docs added
     */
    private void parseEntities(IndexWriter entityIndexWriter)
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

            //Then we add the paragraph id and the entities of the paragraph body for searching
            doc.add(new StringField("id", p.getParaId(), Field.Store.YES));
            String para_entities = "";
            String para_entities_list = "";
            for(int i = 0; i < p.getEntitiesOnly().size(); i++){
                //form 1 field for entities. The entities are joined using white space delimiter
                para_entities += p.getEntitiesOnly().get(i)+" ";
                para_entities_list += p.getEntitiesOnly().get(i)+",,,";
            }
            doc.add(new Field("body", para_entities, contentType));
            doc.add(new StringField("entities", para_entities_list, Field.Store.YES));

            //From here we add the document to the indexwriter

            try {
                entityIndexWriter.addDocument(doc);
                increment++;

                //commit the Data after 50 paragraph

                if(increment % 50 ==0)
                {
                    entityIndexWriter.commit();
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
        if (entityIndexWriter != null)
        {
            try
            {
                entityIndexWriter.close();
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

}