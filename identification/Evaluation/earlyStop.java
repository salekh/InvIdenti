package Evaluation;

import base.pair;
import base.patent;
import clustering.distancefunction.CosDistance;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import preprocessing.IniFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/12/29.
 */
public class earlyStop {
    ArrayList<patent> patents;
    ArrayList<patent> training=new ArrayList<>();
    ArrayList<patent> validation=new ArrayList<>();
    ArrayList<patent> testing=new ArrayList<>();
    ArrayList<String> patentsID=new ArrayList<>();
    ArrayList<String> trainingID=new ArrayList<>();
    ArrayList<String> validationID=new ArrayList<>();
    ArrayList<String> testingID=new ArrayList<>();
    int numberofPatents;
    IniFile ini=new IniFile();
    int numberofOptions;


    double valPer=0.2;
    double testPer=0.2;

    int K=5;

    public earlyStop(ArrayList<patent> patents,ArrayList<String> patentsID,int numberofOptions) {
        this.patents=patents;
        this.patentsID=patentsID;
        numberofPatents=patents.size();
        this.numberofOptions=numberofOptions;
        crossValidation();


    }

    public void seperateDataset(int start,int end) {

        training.clear();
        testing.clear();
        validation.clear();
        trainingID.clear();
        testingID.clear();
        validationID.clear();

        ArrayList<patent> temp_p=new ArrayList<>();
        ArrayList<String> temp_i=new ArrayList<>();

        int k=0;
        for(int i=0;i<this.patents.size();i++) {
            if (i>=start&&i<=end) {
                testing.add(patents.get(i));
                testingID.add(patentsID.get(i));
            } else {
                if (k<end-start+1) {
                    validation.add(patents.get(i));
                    validationID.add(patentsID.get(i));
                } else {
                    trainingID.add(patentsID.get(i));
                    training.add(patents.get(i));
                }
                k++;
            }
        }


    }

    public double[] crossValidation(){
        double[] result=new double[2];

        ArrayList<Double> error=new ArrayList<>();
        for(int start=0;start<numberofPatents;start+=(numberofPatents/5)) {
            int end=start+(numberofPatents/5)-1;
            if(end>numberofPatents-1) {
                end=numberofPatents-1;
            }

            seperateDataset(start,end);

            pair<DoubleMatrix,DoubleMatrix> trainings=new trainingDataMatrix(training,trainingID,false).getPatents_Matrices();
            pair<DoubleMatrix,DoubleMatrix> validations=new trainingDataMatrix(validation,validationID,false).getPatents_Matrices();
            pair<DoubleMatrix,DoubleMatrix> testings=new trainingDataMatrix(testing,testingID,false).getPatents_Matrices();

            DoubleMatrix X1=trainings.firstarg;
            DoubleMatrix Y1=trainings.secondarg;
            DoubleMatrix X2=testings.firstarg;
            DoubleMatrix Y2=testings.secondarg;
            DoubleMatrix X3=validations.firstarg;
            DoubleMatrix Y3=validations.secondarg;

            System.out.println();

            error.add (training(X1,Y1,X2,Y2,X3,Y3,0,start/(numberofPatents/5)+1));

        }

        result[0]=0;
        for(double d:error) {
            result[0]+=d;

        }
        result[0]/=K;
        result[1]=0;

        String temp="";

        for(double d:error) {
            temp+=d+" ";
            result[1]+=(d-result[0])*(d-result[0]);
        }



        System.out.println();

        result[1]/=K;
        result[1]=Math.sqrt(result[1]);

        storeText("WeightRegularization.txt",false,temp+" "+result[0]+" "+result[1]);

        System.out.println(result[0]+" "+result[1]);

        return result;
    }
    public double training(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1,DoubleMatrix X2,DoubleMatrix Y2,double lambda,int numofIter){

        System.out.println();
        System.out.println("The "+numofIter+"th Iteration for "+lambda);
        System.out.println(X.rows+" "+X1.rows+" "+X2.rows);


        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);
        int maxIteration=10000;
        double alpha=9.99;

        //Calculate the initial error;

        DoubleMatrix varM1 = applyLogisticonData(X2, thetas);
        double sum = 0;
        for (int m = 0; m < Y2.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum+=(temp-Y2.get(m,0))*(temp-Y2.get(m,0));

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }

        double initial_error = sum;
        double previous_error= sum;
        double relative_change=0;

        System.out.println(previous_error);

