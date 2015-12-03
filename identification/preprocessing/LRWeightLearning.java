package preprocessing;

import base.pair;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.commons.collections.ArrayStack;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import java.time.Year;
import java.util.ArrayList;

/**
 * Created by leisun on 15/11/5.
 */
public class LRWeightLearning extends ParameterLearning {

    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();

    public AbstractDistance estimateDistanceFunction() {

        this.generateLRTraiingData();
        int maxIteration=1000;

        pair<DoubleMatrix,DoubleMatrix> result=this.logisticRTrainingDataGenerator();

        double[][] var0=new double[numberofOptions+1][1];
        for(int i=0;i<numberofOptions+1;i++) {
            var0[i][0]=1.0;
        }

        double previous_error=Double.MAX_VALUE;

        DoubleMatrix thetas=new DoubleMatrix(var0);
        thetas.transpose();
        outputMatrix(thetas.transpose(),"asd");
        DoubleMatrix X=result.firstarg;
        DoubleMatrix Y=result.secondarg;

        double alpha=0.01/(X.rows/(40*79));
        double lamda=0;

        Y=Y.transpose();

        for(int k=0;k<maxIteration;k++) {


            double sum = 0;

            DoubleMatrix varM1=applyLogisticonData(X,thetas);


            for (int m = 0; m < Y.rows; m++) {

                sum += Y.get(m, 0) * Math.log(varM1.get(m, 0)) + (1 - Y.get(m, 0)) * Math.log(1 - varM1.get(m, 0));

            }

            sum = -sum ;


            //System.out.println(k+ " " + sum);

            varM1.subi(Y);

            varM1 = X.transpose().mmul(varM1);


            // DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

            //thetas1 = thetas1.put(0, 0, 0);


            varM1.muli(alpha);
            DoubleMatrix thetas_p=new DoubleMatrix(thetas.toArray2());

            // thetas1.muli(lamda * alpha);

            thetas.subi(varM1);
            //  thetas.subi(thetas1);



            //outputMatrix(thetas.transpose(),"final result");



            DoubleMatrix thetas_t=new DoubleMatrix(thetas_p.toArray2());
            thetas_t=MatrixFunctions.absi(thetas_t);

            thetas_p=MatrixFunctions.absi(thetas_p.subi(thetas));

            thetas_p.divi(thetas_t);



            if (thetas_p.sum()/thetas_p.rows<0.005) {
                System.out.println();
                System.out.println(X.columns);
                System.out.println(k);
                break;
            }

        }

        System.out.println("Final correcteness: "+previous_error);


        double[] weights=thetas.toArray();

        ArrayList<Double> weight=new ArrayList<>();
        int i=1;

        for(int j=0;j<optionsName.size();j++) {
            if(ini.getOptionValue(optionsName.get(j))) {
                //logger.warn(optionsName.get(j)+weights[i]);
                weight.add(weights[i]);
                i++;
            } else {
                weight.add(0.0);
            }

        }

        this.threshold=-weights[0];
        return generateDistanceFunction(null,weight);
    }


    public DoubleMatrix applyLogisticonData(DoubleMatrix X,DoubleMatrix thetas) {




        DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());


        varM1 = varM1.transpose().mmul(thetas);




        DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

        varM2.subi(varM1);


        MatrixFunctions.expi(varM2);


        varM2.addi(1);

        // outputMatrix(varM2,"asd");
        //

        DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
        varM3.addi(1);

        varM3.divi(varM2);

        //outputMatrix(varM3.transpose(),"check");

        return varM3;

    }

    public void outputMatrix(DoubleMatrix x,String name) {
        logger.error("Matrix Name:" +name);
        int var0=0;
        for (int i=0;i<x.rows;i++) {
            String temp="";
            for(int j=0;j<x.columns;j++) {

                    temp+=x.get(i,j)+" ";


            }
            logger.error(temp);
        }



    }

    public void generateLRTraiingData() {
        this.lrTrainingData.clear();
        for (int i = 0; i < this.patents.size() - 1; i++) {
            for (int j = i + 1; j < this.patents.size(); j++) {
                int[] tempint = new int[2];

                tempint[0] = i;
                tempint[1] = j;

                double result;

                if (this.patentsID.get(i).equalsIgnoreCase(this.patentsID.get(j))) {
                    result = 1.0;
                } else {
                    result = 0.0;
                }

                this.lrTrainingData.add(new pair<>(tempint, result));
            }
        }
    }

    public pair<DoubleMatrix,DoubleMatrix> logisticRTrainingDataGenerator() {



        double[][] var0=new double[this.lrTrainingData.size()][numberofOptions+1];
        double[][] var1=new double[this.lrTrainingData.size()][1];

        int i=0;
        double sum=0;
        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            int var2=1;

            for(int j=0;j<optionsName.size();j++) {
                if (ini.getOptionValue(optionsName.get(j))) {

                    var0[i][var2]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));

                    var2++;
                }

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        DoubleMatrix X=new DoubleMatrix(var0);

        DoubleMatrix Y=new DoubleMatrix(var1);


        System.out.println("Finished Generating!");

        return new pair<>(X, Y);
    }
}
