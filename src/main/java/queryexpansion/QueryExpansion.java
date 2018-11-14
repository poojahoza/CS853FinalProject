package main.java.queryexpansion;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class QueryExpansion
{
        private String methodname="";
        private Map<String,String> query;
        private Map<String, ArrayList<Double>> glove;
        private ExpansionUtils exp = null;
        private Properties prop= null;

        public QueryExpansion()
        {
            exp = new ExpansionUtils();
            query = new LinkedHashMap<>();
            prop = exp.returnProp();

        }
        public QueryExpansion(String mName)
        {
            this();
            this.methodname = mName;
        }

        private void readVector()
        {
            String filename = prop.getProperty("GloveFile");
            glove = exp.readWordVectors(filename);
        }

        public void DisplayVector()
        {
            exp.DisplayVectors(glove);
        }


}
