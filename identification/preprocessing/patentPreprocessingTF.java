package preprocessing;

import base.pair;
import base.patent;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by sunlei on 15/9/7.
 */
public class patentPreprocessingTF {
    ArrayList<Document> docs = new ArrayList<>();
    ArrayList<patent> patents = new ArrayList<>();
    LanguageCode language = LanguageCode.ENGLISH;
    public int clusterCount = 15;
    public boolean useDimensionalityReduction = false;

    public IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();
    public final LabelFormatter labelFormatter = new LabelFormatter();

    public patentPreprocessingTF(ArrayList<patent> Pts) {
        this.patents = Pts;
        /**
         * Set the matrix size to build the term frequency.
         */
        this.matrixBuilder.maximumMatrixSize=250*150*4;
    }

    public void setLanguage(LanguageCode code) {
        this.language = code;
    }

    public void setClusterCount(int ClusterCount) {
        this.clusterCount = ClusterCount;
    }

    public void setUseDimensionalityReduction(boolean r) {
        this.useDimensionalityReduction = r;
    }

    public ArrayList<patent> getPatents()
    {
        return this.patents;
    }

    public void preprocess()
    {
//        this.generateTextVector("FullText");
        this.generateTextVector("Abstract");
        this.generateTextVector("Claims");
        this.generateTextVector("Description");
        this.generateTextVector("Title");

    }

    public void generateTextVector(String str)
    {

        //Build arrayList of docs
        for (patent p : patents)
        {
            String temp=" ";
            if (str.equalsIgnoreCase("Abstract")) temp=p.getAbs();
            if (str.equalsIgnoreCase("Claims"))   temp=p.getClaims();
            if (str.equalsIgnoreCase("Description")) temp=p.getDescription();
            if (str.equalsIgnoreCase("Title")) temp=p.getTitle();
            pair<ArrayList<String>,double[]> var0;

            if (!str.equalsIgnoreCase("Title")) {
                var0 =TFcalculation(temp,0.1);
            } else {
                var0=TFcalculation(temp,1);
            }
            double[][] var1=new double[var0.secondarg.length][2];
            int i=0;
            for(double var2:var0.secondarg) {
                var1[i][0]=var2;
                i++;
            }

            if (str.equalsIgnoreCase("Abstract")) {
                p.setTd_abs(new DenseDoubleMatrix2D(var1));
                p.absStems=var0.firstarg;
                //p.setAbs(null);
            }
            if (str.equalsIgnoreCase("Claims"))  {
                p.setTd_claims(new DenseDoubleMatrix2D(var1));
                p.claimsStems=var0.firstarg;
              //  p.setClaims(null);
            }
            if (str.equalsIgnoreCase("Description")) {
                p.setTd_des(new DenseDoubleMatrix2D(var1));
                p.desStems=var0.firstarg;
            //    p.setDescription(null);
            }
            if (str.equalsIgnoreCase("Title")) {
                p.setTd_title(new DenseDoubleMatrix2D(var1));
                p.titleStems=var0.firstarg;
                //p.setTitle(null);
            }


        }




    }


    public static pair<ArrayList<String>,double[]> TFcalculation(String var0,double threshold) {

        IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
        ArrayList<Document> docs = new ArrayList<>();
        docs.add(new Document(" ", var0));
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

        double[] tfMatrix = new double[preprocessingContext.allStems.tf.length];

        for (int j = 0; j < preprocessingContext.allStems.tf.length; j++) {
            tfMatrix[j]=0.0;
        }


        for (int j = 0; j < preprocessingContext.allStems.tfByDocument.length; j ++) {
            tfMatrix[j]=preprocessingContext.allStems.tfByDocument[j][1];
        }

        double sum=0;

        for(double var1:tfMatrix) {
            sum+=var1;
        }

        for(int vari=0;vari<tfMatrix.length;vari++) {
            tfMatrix[vari]/=sum;
        }

        ArrayList<Double> var1=new ArrayList<>();

        for(double vari:tfMatrix) {
            var1.add(vari);
        }

        ArrayList<String> stems=new ArrayList<>();
        for(int vari=0;vari<preprocessingContext.allStems.image.length;vari++) {

            String varTemp=String.copyValueOf(preprocessingContext.allStems.image[vari]);
            stems.add(varTemp);

        }

        HashMap<Double,ArrayList<String>> stemsWithTF=new HashMap<>();

        for(int i=0;i<tfMatrix.length;i++) {
            if(stemsWithTF.containsKey(tfMatrix[i])) {
                stemsWithTF.get(tfMatrix[i]).add(stems.get(i));
            }
            else {
                ArrayList<String> temp=new ArrayList<>();
                temp.add(stems.get(i));
                stemsWithTF.put(tfMatrix[i],temp);
            }
        }

        Collections.sort(var1);

        int numberofstems= (int) (stems.size()*threshold);
        if (numberofstems==0) numberofstems++;

        ArrayList<Double> var3=new ArrayList<>();
        ArrayList<String> var4=new ArrayList<>();
        int i=0;
        for(int j=var1.size()-1;j>=0;j--) {
            double var5=var1.get(j);
            for(String str:stemsWithTF.get(var5)) {
                var4.add(str);
                var3.add(var5);
                i++;
            }
            if(i>=numberofstems) break;
        }


        double[] var6=new double[var3.size()];
        for(int j=0;j<var3.size();j++) {
            var6[j]=var3.get(j);
        }

        return new pair<>(var4,var6);
    }


}



