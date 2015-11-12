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
        int maxIteration=4000;
        double alpha=1;
        double lamda=1;
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


            double sum=0;

            DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());


            varM1 = varM1.transpose().mmul(thetas);


            DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

            varM2.subi(varM1);



            MatrixFunctions.expi(varM2);



            varM2.addi(1);


            DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
            varM3.addi(1);
            varM3.divi(varM2);

            for(int m=0;m<Y.rows;m++) {
                if(Y.get(m,0)==1) {
                    sum+=Math.log(varM3.get(m,0));
                } else {
                    sum+=Math.log(1-varM3.get(m,0));
                }
            }
            sum=-sum/Y.rows;


            varM3.subi(Y);

            varM4=new DoubleMatrix(varM3.toArray2());
            MatrixFunctions.absi(varM4);
          //  if (varM4.sum()/X.rows>previous_error) break;
            previous_error=varM4.sum()/X.rows;


            varM3 = X.transpose().mmul(varM3);

            DoubleMatrix thetas_p=new DoubleMatrix(thetas.toArray2());

            DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

            thetas1=thetas1.put(0, 0, 0);



            varM3.muli(alpha / X.rows);


            thetas1.muli(lamda *alpha/ X.rows);


            thetas.subi(varM3);

            thetas.subi(thetas1);

            thetas_p=MatrixFunctions.absi(thetas_p.subi(thetas));


            if (thetas_p.sum()<0.005) {
                System.out.println();
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



    public  void test(){

        double[][] var0=new double[2+1][1];

        for(int i=0;i<2+1;i++) {
            var0[i][0]=1.0;
        }



        DoubleMatrix thetas=new DoubleMatrix(var0);
        thetas.transpose();

        int maxIteration=10000;

        double[][] XX={{ 1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1
                ,1},{ -0.0176,-1.3956,-0.7522,-1.3224,0.4234,0.4067,0.6674, -2.4602
                ,0.5694
                 ,       -0.0266
                ,0.8504
                ,1.3472
                ,1.1768
                 ,       -1.7819
                  ,      -0.5666
                ,0.9316
                 ,       -0.0242
                  ,      -0.0365
                   ,     -0.1969
                ,1.0145
                ,1.9853
                 ,       -1.6935
                  ,      -0.5765
                   ,     -0.3468
                    ,    -2.1245
                ,1.2179
                 ,       -0.7339
                  ,      -3.6420
                ,0.3160
                ,1.4166
                 ,       -0.3863
                ,0.5569
                ,1.2249
                 ,       -1.3478
                ,1.1966
                ,0.2752
                ,0.4706
                 ,       -1.8896
                  ,      -1.5279
                   ,     -1.1852
                    ,    -0.4457
                ,1.0422
                 ,       -0.6188
                ,1.1521
                ,0.8285
                 ,       -1.2377
                  ,      -0.6836
                ,0.2295
                 ,       -0.9599
                ,0.4929
                ,0.1850
                 ,       -0.3557
                  ,      -0.3978
                ,0.8248
                ,1.5073
                ,0.0997
                 ,       -0.3440
                ,1.7859
                 ,       -0.9188
                  ,      -0.3640
                   ,     -0.8417
                ,0.4904
                 ,       -0.0072
                ,0.3561
                ,0.3426
                 ,       -0.8108
                ,2.5308
                ,1.2967
                ,0.4755
                ,        -0.7833
                ,0.0748
                 ,       -1.3375
                  ,      -0.1028
                   ,     -0.1473
                ,0.5184
                ,1.0154
                 ,       -1.6581
                ,1.3199
                ,2.0562
                 ,       -0.8516
                  ,      -1.5100
                   ,     -1.0766
                ,1.8211
                ,3.0101
                 ,       -1.0995
                  ,      -0.8349
                   ,     -0.8466
                ,1.4001
                ,1.7528
                ,0.0786
                ,0.0894
                ,1.8257
                ,0.1974
                ,0.1261
                 ,       -0.6798
                ,0.6780
                ,0.7613
                 ,       -2.1688
                ,1.3886
                ,0.3170},{ 14.0531
                ,4.6625
                ,6.5386
                ,7.1529
                ,11.0547
                ,7.0673
                ,12.7415
                ,6.8668
                ,9.5488
                ,10.4277
                ,6.9203
                ,13.1755
                ,3.1670
                ,9.0980
                ,5.7490
                ,1.5895
                ,6.1518
                ,2.6910
                ,0.4442
                ,5.7544
                ,3.2306
                ,        -0.5575
                ,11.7789
                ,        -1.6787
                ,2.6725
                ,9.5970
                ,9.0987
                ,        -1.6181
                ,3.5240
                ,9.6192
                ,3.9893
                ,8.2950
                ,11.5874
                ,        -2.4061
                ,4.9519
                ,9.5436
                ,9.3325
                ,9.5427
                ,12.1506
                ,11.3093
                ,3.2973
                ,6.1052
                ,10.3210
                ,0.5485
                ,2.6760
                ,10.5490
                 ,       -2.1661
                ,5.9219
                ,11.5553
                ,10.9933
                ,8.7215
                ,10.3260
                ,8.0584
                ,13.7303
                ,5.0279
                ,6.8358
                ,10.7175
                ,7.7186
                ,11.5602
                ,4.7473
                ,4.1191
                ,1.9605
                ,9.0758
                ,12.4479
                ,12.2812
                 ,       -1.4660
                ,6.4768
                ,11.6076
                ,12.0400
                ,11.0097
                ,11.0236
                ,0.4683
                ,13.7637
                ,2.8748
                ,9.8870
                ,7.5719
                ,        -0.0273
                ,2.1712
                ,5.0200
                ,4.3757
                ,6.0620
                ,        -3.1819
                ,10.2840
                ,8.4018
                ,1.6883
                 ,       -1.7339
                ,3.8491
                ,12.6288
                ,5.4682
                ,0.0597
                 ,       -0.7153
                ,12.6938
                ,9.7446
                ,0.9223
                ,1.2205
                ,2.5567
                ,10.6939
                ,0.1436
                ,9.3420
                ,14.7390
        }};


        DoubleMatrix X=new DoubleMatrix(XX).transpose();
        double [][] YY={{   0
               , 1
               , 0
               , 0
               , 0
               , 1
               , 0
               , 1
               , 0
               , 0
               , 1
               , 0
               , 1
               , 0
               , 1
               , 1
               , 1
               , 1
               , 1
               , 1
               , 1
               , 1
               , 0
               , 1
               , 1
               , 0
               , 0
               , 1
               , 1
               , 0
               , 1
               , 1
               , 0
               , 1
               , 1
               , 0
               , 0
               , 0
               , 0
               , 0
               , 1
               , 1
               , 0
               , 1
               , 1
               , 0
               , 1
               , 1
               , 0
               , 0
               , 0
               , 0
               , 0
               , 0
               , 1
               , 1
               , 0
               , 1
               , 0
               , 1
               , 1
               , 1
               , 0
               , 0
               , 0
               , 1
               , 1
               , 0
               , 0
               , 0
               , 0
               , 1
               , 0
               , 1
               , 0
               , 0
               , 1
               , 1
               , 1
               , 1
               , 0
               , 1
               , 0
               , 1
               , 1
               , 1
               , 1
                ,0
               , 1
               , 1
               , 1
               , 0
               , 0
               , 1
               , 1
               , 1
               , 0
               , 1
               , 0
               , 0}};

        DoubleMatrix Y=new DoubleMatrix(YY).transpose();
        double lamda=99.99;
        double alpha=1;
        Y=Y.transpose();
        DoubleMatrix varM4=new DoubleMatrix();
        for(int k=0;k<maxIteration;k++) {


            double sum=0;

            DoubleMatrix varM1 = new DoubleMatrix(X.transpose().toArray2());


            varM1 = varM1.transpose().mmul(thetas);


            DoubleMatrix varM2 = new DoubleMatrix(varM1.rows, varM1.columns);

            varM2.subi(varM1);



            MatrixFunctions.expi(varM2);



            varM2.addi(1);


            DoubleMatrix varM3 = new DoubleMatrix(varM2.rows, varM2.columns);
            varM3.addi(1);
            varM3.divi(varM2);

            for(int m=0;m<Y.rows;m++) {
                if(Y.get(m,0)==1) {
                    sum+=Math.log(varM3.get(m,0));
                } else {
                    sum+=Math.log(1-varM3.get(m,0));
                }
            }
            sum=-sum/Y.rows;

           // System.out.print(sum+",");


           // System.out.println(varM3.rows+" "+varM3.columns);


            varM3.subi(Y.transpose());



            varM4=new DoubleMatrix(varM3.toArray2());
            MatrixFunctions.absi(varM4);
            //  if (varM4.sum()/X.rows>previous_error) break;



            varM3 = X.transpose().mmul(varM3);

            DoubleMatrix thetas_p=new DoubleMatrix(thetas.toArray2());

            DoubleMatrix thetas1 = new DoubleMatrix(thetas.toArray2());

            thetas1=thetas1.put(0, 0, 0);


        //    outputMatrix(thetas.transpose(),"asd");
            varM3.muli(0.01);
            thetas1.muli(lamda *0.01);
            thetas.subi(thetas1);
            thetas.subi(varM3.transpose());

            thetas_p=MatrixFunctions.absi(thetas_p.subi(thetas));

          //  System.out.print(thetas.get(2,0)+" ");
/*
            if (thetas_p.sum()<0.005) {
                System.out.println();
                System.out.println(k);
                break;
            }
*/
        }
        System.out.println();
        outputMatrix(thetas,"thetas");

        System.out.println(Math.sqrt(thetas.transpose().mmul(thetas).sum()));
        // System.out.print(Math.abs(thetas.get(2,0)-thetas.get(1,0)));
        //outputMatrix(thetas.transpose(),"parameter");


    }


    public static void main(String[] args) {
        new LRWeightLearning().test();


      //  double[][] dd={{1,2,3,4,5}};
       // DoubleMatrix d=new DoubleMatrix(dd);
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


        return new pair<>(X, Y);
    }
}
