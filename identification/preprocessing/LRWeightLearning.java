package preprocessing;

import Evaluation.trainingDataMatrix;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;

import clustering.distancefunction.CosDistance;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/5.
 */
public class LRWeightLearning extends ParameterLearning {

    ArrayList<patent> training=new ArrayList<>();
    ArrayList<String> trainingID=new ArrayList<>();
    ArrayList<patent> validation=new ArrayList<>();
    ArrayList<String> validationID=new ArrayList<>();
    int batchSize=0;


    public AbstractDistance estimateDistanceFunction(){
        seperateDataset();
        batchSize=(training.size()*(training.size()-1)/2);
        pair<DoubleMatrix,DoubleMatrix> validations=new trainingDataMatrix(validation,validationID,false).getPatents_Matrices();
        DoubleMatrix X=validations.firstarg;
        DoubleMatrix Y=validations.secondarg;


        ArrayList<Double> errors=new ArrayList<>();
        int errorBatchSize=10;

        double[][] var0 = new double[numberofOptions + 1][1];
        for (int i = 0; i < numberofOptions + 1; i++) {
            var0[i][0] = 1.0;
        }
        DoubleMatrix thetas = new DoubleMatrix(var0);

        pair<pair<DoubleMatrix, DoubleMatrix>, pair<Integer, Integer>> batch;

        int maxIteration=3000;
        double alpha=9.2*batchSize/(training.size()*(training.size()-1)/2);
        System.out.println(alpha);
        double lambda=0;


        int num=0;
        int updates=0;
        double minerror=Double.MAX_VALUE;
        double errorForValidation=calculateTheError(X,Y,thetas);



        System.out.println("Initial Error: "+errorForValidation);

        ArrayList<Integer> ID1=new ArrayList<>();
        ArrayList<Integer> ID2=new ArrayList<>();
        for(int i=0;i<training.size();i++) {
            ID1.add(i);
            ID2.add(i);
        }
        batch=geneerateAMiniBatchLRTrainingData(ID1,ID2,0,0,batchSize);
        label:

        for(int k=0;k<maxIteration;k++) {

            Collections.shuffle(ID1);
            Collections.shuffle(ID2);
            System.out.println();
            int starti,startj;
            starti=startj=0;

            pair<ArrayList<patent>,ArrayList<String>> temp=shufflePatents(training,trainingID);
            training=temp.firstarg;
            trainingID=temp.secondarg;


            for(int i=0;i<training.size()*(training.size()-1)/2;i+=batchSize){

              /*
                if (i+batchSize<training.size()*(training.size()-1)/2) {
                    batch=geneerateAMiniBatchLRTrainingData(ID1,ID2,starti,startj,batchSize);
                } else {

                    batch=geneerateAMiniBatchLRTrainingData(ID1,ID2,starti,startj,training.size()*(training.size()-1)/2-i);

                }
*/

                starti=batch.secondarg.firstarg;
                startj=batch.secondarg.secondarg+1;
                if (startj>=training.size()) {
                    starti++;
                    startj=0;
                }
                DoubleMatrix thetas_t = new DoubleMatrix(thetas.toArray2());

                thetas_t = updateWeights(batch.firstarg.firstarg, batch.firstarg.secondarg,thetas_t, alpha / batch.firstarg.firstarg.rows, lambda);

                thetas = new DoubleMatrix(thetas_t.toArray2());




                num+=batch.firstarg.firstarg.rows;


                if (num>=X.rows) {
                    errorForValidation=calculateTheError(X,Y,thetas);
                    System.out.println(errorForValidation);

                    if (errorForValidation<minerror) minerror=errorForValidation;
                    //  System.out.println(errorForTraining+" "+errorForValidation);

                    if (updates<errorBatchSize) {
                        updates++;

                        errors.add(errorForValidation);
                    } else {
                        errors.remove(0);
                        errors.add(errorForValidation);


                        if (k > 0) {
                            pair<Double,Double> var2 =calculateStd(errors);
                            double std=var2.firstarg;
                            double mean=var2.secondarg;
                            if (std<1e-6||std/mean<0.001||errorForValidation<1e-6)
                            {
                                System.out.println("asd"+ std +" "+std/mean+" "+errorForValidation);
                                System.exit(3);
                                break label;
                            }
                        }
                    }



                    num=0;
                }




            }


        }


        double[] weights = thetas.toArray();

        ArrayList<Double> weight = new ArrayList<>();
        int i = 1;

        for (int j = 0; j < optionsName.size(); j++) {
            if (ini.getOptionValue(optionsName.get(j))) {
                weight.add(weights[i]);
                i++;
            } else {
                weight.add(0.0);
            }

        }


        this.threshold=-weights[0];

        return (this.generateDistanceFunction(null,weight));

    }




