package main.java;

import java.io.IOException;

import main.java.indexer.BigramIndexBuilder;
import main.java.indexer.IndexBuilder;
import main.java.indexer.IndexBuilderWithEntities;
import main.java.indexer.EntityIndexBuilder;
import main.java.indexer.EntityIndexBuilderBigram;
import main.java.searcher.LambdaRank;
import main.java.searcher.SDMSearcher;
import main.java.searcher.base;
import main.java.searcher.Searcher;

import main.java.queryexpansion.QueryExpansion;
import main.java.util.constants;
import main.java.util.Util;
import java.util.Map;

import main.java.entities.EntitiesMethods;

public class projectMain
{
    private static void  usage()
    {
        System.out.println("args[0] --> Outlines CBOR Absolute Path");
        System.out.println("args[1] --> Article  Qrel Absolute Path");
        System.out.println("args[2] --> Outlines CBOR Test File Absolute Path");
        System.exit(-1 );
    }
    public static void main(String[] args) throws IOException
    {
        String dest;
        String entity_dest;
        String entity_bigram_dest;
        String entity_field_dest;
        String query_exp_dest;
        Map<String, Map<String, Integer>> ranked_entities;

        if( args.length < 3 )
        {
            usage();
        }
        else
        {
            //dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            query_exp_dest="//home//team3//indexed_file";
            constants.setDirectoryName(query_exp_dest);

            //dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file";
            //entity_dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"entity_indexed_file";
            //entity_bigram_dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"entity_bigram_indexed_file";
            //entity_field_dest = System.getProperty("user.dir")+System.getProperty("file.separator")+"entity_field_indexed_file";
            entity_field_dest = "//home//team3//entity_field_indexed_file";
            entity_dest = "//home//team3//entity_indexed_file";
            //constants.setIndexFileName(args[3]);
            //constants.setDirectoryName(query_exp_dest);

            constants.setEntityDirectoryName(entity_dest);
            //constants.setEntityBigramDirectoryName(entity_bigram_dest);
            constants.setDirectoryNameWithEntityField(entity_field_dest);
            //constants.setDirectoryName(System.getProperty("user.dir")+System.getProperty("file.separator")+"indexed_file");

            constants.setBigramDirectory("//home//team3//BigramIndexed_file");

            constants.setWindowDirectory("//home//team3//Window_file");


			constants.setOutlineCbor(args[0]);
            constants.setQrelPath(args[1]);
            constants.setTrainQrelPath(args[2]);
            constants.setTrainOutlineCbor(args[3]);



            //Create the new lucene Index
           /* IndexBuilder defaultIndex = new IndexBuilder();
            defaultIndex.getIndexWriter();
            BigramIndexBuilder BigramIndex = new BigramIndexBuilder();
            BigramIndex.getIndexWriter("BigramIndex");
            BigramIndexBuilder windowIndex = new BigramIndexBuilder();
            windowIndex.getIndexWriter("WindowIndex");*/

            /*IndexBuilderWithEntities ibe = new IndexBuilderWithEntities();
            ibe.getIndexWriter();*/

            Map<String,String> p = Util.readOutline(constants.OUTLINE_CBOR);

            QueryExpansion q= new QueryExpansion(p);
            q.runBM25("BM25",10);
            q.runPRF("PRF",10);
            q.runPrfIndividual("PRF_PER_QUERY_TERM",10);
            q.runPrfIndividualIDF("PRF_IDF",100);
            q.runPrfIndividualDF("PRF_DF",100);
            q.runPrfIndexElimination("Index_ELIM",100);


            /*Searcher BM25Searcher = new Searcher();
            BM25Searcher.writeRankings(p);*/
            base BM25Searcher = new base("UnigramBM25");
            BM25Searcher.writeRankings(p);

            EntitiesMethods entity_methods = new EntitiesMethods();
            ranked_entities = entity_methods.getBM25entities(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Entites_Ranking.txt");

            ranked_entities.clear();
            ranked_entities = entity_methods.getBM25QueryExpansionEntities(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Expanded_Entites_Ranking.txt");

            constants.methodRunfile.put("UnigramBM25" , BM25Searcher.getOutputFileName());

            System.out.println("-----------------------------------------------------------------------------");


            /*EntityIndexBuilder el = new EntityIndexBuilder();
            el.getEntityIndexWriter();*/

            ranked_entities.clear();
            ranked_entities = entity_methods.getBM25entitiesAsBody(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Entites_Body_Ranking.txt");

            /*EntityIndexBuilderBigram elb = new EntityIndexBuilderBigram();
            elb.getEntityIndexWriter();
            ranked_entities.clear();
            ranked_entities = entity_methods.getBM25entitiesbigramAsBody(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_BM25_Bigram_Entites_Body_Ranking.txt");*/




            base uniLapalce = new base("UnigramLaplace");
            uniLapalce.setUnigramLaplace();
            uniLapalce.writeRankings(p);

            constants.methodRunfile.put("UnigramLaplace", uniLapalce.getOutputFileName());



            base BiLaplace = new base("BigramLaplace");
            BiLaplace.setBigramLaplace();
            BiLaplace.writeRankings(p);

            constants.methodRunfile.put("BigramLaplace", BiLaplace.getOutputFileName());



            base windowLaplace = new base("WindowLaplace");
            windowLaplace.setWindowLaplace();
            windowLaplace.writeRankings(p);

            constants.methodRunfile.put("WindowLaplace", windowLaplace.getOutputFileName());



            base uniJM = new base("UnigramJM");
            uniJM.setUnigramJM();
            uniJM.writeRankings(p);

            constants.methodRunfile.put("UnigramJM", uniJM.getOutputFileName());

            base BiJM = new base("BigramJM");
            BiJM.setBigramJM();
            BiJM.writeRankings(p);

            constants.methodRunfile.put("BigramJM", BiJM.getOutputFileName());



            base windowJm = new base("WindowJM");
            windowJm.setWindowJM();
            windowJm.writeRankings(p);

            constants.methodRunfile.put("WindowJM", windowJm.getOutputFileName());



            base uniDrichlet = new base("UnigramDrichlet");
            uniDrichlet.setUnigramDirichlet();
            uniDrichlet.writeRankings(p);

            constants.methodRunfile.put("UnigramDrichlet" , uniDrichlet.getOutputFileName());




            base BiDrichlet = new base("BigramDrichlet");
            BiDrichlet.setBigramDirichlet();
            BiDrichlet.writeRankings(p);

            constants.methodRunfile.put("BigramDrichlet", BiDrichlet.getOutputFileName());


            base windowDrichlet = new base("WindowDrichlet");
            windowDrichlet.setWindowDritchlet();
            windowDrichlet.writeRankings(p);

            constants.methodRunfile.put("WindowDrichlet", windowDrichlet.getOutputFileName());



            base BiBM25 = new base("BigramBM25");
            BiBM25.setBigramBM25();
            BiBM25.writeRankings(p);

            constants.methodRunfile.put("BigramBM25", BiBM25.getOutputFileName());

            base windowBM25 = new base("WindowBM25");
            windowBM25.setWindowBM25();
            windowBM25.writeRankings(p);

            constants.methodRunfile.put("WindowBM25", windowBM25.getOutputFileName());

            SDMSearcher SDM_Laplace = new SDMSearcher("SDM_with_Laplace_smoothing");
            SDM_Laplace.setLaplaceSDM();
            constants.sdmQueryDocPair.put(SDM_Laplace.getMethodName(), SDM_Laplace.get_query_doc_pair());

            SDMSearcher SDM_JM = new SDMSearcher("SDM_with_JM_smoothing");
            SDM_JM.setJMSDM();
            constants.sdmQueryDocPair.put(SDM_JM.getMethodName(), SDM_JM.get_query_doc_pair());

            SDMSearcher SDM_Drichlet = new SDMSearcher("SDM_with_Drichlet_smoothing");
            SDM_Drichlet.setDrichletSDM();
            constants.sdmQueryDocPair.put(SDM_Drichlet.getMethodName(), SDM_Drichlet.get_query_doc_pair());

            SDMSearcher SDM_BM25 = new SDMSearcher("SDM_with_BM25");
            SDM_BM25.setBM25SDM();
            constants.sdmQueryDocPair.put(SDM_BM25.getMethodName(), SDM_BM25.get_query_doc_pair());

            SDMSearcher SDM_LaplaceRR = new SDMSearcher("SDM_with_Laplace_smoothing_reverse_rank");
            SDM_LaplaceRR.setLaplaceSDMRR();
            constants.sdmQueryDocPair.put(SDM_LaplaceRR.getMethodName(), SDM_LaplaceRR.get_query_doc_pair());

            SDMSearcher SDM_JMRR = new SDMSearcher("SDM_with_JM_smoothing_reverse_rank");
            SDM_JMRR.setJMSDMRR();
            constants.sdmQueryDocPair.put(SDM_JMRR.getMethodName(), SDM_JMRR.get_query_doc_pair());

            SDMSearcher SDM_DrichletRR = new SDMSearcher("SDM_with_Drichlet_smoothing_reverse_rank");
            SDM_DrichletRR.setDrichletSDMRR();
            constants.sdmQueryDocPair.put(SDM_DrichletRR.getMethodName(), SDM_DrichletRR.get_query_doc_pair());

           SDMSearcher SDM_BM25RR = new SDMSearcher("SDM_with_BM25_reverse_rank");
            SDM_BM25RR.setBM25SDMRR();
            constants.sdmQueryDocPair.put(SDM_BM25RR.getMethodName(), SDM_BM25RR.get_query_doc_pair());

            ranked_entities =  entity_methods.getSDMLaPlaceentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_SDM_LaPlace_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getSDMJMentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_SDM_JM_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getSDMDirchletentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_SDM_Dirchlet_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getSDMBM25etentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_SDM_BM25_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getUnigramLaPlaceentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Unigram_LaPlace_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getBigramLaPlaceentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Bigram_LaPlace_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getWindowBigramLaPlaceentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_WindowBigram_LaPlace_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getUnigramJMentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Unigram_JM_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getBigramJMentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Bigram_JM_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getWindowJMentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_WindowBigram_JM_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getUnigramDrichletentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Unigram_Dirchlet_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getBigramDrichletentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Bigram_Dirchlet_Entites_Ranking.txt");

            ranked_entities =  entity_methods.getWindowDrichletentities();
            entity_methods.writeEntitiesToFile(ranked_entities, "output_WindowBigram_Dirchlet_Entites_Ranking.txt");

            ranked_entities.clear();
            ranked_entities = entity_methods.getBigramBM25QueryExpansionEntities(p);
            entity_methods.writeEntitiesToFile(ranked_entities, "output_Bigram_BM25_Expanded_Entites_Ranking.txt");


            Map<String,Map<String,Integer>> qrel = Util.createQrelMap(constants.TRAIN_QREL_PATH);
            
            LambdaRank LR = new LambdaRank(qrel);
		       LR.generateRanklibFile();
        }
    }
}