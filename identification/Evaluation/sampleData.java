package Evaluation;

import base.ProgressBar;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import org.jblas.DoubleMatrix;
import preprocessing.IniFile;
import preprocessing.patentPreprocessingTF;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/12/25.
 */
public class sampleData {
    IniFile iniFile=new IniFile();
    String sampleDataPath;
    String textPath;
    String infoPath;
    private static Logger logger= LogManager.getLogger(sampleData.class.getName());
    private pair<ArrayList<patent>,ArrayList<String>> training;
    private pair<ArrayList<patent>,ArrayList<String>> validation;
    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();
    protected ArrayList<String> optionsName;
    protected int numberofOptions=8;
    protected ArrayList<AbstractDistance> distances;
    protected pair<DoubleMatrix,DoubleMatrix> training_matrices;
    protected pair<DoubleMatrix,DoubleMatrix> validation_matrices;

    pair<ArrayList<patent>,ArrayList<String>> sampleData;
    public sampleData() {
        sampleDataPath=iniFile.getSamplePath();
        infoPath=iniFile.getInfoDataPath();
        textPath=iniFile.getTextPath();
        optionsName=iniFile.getOptionsNames();


        ArrayList<patent> training_p=new ArrayList<>();
        ArrayList<String> training_ID=new ArrayList<>();
        ArrayList<patent> validation_p=new ArrayList<>();
        ArrayList<String> validation_ID=new ArrayList<>();

        logger.info("Start to import the sample data");
        sampleData=new patentsDataset(sampleDataPath,infoPath,textPath,5000,"Benchmark").getPatents();
        logger.info("Sample Data Size: "+sampleData.firstarg.size()+" Patents");
        logger.info("Separate the sample data into training data and validation data");
        for(int i=0;i<sampleData.firstarg.size();i++) {
            if (i<sampleData.firstarg.size()/2) {
                training_p.add(sampleData.firstarg.get(i));
                training_ID.add(sampleData.secondarg.get(i));
            }
            else {
                validation_ID.add(sampleData.secondarg.get(i));
                validation_p.add(sampleData.firstarg.get(i));
            }
        }

        training=new pair<>(training_p,training_ID);
        validation=new pair<>(validation_p,validation_ID);

        sampleData=null;

        logger.info("Training Data Size:"+training.firstarg.size());
        logger.info("Validation Data Size:"+validation.firstarg.size());
        preprocess();
        generateSeperatedDisFunctions();
        this.generateLRTraininngData(training.firstarg,training.secondarg);
        training_matrices=this.logisticRTrainingDataGenerator();


    }


    /**
     * preprocess the training.firstarg;
     */
    protected void preprocess() {
        double start=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(this.training.firstarg);

        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        this.training.firstarg = preprocess.getPatents();
        double end=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(end-start));

    }


    /**
     * Generating the training data of the matrix type.
     */
    public void generateLRTraininngData(ArrayList<patent> patents,ArrayList<String> IDs) {

        /**
         * Clean the Training Data
         */



        this.lrTrainingData.clear();


        for (int i = 0; i < patents.size() - 1; i++) {
            for (int j = i + 1; j < patents.size(); j++) {
                int[] tempint = new int[2];

                tempint[0] = i;
                tempint[1] = j;

                double result;

                if (IDs.get(i).equalsIgnoreCase(IDs.get(j))) {
                    result = 1.0;
                } else {
                    result = 0.0;
                }

                this.lrTrainingData.add(new pair<>(tempint, result));

            }
        }

    }






    /** Generating the training data of the matrix form
     * @return the similarity matrix and the target value vector
     */
    public pair<DoubleMatrix,DoubleMatrix> logisticRTrainingDataGenerator() {



        double[][] var0=new double[this.lrTrainingData.size()][numberofOptions+1];
        double[][] var1=new double[this.lrTrainingData.size()][1];
        logger.info("Start to generate trainingData of patent-patent pair.");
        int i=0;
        double sum=0;
        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            int var2=1;
            System.out.print("\r"+ ProgressBar.barString((int)((i+1)*100/lrTrainingData.size())));
            for(int j=0;j<optionsName.size();j++) {
                if (iniFile.getOptionValue(optionsName.get(j))) {

                    var0[i][var2]=distances.get(j).distance(training.firstarg.get(p.firstarg[0]), training.firstarg.get(p.firstarg[1]));

                    var2++;
                }

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        lrTrainingData.clear();

        DoubleMatrix X=new DoubleMatrix(var0);

        DoubleMatrix Y=new DoubleMatrix(var1);

        System.out.println();
        logger.info("Finished Generating!"+X.rows);

        return new pair<>(X, Y);
    }


    /**
     * Generate a distance function based on a arraylist of weights and a arraylist of index
     * @param attrIndex distance function index
     * @param weights distance function weights
     * @return the generated distance function
     */
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex, ArrayList<Double> weights) {
        CosDistance var0=new CosDistance();
        if (attrIndex!=null) {
            boolean[] var1=new  boolean[this.iniFile.getOptionsNames().size()];
            for(int i=0;i<this.iniFile.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    var1[i]=true;
                } else {
                    var1[i]=false;
                }
            }
            var0.setOptions(var1);
        }
        if (weights!=null&&weights.size()>=this.iniFile.getOptionsNames().size()) {
            double[] var2=new double[this.iniFile.getOptionsNames().size()];
            for(int i=0;i<this.iniFile.getOptionsNames().size();i++) {
                var2[i]=weights.get(i);
            }

            var0.setWeights(var2);
        }



        return var0;
    }

    /**
     * geneerate all the seperated distance functions based on the options needed
     */

    public void generateSeperatedDisFunctions(){



        this.distances=new ArrayList<>();
        int var0=0;

        for(int i=0;i<optionsName.size();i++) {

            ArrayList<Integer> var1 = new ArrayList<>();
            var1.add(i);

            distances.add(this.generateDistanceFunction(var1, null));

            if (iniFile.getOptionValue(optionsName.get(i))) var0++;

        }

        this.numberofOptions=var0;
    }

    public void estimatelearningRate(){
        new learningRate(this.numberofOptions,this.training_matrices.firstarg,this.training_matrices.secondarg);
    }

    public void estimateRegularizationParameter() {
        new regularizationParameter(this.training_matrices.firstarg,this.training_matrices.secondarg,numberofOptions);
    }

    public static void main(String[] args) {
        sampleData l=new sampleData();
        //l.estimatelearningRate();
        l.estimateRegularizationParameter();
    }
}
