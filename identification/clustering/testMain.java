package clustering;


import DatasetGenerator.TrainingDataGenerator;
import base.IniFileReader;
import base.patent;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierClusteringPatents;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;
import preprocessing.SqlitePatents;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by leisun on 15/9/8.
 */
public class testMain
{
    private static Logger l= LogManager.getLogger(testMain.class.getName());
    public static void main(String[] args) {
//        SqlitePatents s=new SqlitePatents("/Users/sunlei/Desktop/ThesisData/PatentData/PatTest.sqlite");

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
        // ArrayList<patent> p=s.getPatentsTextFromSQL(50, "/Users/sunlei/Desktop/ThesisData/PatentData/text/", "invpat");
        //s.writeToTexts("/Users/leisun/Desktop/PatentData/PatentText/");
        //s.readText("/Users/leisun/Desktop/PatentData/PatentText/04105099/Abstract.txt");
        //l.info(p.get(1).getClaims());
        /**
         * Clustering process
         */
        //boolean[] var_b={false,false,false,false,false,true};
        /*
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
*/


        // NormalizedLevenshtein l1 = new NormalizedLevenshtein();

        /**
         TrainingDataGenerator dg=new TrainingDataGenerator();
         dg.setSize(300);
         dg.buildData();
         **/

        String str1 = "student,study,invent,inventation,inventation,a,an";
        String str2 = "apple,pear,invent,invent,study,a,an";
        ArrayList<Document> docs = new ArrayList<>();
        docs.add(new Document(str1));
        docs.add(new Document(str2));

        IPreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();
        final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();
        final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();
        PreprocessingContext preprocessingContext = preprocessingPipeline.preprocess(docs, (String) null, LanguageCode.ENGLISH);


        int[] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        short[] wordsType = preprocessingContext.allWords.type;
        IntArrayList featureIndices = new IntArrayList(stemsMfow.length);

        for (int vsmContext = 0; vsmContext < stemsMfow.length; ++vsmContext) {
            short reducedVsmContext = wordsType[stemsMfow[vsmContext]];
            if ((reducedVsmContext & 12290) == 0) {
                featureIndices.add(stemsMfow[vsmContext]);
            }
        }



        ArrayList<Integer> stemIndex=new ArrayList<>();

        for(Integer i:featureIndices.toArray()) {
            stemIndex.add(i);
        }


        ArrayList<ArrayList<Double>> tfMatrix=new ArrayList<>();

        for(int i=0;i<docs.size();i++) {
            ArrayList<Double> var2=new ArrayList<>();
            for(int j=0;j<stemIndex.size();j++) {
                var2.add(0.0);
            }
            tfMatrix.add(var2);
        }


        int index=0;
        for(int i=0;i<preprocessingContext.allStems.tf.length;i++) {
            if (stemIndex.contains(i)) {
                for(int j=0;j<preprocessingContext.allStems.tfByDocument[i].length;j+=2) {
                    tfMatrix.get(preprocessingContext.allStems.tfByDocument[i][j]).set(index, tfMatrix.get(preprocessingContext.allStems.tfByDocument[i][j]).get(index)+preprocessingContext.allStems.tfByDocument[i][j+1]);
                }
                index++;
            }
        }

        for (int i=0;i<stemIndex.size();i++) {
            System.out.print(tfMatrix.get(0).get(i)+" ");
            System.out.println(tfMatrix.get(1).get(i)+" ");
        }
    }

}