    public pair<pair<DoubleMatrix,DoubleMatrix>,pair<Integer,Integer>> geneerateAMiniBatchLRTrainingData(ArrayList<Integer> ID1,ArrayList<Integer>ID2,int starti,int startj,int batchsize){
        boolean firsttry=true;

        double[][] x=new double[batchsize][numberofOptions+1];
        double[][] y=new double[batchsize][1];
        int index=0;

        int endi,endj;
        endi=endj=ID1.size();
        pair<Integer,Integer> continueIndex;
        label:
        for(int i=starti;i<ID1.size();i++) {
            for(int j=0;j<ID2.size();j++) {
                if(firsttry) {
                    j=startj;
                    firsttry=false;
                }
                if(ID2.get(j)>ID1.get(i)) {
                    x[index][0]=1.0;
                    if (this.trainingID.get(ID1.get(i)).equalsIgnoreCase(this.trainingID.get(ID2.get(j)))) {
                        y[index][0]=1.0;
                    } else {
                        y[index][0] = 0.0;


                    }


                    int var2=1;


                    for(int m=0;m<optionsName.size();m++) {
                        if (ini.getOptionValue(optionsName.get(m))) {
                            x[index][var2]=distances.get(m).distance(training.get(ID1.get(i)), training.get(ID2.get(j)));
                            var2++;
                        }


                    }

                    index++;
                    if(index>=batchsize) {
                        endi=i;
                        endj=j;

                        break label;
                    }
                }
            }
        }

        continueIndex=new pair<>(endi,endj);

        return new pair<>(new pair<>(new DoubleMatrix(x),new DoubleMatrix(y)),continueIndex);
    }





    private pair<ArrayList<patent>,ArrayList<String>> shufflePatents(ArrayList<patent> patents,ArrayList<String> patentsID) {
        ArrayList<Integer> indexes=new ArrayList<>();
        for(int i=0;i<patents.size();i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        ArrayList<patent> temp_p=new ArrayList<>();
        ArrayList<String> temp_i=new ArrayList<>();
        for(int i=0;i<indexes.size();i++) {
            temp_p.add(patents.get(indexes.get(i)));
            temp_i.add(patentsID.get(indexes.get(i)));

        }

        return new pair<>(temp_p,temp_i);
    }


    public pair<Double,Double> calculateStd(ArrayList<Double> errors) {
        double mean=0;
        for(double var0:errors) {
            mean+=var0;
        }
        mean=mean/errors.size();
        double std=0;
        for (double var0:errors) {
            std+=(var0-mean)*(var0-mean);
        }
        std=std/errors.size();
        std=Math.sqrt(std);
        return new pair<>(std,mean);
    }

    public pair<Double,Double> calculatePQ(double minerror,ArrayList<Double> errors){

        int size=5;
        double GL=100*(errors.get(errors.size()-1)/minerror-1);

        double PQ=0;
        double sum=0;
        for(int i=0;i<5;i++) {

            sum+=errors.get(i);
        }

        double progress=(sum/(Collections.min(errors)*errors.size())-1);

        PQ=GL/(100*(sum/(Collections.min(errors)*errors.size())-1));



        return new pair<>(PQ,progress);

    }

    public double calculateTheError(DoubleMatrix X,DoubleMatrix Y,DoubleMatrix thetas){
        DoubleMatrix varM=applyLogisticonData(X, thetas);

        double sum = 0;
        for (int m = 0; m < Y.rows; m++) {

            double temp = varM.get(m, 0);


            if (temp > 1) temp = 1;
            if (temp < 0) temp = 0;

            sum += Y.get(m, 0) * Math.log(temp) + (1 - Y.get(m, 0)) * Math.log(1-temp);

        }

        return (-sum)/X.rows;

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
    public DoubleMatrix updateWeights(DoubleMatrix X, DoubleMatrix Y, DoubleMatrix thetas, double alpha, double lamda) {


        DoubleMatrix varM1=applyLogisticonData(X,thetas);


        varM1.subi(Y);


        varM1 = X.transpose().mmul(varM1);


        DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

        thetas1 = thetas1.put(0, 0, 0);

        varM1.muli(alpha);


        thetas1.muli(lamda * alpha);

        thetas.subi(varM1);
        thetas.subi(thetas1);


        return thetas;

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

    /**
     * geneerate all the seperated distance functions based on the options needed
     */

    public void generateSeperatedDisFunctions(){
        ArrayList<String> optionsName=ini.getOptionsNames();


        this.distances=new ArrayList<>();
        int var0=0;

        for(int i=0;i<optionsName.size();i++) {

            ArrayList<Integer> var1 = new ArrayList<>();
            var1.add(i);

            distances.add(this.generateDistanceFunction(var1, null));

            if (ini.getOptionValue(optionsName.get(i))) var0++;

        }

        this.numberofOptions=var0;
    }




    public void seperateDataset() {

        training.clear();
        validation.clear();
        trainingID.clear();
        validationID.clear();


        ArrayList<Integer> index=new ArrayList<>();
        for(int i=0;i<patents.size();i++) {
            index.add(i);
        }
        int k=0;
        Collections.shuffle(index);
        for(int i=0;i<index.size();i++) {
            if (k<index.size()*0.2) {
                validation.add(patents.get(index.get(i)));
                validationID.add(patentsID.get(index.get(i)));
            }else{
                training.add(patents.get(index.get(i)));
                trainingID.add(patentsID.get(index.get(i)));
            }
            k++;
        }

        System.out.println("Training Data Size:"+training.size());
        System.out.println("Testing Data Size:"+validation.size());
    }

}