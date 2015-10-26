package clustering;


import base.patent;
import clustering.distancefunction.CosDistance;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;


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

        /*
        patent p1=new patent("123","123","123","123","123","123","123","123");
        patent p2=new patent("123","123","123","123","123","123","123","123");
        CosDistance c=new CosDistance();
        l.error(c.distance(p1,p2));
*/

      int clusterCount = 15;
       boolean useDimensionalityReduction = false;

       IPreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();
         TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();
         TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();


        ArrayList<Document> docs = new ArrayList<>();
        docs.add(new Document("","invent,inventation,invent,apple,apple,apply"));
        docs.add(new Document("","Invent,invent,apple,asdasd"));


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

        preprocessingContext.allLabels.featureIndex = featureIndices.toArray();
        preprocessingContext.allLabels.firstPhraseIndex = -1;
        if (preprocessingContext.hasLabels()) {
            VectorSpaceModelContext var17 = new VectorSpaceModelContext(preprocessingContext);
            ReducedVectorSpaceModelContext var18 = new ReducedVectorSpaceModelContext(var17);
            matrixBuilder.buildTermDocumentMatrix(var17);
            matrixBuilder.buildTermPhraseMatrix(var17);
            IntIntHashMap rowToStemIndex = new IntIntHashMap();
            Iterator tdMatrix = var17.stemToRowIndex.iterator();

            while (tdMatrix.hasNext()) {
                IntIntCursor columns = (IntIntCursor) tdMatrix.next();
                rowToStemIndex.put(columns.value, columns.key);
            }


            DoubleMatrix2D var19;
            if (useDimensionalityReduction && clusterCount * 2 < preprocessingContext.documents.size()) {
               matrixReducer.reduce(var18, clusterCount * 2);
                var19 = var18.coefficientMatrix.viewDice();
            } else {
                var19 = var17.termDocumentMatrix;

            }

            l.error(preprocessingContext.allStems);

            l.info(var19.rows());
            for(int i=0;i<var19.rows();i++) {
                for(int j=0;j<var19.columns();j++) {
                    System.out.print(var19.get(i,j)+" ");
                }
                System.out.println();
            }

        }
    }

}
