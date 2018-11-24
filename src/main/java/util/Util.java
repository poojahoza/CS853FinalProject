package main.java.util;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs.treccar_v2.Data;
import java.io.*;
import java.util.*;


public class Util {
    static public Map<String, String> readOutline(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());

        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {

            data.put(page.getPageId(), page.getPageName());

        }
        return data;
    }

    static public Map<String, String> readOutlineSectionPath(String filename) {
        Map<String, String> data = new LinkedHashMap<String, String>();

        FileInputStream qrelStream = null;
        try {
            qrelStream = new FileInputStream(new File(filename));
        } catch (FileNotFoundException fnf) {
            System.out.println(fnf.getMessage());

        }
        for (Data.Page page : DeserializeData.iterableAnnotations(qrelStream))
        {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(page.getPageName());

            for (List<Data.Section> sectionPath : page.flatSectionPaths())
            {

                for(Data.Section section:sectionPath)
                {

                    queryBuilder.append(" ");
                    String  result = section.getHeading().replaceAll("[^\\w\\s]","");
                    queryBuilder.append(result);
                }

            }
            data.put(page.getPageId(), queryBuilder.toString());
        }
        return data;
    }


    public static int relevancy_count(Map<String, Map<String, Integer>> m, String query_id) {
        if (m.containsKey(query_id)) {
            Map<String, Integer> temp = m.get(query_id);
            return temp.size();
        } else {
            return 0;
        }

    }


    static public Map<String, Map<String, Integer>> createQrelMap(String filename) {
        Map<String, Map<String, Integer>> mp = new LinkedHashMap<String, Map<String, Integer>>();

        File fp = new File(filename);
        FileReader fr;
        BufferedReader br = null;


        try {
            fr = new FileReader(fp);
            br = new BufferedReader(fr);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                String line = br.readLine();

               if (line == null) {
                    break;
                }

                String[] words = line.split(" ");
                String outKey = words[0];

                if (mp.containsKey(outKey)) {
                    Map<String, Integer> extract = mp.get(outKey);
                    String inner_key = words[2];
                    Integer is_relevant = new Integer(words[3]);
                    extract.put(inner_key, is_relevant);
                } else {

                    String inner_key = words[2];
                    Integer is_relevant = new Integer(words[3]);
                    Map<String, Integer> temp = new LinkedHashMap<String, Integer>();
                    temp.put(inner_key, is_relevant);
                    mp.put(outKey, temp);
                }
            } catch (NullPointerException n) {
                System.out.println(n.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
        return mp;

    }

    static public void DisplayMap(Map<String, Map<String, Integer>> q) {
        for (Map.Entry<String, Map<String, Integer>> Query : q.entrySet()) {

            for (Map.Entry<String, Integer> p : Query.getValue().entrySet()) {
                System.out.println(Query.getKey() + "," + p.getKey()+","+p.getValue());

            }
            System.out.println("----------------------------------------------------------------------------------------------------");

        }

    }


    static public Map<String, Map<String, Integer>> readRunFile(String filename)
    {
        Map<String, Map<String, Integer>> mp = new LinkedHashMap<String, Map<String, Integer>>();

        File fp = new File(filename);
        FileReader fr;
        BufferedReader br = null;


        try {
            fr = new FileReader(fp);
            br = new BufferedReader(fr);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            try {
                String line = br.readLine();

                if (line == null) {
                    break;
                }

                String[] words = line.split(" ");
                String outKey = words[0];

                if (mp.containsKey(outKey)) {
                    Map<String, Integer> extract = mp.get(outKey);
                    String inner_key = words[2];
                    Integer is_relevant = new Integer(words[3]);
                    extract.put(inner_key, is_relevant);
                } else {

                    String inner_key = words[2];
                    Integer is_relevant = new Integer(words[3]);
                    Map<String, Integer> temp = new LinkedHashMap<String, Integer>();
                    temp.put(inner_key, is_relevant);
                    mp.put(outKey, temp);
                }
            } catch (NullPointerException n) {
                System.out.println(n.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
        return mp;

    }

    /**
     * @author: Amith RC
     * Helper method to find the Document length. Used only in the spearman.
     * This function is more specific to SpearMan
     */

    public static Integer docRanking(Map<String,Map<String,Integer>> m, String queryID, String paraID)
    {

        if(m.containsKey(queryID))
        {
            Map<String,Integer> insideHolder = m.get(queryID);

            if(insideHolder.containsKey(paraID))
            {

                return insideHolder.get(paraID);
            }

        }

        return 0;
    }
}
