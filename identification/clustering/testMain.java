package clustering;


import base.IniFileReader;
import preprocessing.SqlitePatents;


/**
 * Created by leisun on 15/9/8.
 */
public class testMain
{

    public static void main(String[] args)
    {
        //SqlitePatents s=new SqlitePatents("/Users/leisun/Desktop/PatentData/PatTest.sqlite");

        /**
         * Test one patent
         */
        /*
        String number="05868938";
        USPTOSearch si=new USPTOSearch(number);
        patent p=new patent("",si.getAbs(),si.getClaims(),si.getDescription(),si.getTitle(),"","","");
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Abstract.txt",p.getAbs());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Claims.txt",p.getClaims());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Description.txt",p.getDescription());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Title.txt",p.getTitle());
        */

        /**
         * Generate patents arraylist
         */
        //ArrayList<patent> p=s.getNumPatents(200, "invpat");
        //ArrayList<patent> p=s.getPatentsTextFromSQL(20, "/Users/leisun/Desktop/PatentData/PatentText/", "invpat");
        //s.writeToTexts("/Users/leisun/Desktop/PatentData/PatentText/");
        //s.readText("/Users/leisun/Desktop/PatentData/PatentText/04105099/Abstract.txt");


        /**
         * Clustering process
         */
        //HierClusteringPatents hi=new HierClusteringPatents(p);
        //hi.setEps(1.1);
        // hi.Cluster();
        //System.out.println(hi.toString());

        IniFileReader r=new IniFileReader();
        System.out.println(r.getValue("Weights"));
    }

}
