package preprocessing;

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
import org.carrot2.text.preprocessing.pipeline.CompletePreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by leisun on 15/10/22.
 */
public class patentPreprocessingTF {

    ArrayList<Document> docs = new ArrayList<>();
    ArrayList<patent> patents = new ArrayList<>();
    LanguageCode language = LanguageCode.ENGLISH;
    public int clusterCount = 15;
    public boolean useDimensionalityReduction = false;

    public IPreprocessingPipeline preprocessingPipeline = new CompletePreprocessingPipeline();
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();
    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();
    public final LabelFormatter labelFormatter = new LabelFormatter();

    public patentPreprocessingTF(ArrayList<patent> Pts) {
        this.patents = Pts;
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
        this.generateTextVector("FullText");
        this.generateTextVector("Abstract");
        this.generateTextVector("Claims");
        this.generateTextVector("Description");

    }

    public void generateTextVector(String str)
    {
        docs.clear();
        //Build arrayList of docs
        for (patent p : patents)
        {
            String temp=" ";

            if (str.equalsIgnoreCase("FullText")) temp=p.getAbs() + " " + p.getClaims() + " " + p.getDescription();
            if (str.equalsIgnoreCase("Abstract")) temp=p.getAbs();
            if (str.equalsIgnoreCase("Claims"))   temp=p.getClaims();
            if (str.equalsIgnoreCase("Description")) temp=p.getDescription();


            docs.add(new Document(temp));
        }


        PreprocessingContext preprocessingContext = this.preprocessingPipeline.preprocess(this.docs, (String) null, language);



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





        DoubleMatrix2D var19=new DenseDoubleMatrix2D(stemIndex.size(),docs.size());

        for(int i=0;i<stemIndex.size();i++) {
            for (int j=0;j<docs.size();j++) {
                var19.set(i, j, tfMatrix.get(j).get(i));
            }
        }



        IntArrayList intA=new IntArrayList();

        for(int i=0;i<var19.columns();i++)
        {
            intA.clear();
            intA.add(i);

            if (str.equalsIgnoreCase("FullText")) patents.get(i).setTd(var19.viewSelection((int[]) null, intA.toArray()).copy());
            if (str.equalsIgnoreCase("Abstract")) patents.get(i).setTd_abs(var19.viewSelection((int[]) null, intA.toArray()).copy());
            if (str.equalsIgnoreCase("Claims"))  patents.get(i).setTd_claims(var19.viewSelection((int[]) null, intA.toArray()).copy());
            if (str.equalsIgnoreCase("Description")) patents.get(i).setTd_des(var19.viewSelection((int[]) null, intA.toArray()).copy());
        }

    }

}