        int num=0;
        for(int k=0;k<maxIteration;k++) {
            num++;
            DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());
            pair<DoubleMatrix, Double> var1 = updateWeights(X, Y, X2,Y2,thetas_t, alpha / X.rows, lambda);
            double error = var1.secondarg;
            System.out.println(error+" "+k);
            relative_change=2 * Math.abs(var1.secondarg - previous_error) / (var1.secondarg + previous_error);


            if (relative_change< 1e-4) {
                thetas=new DoubleMatrix(thetas_t.toArray2());
                previous_error=error;


            }



            thetas=new DoubleMatrix(thetas_t.toArray2());

            previous_error=error;


            // System.out.println(previous_error+" "+sum+" "+k);
           // System.out.print("\r"+relative_change+" "+k);


        }

        System.out.println();

        ArrayList<String> optionsName=ini.getOptionsNames();

        double[] weights = thetas.toArray();

        varM1 = applyLogisticonData(X1, thetas);
        sum = 0;
        for (int m = 0; m < Y1.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum+=(temp-Y1.get(m,0))*(temp-Y1.get(m,0));

        }


        ArrayList<Double> weight = new ArrayList<>();
        int i = 1;

        for (int j = 0; j < optionsName.size(); j++) {
            if (ini.getOptionValue(optionsName.get(j))) {
                //logger.warn(optionsName.get(j)+weights[i]);
                weight.add(weights[i]);
                i++;
            } else {
                weight.add(0.0);
            }

        }

        double threshold = -weights[0];

        System.out.println(this.generateDistanceFunction(null,weight));

        System.out.println("Threshold:"+threshold);

        System.out.println(sum);

        System.exit(3);

        return sum;
    }


    public void outputMatrix(DoubleMatrix x,String name) {
        System.out.println("Matrix Name:" +name);
        int var0=0;
        for (int i=0;i<x.rows;i++) {
            String temp="";
            for(int j=0;j<x.columns;j++) {

                temp+=x.get(i,j)+" ";


            }
            System.out.println(temp);
        }



    }



    public void storeText(String path,boolean follow,String str){
        if (follow) {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Update the weights and threshold
     * @param X Similarity matrix
     * @param Y target value matrix
     * @param thetas weights and threshold vector
     * @param alpha learning rate
     * @param lamda regularization factor
     * @return updated weights and threshold vector
     *
     */
    public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X, DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1, DoubleMatrix thetas, double alpha, double lamda) {


        DoubleMatrix varM1=applyLogisticonData(X,thetas);


        varM1.subi(Y);


        varM1 = X.transpose().mmul(varM1);


        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

        thetas1 = thetas1.put(0, 0, 0);

        varM1.muli(alpha);


        thetas1.muli(lamda * alpha);

        thetas.subi(varM1);
        thetas.subi(thetas1);


      //  varM1=applyLogisticonData(X,thetas);
/*
        double sum=0;
        for (int m = 0; m < Y.rows; m++) {

            double temp=varM1.get(m,0);


            if (temp>1) temp=1;
            if (temp<0) temp=0;

            sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);


        }

        //System.out.print(-sum+" ");
*/

        varM1=applyLogisticonData(X1,thetas);

        double sum=0;
        for (int m = 0; m < Y1.rows; m++) {

            double temp=varM1.get(m,0);


            if (temp>1) temp=1;
            if (temp<0) temp=0;

            sum += Y1.get(m, 0) * Math.log(temp) + (1 - Y1.get(m, 0)) * Math.log(1-temp);


        }



        return new pair<>(thetas,-sum);

    }

    /**
     * Apply sigmoid function on the similarity matrix
     * @param X the similarity matrix
     * @param thetas the weights and the threshold
     * @return the Matrix after applying the sigmoid function on the similarity matrix
     */

    public DoubleMatrix applyLogisticonData(DoubleMatrix X,DoubleMatrix thetas) {

        DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());



        varM1 = varM1.transpose().mmul(thetas);

        DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

        varM2.subi(varM1);

        MatrixFunctions.expi(varM2);

        varM2.addi(1);

        DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
        varM3.addi(1);

        varM3.divi(varM2);



        return varM3;

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
            boolean[] var1=new  boolean[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    var1[i]=true;
                } else {
                    var1[i]=false;
                }
            }
            var0.setOptions(var1);
        }
        if (weights!=null&&weights.size()>=this.ini.getOptionsNames().size()) {
            double[] var2=new double[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                var2[i]=weights.get(i);
            }

            var0.setWeights(var2);
        }



        return var0;
    }

}
