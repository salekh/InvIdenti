package preprocessing;

import DatasetGenerator.PatentsGenerator;
import base.pair;
import base.patent;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierCluster;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.core.LanguageCode;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Using a training dataset to initialize the parameter value
 * Created by sunlei on 15/10/16.
 */
public class ParaIni extends ParameterLearning{

    protected static Logger logger= LogManager.getLogger(ParaIni.class.getName());

    /**
     * @beta used for guess the parametervalue
     */

    double beta=-1;



    ArrayList<pair<int[],Double>> logisticRTrainingData=new ArrayList<>();

    double threshold=-Double.MAX_VALUE;

    public ParaIni(ArrayList<patent> patents,ArrayList<String> patentsID) {

        super();

    }

/*



    public pair<CosDistance,Double> estimateParabyLR(int lamda) {

        ArrayList<String> optionsName=ini.getOptionsNames();
        double[] sums=new double[optionsName.size()];

        ArrayList<AbstractDistance> distances=new ArrayList<>();
        int numberofOptions=0;

        for(int i=0;i<optionsName.size();i++) {
            if(ini.getOptionValue(optionsName.get(i))) {
                ArrayList<Integer> var1 = new ArrayList<>();
                var1.add(i);
                distances.add(this.generateDistanceFunction(var1, null));
                numberofOptions++;
            }
        }
        logger.error(distances.size()+" "+numberofOptions);

        for(int i=0;i<this.trainingPatents.size()-1;i++) {
            for (int j=i+1;j<this.trainingPatents.size();j++)
            {
                int[] tempint=new int[2];
                tempint[0]=i;
                tempint[1]=j;
                double result;
                if (trainingPatentsID.get(i).equalsIgnoreCase(trainingPatentsID.get(j))) {
                    result=1.0;
                }
                 else {
                    result=0.0;
                }
                this.logisticRTrainingData.add(new pair<>(tempint,result));
            }
        }

        pair<double[][],double[]> computation=logisticRTrainingDataGenerator(distances,numberofOptions);
        double[][] X=computation.firstarg;
        double[] YY=computation.secondarg;
        double[][] Y=new double[this.logisticRTrainingData.size()][1];

        for(int i=0;i<this.logisticRTrainingData.size();i++) {
            Y[i][0]=YY[i];
        }

        double[][] xx=new double[8][8];
        for(int i=0;i<8;i++) {
            double sum=0.0;
            for(int j=0;j<8;j++) {
                for(int m=0;m<this.logisticRTrainingData.size();m++) {
                    sum+=X[m][i]*X[m][j];
                }
                xx[i][j]=sum;
            }
        }

        for(int i=0;i<8;i++) {

            for(int j=0;j<8;j++) {
                System.out.print(xx[i][j]+" ");
                }
               System.out.println(";");
            }


        double[] yy=new double[8];

        for(int i=0;i<8;i++) {
           double sum=0.0;

            for(int m=0;m<this.logisticRTrainingData.size();m++) {
                sum+=X[m][i]*YY[m];
            }

            yy[i]=sum;


        }


        for(int i=0;i<8;i++) {


            System.out.println(yy[i]+" ");
        }

        System.out.println(";");

        /*
        DoubleMatrix2D x=new DenseDoubleMatrix2D(X);
        DoubleMatrix2D y=new DenseDoubleMatrix2D(Y);
        DoubleMatrix2D z=new DenseDoubleMatrix2D(numberofOptions+1,numberofOptions+1);
        z.assign(0.0);

        for (int i=1;i<numberofOptions+1;i++) {
            z.set(i,i,1.0);
        }

        for (int i=0;i<this.logisticRTrainingData.size();i++) {
            for (int j=0;j<numberofOptions+1;j++) {
                System.out.print(x.get(i,j)+" ");
            }
            System.out.println(";");
        }

        for (int i=0;i<this.logisticRTrainingData.size();i++) {

                System.out.print(y.get(i,0)+",");

        }
        System.out.println(";");

        double sum=0;
        for(int i=0;i<this.logisticRTrainingData.size();i++) {
            sum+=X[i][4]*X[i][4];
            logger.warn(sum+" "+i);
        }

        logger.warn(sum);

        return new pair<>(new CosDistance(),0.0);

    }

    public pair<double[][],double[]> logisticRTrainingDataGenerator(ArrayList<AbstractDistance> distances,int optionNumber) {
        double[][] X=new double[this.logisticRTrainingData.size()][optionNumber+1];
        double[] Y=new double[this.logisticRTrainingData.size()];

        int i=0;


        for(pair<int[],Double> p:this.logisticRTrainingData) {
            X[i][0]=1.0;
            for(int j=0;j<optionNumber;j++) {
                X[i][j+1]=distances.get(j).distance(trainingPatents.get(p.firstarg[0]),trainingPatents.get(p.firstarg[1]));
                if(X[i][j+1]==0.0) X[i][j+1]=0.001;
            }
            Y[i]=p.secondarg;
            i++;
        }

        return new pair<>(X,Y);
    }

*/

