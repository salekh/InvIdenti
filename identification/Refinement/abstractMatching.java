package Refinement;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.*;
import org.jblas.DoubleMatrix;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by leisun on 16/1/21.
 */
public class abstractMatching {
    public static IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
    public static final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();
    public static final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    public static double getSimilarity(String abs1, String abs2) {

        matrixBuilder.maximumMatrixSize=2*300;
        matrixBuilder.maxWordDf=1.0;

        ArrayList<org.carrot2.core.Document> docs = new ArrayList<>();
        docs.add(new org.carrot2.core.Document("", abs1));
        docs.add(new org.carrot2.core.Document("", abs2));

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

            matrixBuilder.termWeighting = new TfTermWeighting();
            matrixBuilder.buildTermDocumentMatrix(var17);
            IntIntHashMap rowToStemIndex = new IntIntHashMap();
            Iterator tdMatrix = var17.stemToRowIndex.iterator();

            while (tdMatrix.hasNext()) {
                IntIntCursor columns = (IntIntCursor) tdMatrix.next();
                rowToStemIndex.put(columns.value, columns.key);
            }

            DoubleMatrix2D var19;


            var19 = var17.termDocumentMatrix;


            var19 = normalize(var19);

            double[][] value=var19.toArray();

            double[][] m1=new double[var19.rows()][1];
            double[][] m2=new double[var19.rows()][1];

            for(int i=0;i<var19.rows();i++) {
                m1[i][0]=value[i][0];
            }

            for(int i=0;i<var19.rows();i++) {
                m2[i][0]=value[i][1];
            }

            DoubleMatrix M1=new DoubleMatrix(m1);
            DoubleMatrix M2=new DoubleMatrix(m2);

            double n1=M1.transpose().mmul(M1).get(0,0);
            double n2=M2.transpose().mmul(M2).get(0,0);

            double sum=M1.transpose().mmul(M2).get(0,0);

            return sum/(Math.sqrt(n1)*Math.sqrt(n2));

        }



        return 0;

    }

    public static DoubleMatrix2D normalize(DoubleMatrix2D var) {
        double[] sum=new double[var.columns()];
        for(int i=0;i<var.columns();i++) {
            sum[i]=var.viewColumn(i).zSum();
        }

        for(int i=0;i<var.columns();i++) {
            for(int j=0;j<var.rows();j++) {
                if(var.get(j,i)>0) {
                    var.set(j,i,var.get(j,i)/sum[i]);
                }
            }
        }
        return var;
    }

}