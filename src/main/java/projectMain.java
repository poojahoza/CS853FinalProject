package main.java;
import java.io.IOException;

import main.java.indexer.IndexBuilder;
import main.java.indexer.IndexBuilderWithEntities;
import main.java.indexer.EntityIndexBuilder;
import main.java.searcher.Searcher;
import main.java.util.constants;
import main.java.util.Util;
import java.util.Map;

import main.java.entities.EntitiesMethods;

public class projectMain
{
    private static void  usage()
    {
        System.out.println("args[0] --> Paragraph CBOR Absolute Path");
        System.out.println("args[1] --> Outlines CBOR Absolute Path");
        System.out.println("args[2] --> Article  Qrel Absolute Path");
        System.exit(-1 );
    }
    public static void main(String[] args) throws IOException
    {
        String dest;
        String entity_dest;
        String entity_field_dest;
        if( args.length < 3 )
        {
            usage();
        }

        else
        {
            dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            entity_dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"entity_indexed_file";
            entity_field_dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"entity_field_indexed_file";
            constants.setIndexFileName(args[0]);
            constants.setDirectoryName(dest);
            constants.setEntityDirectoryName(entity_dest);
            constants.setDirectoryNameWithEntityField(entity_field_dest);

            constants.setOutlineCbor(args[1]);
            constants.setQrelPath(args[2]);

            //Create the new lucene Index
            IndexBuilder l = new IndexBuilder();
            l.getIndexWriter();

            IndexBuilderWithEntities ibe = new IndexBuilderWithEntities();
            ibe.getIndexWriter();

            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

            Searcher BM25Searcher = new Searcher();
            BM25Searcher.writeRankings(p);

            EntitiesMethods entity_methods = new EntitiesMethods();
            Map<String, Map<String, Integer>> ranked_entities = entity_methods.getBM25entities(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Entites_Ranking.txt");

            ranked_entities.clear();
            ranked_entities = entity_methods.getBM25QueryExpansionEntities(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Expanded_Entites_Ranking.txt");

            System.out.println("-----------------------------------------------------------------------------");

            EntityIndexBuilder el = new EntityIndexBuilder();
            el.getEntityIndexWriter();

            ranked_entities.clear();
            ranked_entities = entity_methods.getBM25entitiesAsBody(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Entites_Body_Ranking.txt");

            /*EntitiesMethods entity_methods = new EntitiesMethods();
            entity_methods.getBM25entities(p);*/
        }

    }
}