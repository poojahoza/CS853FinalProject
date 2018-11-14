package main.java.queryexpansion;


import main.java.util.constants;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public class ExpansionUtils
{
        public Map<String, ArrayList<Double>> readWordVectors(String fname)
        {

            BufferedReader br = null;
            Map<String, ArrayList<Double>> vector= new LinkedHashMap<>();
            try
            {
                br = new BufferedReader(new FileReader(new File(fname)));
            }catch (FileNotFoundException e)
            {
                System.out.println(e.getMessage());
            }

            String line;
            try
            {
                while ((line = br.readLine()) != null )
                {
                    String[] vec= line.split(" ");
                    ArrayList<Double> arr= new ArrayList<>();
                    for(String s:vec)
                    {
                        if(s.equals(vec[0]))
                        {
                                continue;
                        }
                        else
                        {
                            arr.add(Double.parseDouble(s));
                        }
                    }
                    vector.put(vec[0],arr);
                }
            } catch (NullPointerException | IOException n) {
                System.out.println(n.getMessage());
            }
            return vector;
        }

        public void DisplayVectors(Map<String,ArrayList<Double>> vec)
        {
            for(Map.Entry<String,ArrayList<Double>> m:vec.entrySet())
            {
                System.out.println(m.getKey());
                int i=0;
                for(Double A:m.getValue())
                {
                    ++i;
                    System.out.println(i+"<--------->"+A);
                }
            }
        }

        public double computeCosineSimilarity(ArrayList<Double> v1,ArrayList<Double> v2)
        {
            assert(v1.size() == v2.size());
            double val = 0.0;

            for(int i=0;i< v1.size();i++)
            {
                val+=  v1.get(i) * v2.get(i);
            }
            return val;
        }


        public Properties returnProp()
        {
            Properties prop = new Properties();
            InputStream input = null;

            try {
                input = new FileInputStream(constants.Prop);
                prop.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return prop;
        }



}