    public AbstractDistance estimateDistanceFunction() {
        return estimatePara(4);

    }

    public CosDistance estimatePara(double beta) {
        ArrayList<Double> weights=new ArrayList<>();
        double[] sums=new double[numberofOptions];
        for(double var0:sums){
            var0=0.0;
        }


        for(int i=0;i<patents.size()-1;i++)
        {
            for (int j=i+1;j<patents.size();j++) {
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    for(int m=0;m<optionsName.size();m++) {
                        if(ini.getOptionValue(optionsName.get(m))) {
                            sums[m] += distances.get(m).distance(patents.get(i), patents.get(j));
                        }
                    }
                }
            }
        }


        for(int i=0;i<optionsName.size();i++) {
            if (ini.getOptionValue(optionsName.get(i))) {
                double var2 = 0;
                for (int j = 0; j < optionsName.size(); j++) {
                    if (ini.getOptionValue(optionsName.get(j))) {
                        var2 += Math.pow(sums[i] / sums[j], (1 / (beta - 1)));
                    }
                }
               // logger.warn(1 / var2);
                weights.add(Math.pow(1 / var2, beta));
            } else {
                weights.add(0.0);
            }
        }

/*
        for(Double d:this.weights) {
            logger.info(d);

        }
*/

        //weights.set(8,weights.get(8)/5);
        CosDistance estimatedDistance=this.generateDistanceFunction(null,weights);



        double min=Double.MAX_VALUE;
        double max=-Double.MAX_VALUE;
        double sum=0;
        double n=0;
        for(int i=0;i<patents.size()-1;i++)
        {
            for (int j=i+1;j<patents.size();j++) {
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    double var5=estimatedDistance.distance(patents.get(i),patents.get(j));
                    sum+=var5;
                    n++;
                    if (var5>max) max=var5;
                    if (var5<min) min=var5;
                }
            }
        }
        this.threshold=max;


      //  logger.info("threshold:"+this.threshold);



        return estimatedDistance;
    }





    public static double evaluateClustering(ArrayList<HierCluster> clusters,ArrayList<String> patentsID) {
        int TN, TP, FN, FP;
        TP = FP = 0;
        for (HierCluster c : clusters) {
            for (int i = 0; i < c.getPatentsIndex().size(); i++) {
                for (int j = i + 1; j < c.getPatentsIndex().size(); j++) {
                    if (patentsID.get(c.getPatentsIndex().get(i)).equalsIgnoreCase(patentsID.get(c.getPatentsIndex().get(j)))) {
                        TP++;
                    }
                    FP++;
                }
            }
        }

        FP = FP - TP;
        TN = FN = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                for (Integer var1 : clusters.get(i).getPatentsIndex()) {
                    for (Integer var2 : clusters.get(j).getPatentsIndex()) {
                        if (patentsID.get(var1).equalsIgnoreCase(patentsID.get(var2))) {
                            FN++;
                        }
                        TN++;
                    }
                }
            }
        }
        TN = TN - FN;


        double precision;
        double recall;
       // System.out.println(TP + " " + FP + " " + " " + TN + " " + FN);
        if ((TP + FP) != 0) {
            precision = (double) TP / (TP + FP);
        } else {
            precision = 0;
        }
        if ((TP + FN) != 0) {
            recall = (double) TP / (TP + FN);

        } else {
            recall = 0;
        }

        if ((precision + recall) != 0) {
            return (double) 2 * precision * recall / (precision + recall);
        }
        else  {
            return 0;
        }
    }

    public static void main(String[] args) {

        IniFile ini=new IniFile();
        String trainingDataPath=ini.getTrainingDataOutputPath()+"/trainingData.db";
        String trainingTextPath=ini.getTrainingDataOutputPath()+"/PatentsText/";
        String infoDataPath=ini.getInfoDataPath();

        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);

        pair<ArrayList<patent>,ArrayList<String>> var0=patentGenerator.getTrainingPatents("TrainingData",1,290);

        ArrayList<patent>patentsI=var0.firstarg;
        ArrayList<String> patentsIDI=var0.secondarg;

        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();

        ArrayList<patent> trainingPatents=new ArrayList<>();
        ArrayList<String> trainingPatentsID=new ArrayList<>();


