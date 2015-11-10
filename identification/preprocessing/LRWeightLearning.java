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
        int maxIteration=10000;
        double alpha=1;
        double lamda=0;
        pair<DoubleMatrix,DoubleMatrix> result=this.logisticRTrainingDataGenerator();


        double[][] var0=new double[numberofOptions+1][1];

        for(int i=0;i<numberofOptions+1;i++) {
            var0[i][0]=1.0;
        }

        double previous_error=Double.MAX_VALUE;

        DoubleMatrix thetas=new DoubleMatrix(var0);
        thetas.transpose();

        DoubleMatrix X=result.firstarg;
        DoubleMatrix Y=result.secondarg;
        Y=Y.transpose();
        DoubleMatrix varM4=new DoubleMatrix();
        for(int k=0;k<maxIteration;k++) {

            DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());
            varM1 = varM1.transpose().mmul(thetas);
            DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

            varM2.subi(varM1);



            MatrixFunctions.expi(varM2);



            varM2.addi(1);


            DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
            varM3.addi(1);
            varM3.divi(varM2);


            varM3.subi(Y);

            varM4=new DoubleMatrix(varM3.toArray2());
            MatrixFunctions.absi(varM4);
            if (varM4.sum()/X.rows>previous_error) break;
            previous_error=varM4.sum()/X.rows;


            varM3 = X.transpose().mmul(varM3);

            DoubleMatrix thetas_p=new DoubleMatrix(thetas.toArray2());

            DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

            thetas1=thetas1.put(0, 0, 0);



            varM3.muli(alpha / X.rows);


            thetas1.muli(lamda *alpha/ X.rows);


            thetas.subi(varM3);

            thetas.addi(thetas1);

            thetas_p=MatrixFunctions.absi(thetas_p.subi(thetas));

            if (thetas_p.sum()<0.005) {
                System.out.println(k);
                break;
            }

        }





        System.out.println("Final correcteness: "+previous_error);


        double[] weights=thetas.toArray();

        ArrayList<Double> weight=new ArrayList<>();
        int i=1;

        for(int j=0;j<9;j++) {
            if(ini.getOptionValue(optionsName.get(j))) {
                logger.warn(optionsName.get(j)+weights[i]);
                weight.add(weights[i]);
                i++;
            } else {
                weight.add(0.0);
            }

        }

        this.threshold=-weights[0];
        return generateDistanceFunction(null,weight);
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
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
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

      //distances.get(4).show=true;
       // System.out.println(distances.get(4));
       // distances.get(5).show=true;

        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            int var2=1;
            for(int j=0;j<optionsName.size();j++) {
                if (ini.getOptionValue(optionsName.get(j))) {


                    var0[i][var2]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));
                  // if (j==4) System.out.println(p.secondarg);

                    var2++;
                }

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        DoubleMatrix X=new DoubleMatrix(var0);

        DoubleMatrix Y=new DoubleMatrix(var1);

       // for(int var=0;var<X.columns;var++) {
         //   System.out.println(MatrixFunctions.absi(X.getColumn(var).subi(Y)).sum());
        //}
        return new pair<>(X, Y);
    }
}
