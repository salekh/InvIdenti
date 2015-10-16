package clustering;


import DatasetGenerator.TrainingDataGenerator;
import base.IniFileReader;
import base.patent;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierClusteringPatents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preprocessing.SqlitePatents;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by leisun on 15/9/8.
 */
public class testMain
{
    private static Logger l= LogManager.getLogger(testMain.class.getName());
    public static void main(String[] args)
    {
        SqlitePatents s=new SqlitePatents("/Users/sunlei/Desktop/ThesisData/PatentData/PatTest.sqlite");

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

        //ArrayList<patent> p=s.getNumPatents(100, "invpat");
        ArrayList<patent> p=s.getPatentsTextFromSQL(50, "/Users/sunlei/Desktop/ThesisData/PatentData/text/", "invpat");
        //s.writeToTexts("/Users/leisun/Desktop/PatentData/PatentText/");
        //s.readText("/Users/leisun/Desktop/PatentData/PatentText/04105099/Abstract.txt");
        //l.info(p.get(1).getClaims());
        /**
         * Clustering process
         */
        //boolean[] var_b={false,false,false,false,false,true};
        HierClusteringPatents hi=new HierClusteringPatents(p);
        hi.setEps(0.003726864190953778);
        double[] weight={1.0,
                1.8556438515929588E-4,
                0.002305386817577655,
                0.004262380069952872,
                0.002160624848105635,
                0.0013890053935176059};
        CosDistance var0=new CosDistance();
        var0.setWeights(weight);
        //var0.setOptions(var_b);
        l.error(var0.distance(p.get(14),p.get(15)));
        hi.Cluster(var0);
        //System.out.println(hi.toString());
        l.info(hi.toString());

        /**
        TrainingDataGenerator dg=new TrainingDataGenerator();
        dg.setSize(300);
        dg.buildData();
        **/
    }

}