/*
        pair<ArrayList<patent>,ArrayList<String>> var1=patentGenerator.getTrainingPatents("TrainingData",1,290);
        this.testingPatents=var1.firstarg;
        this.testingPatentsID=var1.secondarg;
*/

        patentGenerator.closeDatabase();

        ArrayList<Integer> shuffleIndex=new ArrayList<>();
        for(int i=0;i<290;i++) {
            shuffleIndex.add(i);
        }


        Collections.shuffle(shuffleIndex);
        for (int i=0;i<290;i++) {
            patents.add(patentsI.get(shuffleIndex.get(i)));
            patentsID.add(patentsIDI.get(shuffleIndex.get(i)));
        }

/*
            patentPreprocessing preprocess = new patentPreprocessing(this.trainingPatents);
            preprocess.setLanguage(this.language);
            preprocess.preprocess();
            this.trainingPatents = preprocess.getPatents();


            estimateParabyLR(2);
*/


        logger.info("Training Data Size:"+patentsID.size());

        String var2="";
        int var3=1000;
        for(;var3>1;var3--) {
            var2+="=";
        }

        logger.error("");
        logger.info("Patent Sample" + var2);
        logger.error(patents.get(0));
        logger.info(var2);


        trainingPatents.clear();
        trainingPatentsID.clear();

        ArrayList<patent> testingP = new ArrayList<>();
        ArrayList<String> testingID = new ArrayList<>();

        for(int i=0;i<290;i++) {


            if (i >120&&i<181) {
                testingP.add(patents.get(i));
                testingID.add(patentsID.get(i));
            } else {

                trainingPatents.add(patents.get(i));
                trainingPatentsID.add(patentsID.get(i));
            }
        }

        patentPreprocessing preprocess = new patentPreprocessing(trainingPatents);
        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        trainingPatents = preprocess.getPatents();



        CosDistance d= (CosDistance)new ParaIni(trainingPatents,trainingPatentsID).estimateDistanceFunction();

        logger.error("");
        logger.info("Distance Function"+var2);
        logger.error(d);
        logger.info(var2);

        logger.warn(testingP.size()+" "+testingID.size());

        HierClusteringPatents hiT=new HierClusteringPatents();
        hiT.ininitialize(testingP);

        hiT.Cluster(d);
        hiT.setEps(Double.MAX_VALUE);
        logger.warn(hiT);
        logger.warn("F measure: "+ParaIni.evaluateClustering(hiT.getHier_clusters(), testingID));


           /*
            double dmax=0;
            double maxf=0;



            ArrayList<Double> matlab=new ArrayList<>();
             HierClusteringPatents hi= new HierClusteringPatents(testingPatents);

            logger.warn("Rate From 0.8 to 0.9");
            for(double d=1.2;d>0.2;d-=0.1) {

               hi.setEps(threshold*d);
               hi.Cluster(distance);
                double tempf=evaluateClustering(hi.getHier_clusters(), testingPatentsID);
                if (tempf>maxf) {
                    maxf=tempf;
                    dmax=d;
                }
               matlab.add(tempf);
               logger.error("F-Measure for " + d + " :" + tempf);

           }




            for(double d1:matlab){
                System.out.print(d1+" ");
            }

            System.out.println();





        logger.warn("Best Threshold: "+threshold*dmax);
        logger.info(var2);
        logger.error(" ");

      //  logger.error("Evaluation"+var2);


 /*       HierClusteringPatents hiT=new HierClusteringPatents(this.testingPatents);

        for(int i=2;i<3;i++) {
            logger.warn("beta "+i+" "+betaEvaluation(i,hiT));
        }
        CosDistance distance=new CosDistance();


        logger.info(var2);
        hiT.setEps(Double.MAX_VALUE);
         //hiT.setNumberofCluster(7);
        hiT.Cluster(distance);
        logger.warn(hiT);
        logger.warn("F measure: "+evaluateClustering(hiT.getHier_clusters(), testingPatentsID));
**/


    }



}
