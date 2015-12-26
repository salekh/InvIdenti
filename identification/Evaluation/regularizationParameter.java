package Evaluation;

import base.pair;
import clustering.distancefunction.CosDistance;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import org.jblas.util.Random;
import preprocessing.IniFile;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/12/26.
 */
public class regularizationParameter {
    DoubleMatrix X_t;
    DoubleMatrix Y_t;
    DoubleMatrix X_v;
    DoubleMatrix Y_v;
    IniFile ini=new IniFile();
    int numberofOptions;
    int K=5;

    public regularizationParameter(DoubleMatrix X,DoubleMatrix Y,int numberofOptions){
        int numberofTrainingData=X.rows;
        this.numberofOptions=numberofOptions;
        int[] training=new int[numberofTrainingData/2];
        int[] validation=new int[numberofTrainingData-numberofTrainingData/2];
        for(int i=0;i<X.rows;i++) {
            if(i<numberofTrainingData/2) {
                training[i]=i;
            } else {
                validation[i-numberofTrainingData/2]=i;
            }
        }
       DoubleMatrix X1=X.getRows(training);
        DoubleMatrix Y1=Y.getRows(training);
        DoubleMatrix X2=X.getRows(validation);
        DoubleMatrix Y2=Y.getRows(validation);

        System.out.println(training(X1,Y1,X2,Y2,5));
    }

    public double[] crossValidation(){
        double[] result=new double[2];
        return result;
    }

    public double training(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix X1,DoubleMatrix Y1,double lambda){

        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);
        int maxIteration=5000;
        double alpha=9.7148;

        //Calculate the initial error;
        DoubleMatrix varM1 = applyLogisticonData(X, thetas);
        double sum = 0;
        for (int m = 0; m < Y.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            if (Y.get(m, 0) == 1) {
                sum += Math.log(temp);
            } else {
                sum += Math.log(1 - temp);
            }

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }

        double initial_error = -sum;
        double previous_error=-sum;


        for(int k=0;k<maxIteration;k++) {
            DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());
            pair<DoubleMatrix, Double> var1 = updateWeights(X, Y, thetas_t, alpha / X.rows, lambda);
            double error = var1.secondarg;
            if (2 * Math.abs(var1.secondarg - previous_error) / (var1.secondarg + previous_error + 1e-5) < 1e-5) {
                thetas=new DoubleMatrix(thetas_t.toArray2());
                previous_error=error;
                break;
            }
            previous_error=error;
            System.out.println(previous_error+" "+k);
            thetas=new DoubleMatrix(thetas_t.toArray2());

        }



        ArrayList<String> optionsName=ini.getOptionsNames();

        double[] weights = thetas.toArray();

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

        System.out.println(this.generateDistanceFunction(null,weight));
        System.out.println("Threshold:"+(-weights[0]));




        varM1 = applyLogisticonData(X1, thetas);
        sum = 0;
        for (int m = 0; m < Y1.rows; m++) {

            double temp = varM1.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum+=(temp-Y1.get(m,0))*(temp-Y1.get(m,0));

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
        }


        return sum/Y1.rows;
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
    public pair<DoubleMatrix,Double> updateWeights(DoubleMatrix X, DoubleMatrix Y, DoubleMatrix thetas, double alpha, double lamda) {
        DoubleMatrix varM1=applyLogisticonData(X,thetas);



        double error=0;
        varM1.subi(Y);
        DoubleMatrix error_M=new DoubleMatrix(varM1.toArray2());

        //error=MatrixFunctions.absi(error_M).sum()/X.rows;

        varM1 = X.transpose().mmul(varM1);


        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

        thetas1 = thetas1.put(0, 0, 0);

        varM1.muli(alpha);


        thetas1.muli(lamda * alpha);

        thetas.subi(varM1);
        thetas.subi(thetas1);

        varM1=applyLogisticonData(X,thetas);

        double sum=0;
        for (int m = 0; m < Y.rows; m++) {

            double temp=varM1.get(m,0);


            if (temp>1) temp=1;
            if (temp<0) temp=0;

            if (Y.get(m,0)==1) {
                sum+=Math.log(temp);
            } else {
                sum+=Math.log(1-temp);
            }

            //    sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);
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
