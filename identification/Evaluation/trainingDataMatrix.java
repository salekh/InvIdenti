package Evaluation;

import base.ProgressBar;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import preprocessing.IniFile;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by leisun on 15/12/28.
 */
public class trainingDataMatrix {

    private ArrayList<patent> patents;
    private ArrayList<String> patentsID;
    private static Logger logger= LogManager.getLogger(trainingDataMatrix.class.getName());

    boolean infoShow=true;
    IniFile iniFile=new IniFile();
    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();
    protected ArrayList<String> optionsName;
    protected int numberofOptions=8;
    protected ArrayList<AbstractDistance> distances;
    private pair<DoubleMatrix,DoubleMatrix> p_Matrices;

    public trainingDataMatrix(ArrayList<patent> patents,ArrayList<String> IDs,boolean infoShow ){
        this.patents=patents;
        this.patentsID=IDs;
        this.infoShow=infoShow;
        this.optionsName=iniFile.getOptionsNames();
        generateSeperatedDisFunctions();
        generateDoubleMatrix();


    }

    public pair<DoubleMatrix,DoubleMatrix> getPatents_Matrices(){
        return p_Matrices;
    }

    private void generateDoubleMatrix(){
        if (infoShow) {
            logger.info("Start to generate Matrix...");
            logger.info("Patent Size: "+patents.size());
        }
        generateLRTraininngData(patents,patentsID);
        p_Matrices=this.logisticRTrainingDataGenerator();
        if(infoShow) {
            logger.info("Finish Generating...");
            logger.info("Matrix Rows: "+p_Matrices.firstarg.rows);
        }
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
       if (infoShow) logger.info("Start to generate trainingData of patent-patent pair.");
        int i=0;
        double sum=0;
        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            int var2=1;
            if (infoShow)  System.out.print("\r"+ ProgressBar.barString((int)((i+1)*100/lrTrainingData.size())));
            for(int j=0;j<optionsName.size();j++) {
                if (iniFile.getOptionValue(optionsName.get(j))) {

                    var0[i][var2]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));

                    var2++;
                }

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        lrTrainingData.clear();

        DoubleMatrix X=new DoubleMatrix(var0);

        DoubleMatrix Y=new DoubleMatrix(var1);

        if (infoShow) System.out.println();


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

}
