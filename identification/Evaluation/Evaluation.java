package Evaluation;

import DatasetGenerator.PatentsGenerator;
import base.indexCluster;
import base.pair;
import base.patent;
import clustering.NameHierClustering;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierCluster;
import clustering.hierarchy.HierClusteringPatents;
import clustering.invClustering;
import clustering.patentClustering;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import preprocessing.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/3.
 */
public class Evaluation {

    private static Logger logger= LogManager.getLogger(Evaluation.class.getName());
    int[] wrongIndex={122,18,229,133,104,53,171,15,6,247,225,149,240,249,280,142,176,131,267,251,68,66,44,75,288,24,227,103,26,120,34,19,168,213,230,58,175,101,112,32,62,181,37,139,226,124,126,192,94,231,74,173,57,143,221,268,172,27};

    IniFile ini=new IniFile();
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    ParameterLearning weightlearning;
    patentClustering clusteirngMethod;
    ArrayList<patent> patents;
    ArrayList<String> patentsID;
    ArrayList<Integer> shuffleIndex;

    boolean shuffle=true;

    int datasize=290;

    int k=10;

    public Evaluation(ParameterLearning p,patentClustering c) {
        ini=new IniFile();
        this.trainingDataPath=ini.getTrainingDataOutputPath()+"/trainingData.db";
        this.trainingTextPath=ini.getTrainingDataOutputPath()+"/PatentsText/";
        infoDataPath=ini.getInfoDataPath();
        this.weightlearning=p;
        this.clusteirngMethod=c;

        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);

        pair<ArrayList<patent>,ArrayList<String>> var0=patentGenerator.getTrainingPatents("TrainingData",1,datasize);

        ArrayList<patent>patentsI=var0.firstarg;
        ArrayList<String> patentsIDI=var0.secondarg;

        patents=new ArrayList<>();
        patentsID=new ArrayList<>();

        patentGenerator.closeDatabase();

        if(this.shuffle) {
             shuffleIndex = new ArrayList<>();
            for (int i = 0; i < datasize; i++) {
                shuffleIndex.add(i);
            }


            Collections.shuffle(shuffleIndex);
            for (int i = 0; i < datasize; i++) {
                patents.add(patentsI.get(shuffleIndex.get(i)));
                patentsID.add(patentsIDI.get(shuffleIndex.get(i)));
            }
        }else {
            patents=patentsI;
            patentsID=patentsIDI;
        }
        logger.info("Patents Initialized");
        logger.info("Patents total number:"+this.patents.size());
        logger.info("");

    }

    /**
     * Evaluate the clustering result using F-measure
     * @param clusters clustering result
     * @param patentsID true patents clustering index
     * @return F measurement
     */
    public double getFScoreofClustering(ArrayList<indexCluster> clusters, ArrayList<String> patentsID) {
        int TN, TP, FN, FP;
        TP = FP = 0;
        for (indexCluster c : clusters) {
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

    /**
     * Generate training patents between start and end and the rest is the testing data
     * @return training patents & IDs and testing patents & IDs
     */
    public pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> getTrainingandTesingPatents(int start,int end) {
        ArrayList<patent> training=new ArrayList<>();
        ArrayList<patent> testing=new ArrayList<>();
        ArrayList<String> trainingIDs=new ArrayList<>();
        ArrayList<String> testingIDs=new ArrayList<>();

        for(int i=0;i<patents.size();i++) {
            if (i>=start&&i<end) {
                testing.add(patents.get(i));
                System.out.print(shuffleIndex.get(i)+" ");
                testingIDs.add(patentsID.get(i));
            } else {
                training.add(patents.get(i));
                trainingIDs.add(patentsID.get(i));
            }
        }
        logger.info("");

        pair<ArrayList<patent>,ArrayList<String>> one = new pair<>(training,trainingIDs);
        pair<ArrayList<patent>,ArrayList<String>> second = new pair<>(testing,testingIDs);

        return new pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>>(one,second);

    }

    /**
     * Evaluate all K clustering
     */
    public void evaluteAllClustering() {

        int numberoftesting=this.patents.size()/k;





        ArrayList<Double> FMeasure=new ArrayList<>();
        for(int i=0;i<k;i++){
            int start=i*numberoftesting;
            int end=start+numberoftesting;
            if (end>patents.size()-1) end=patents.size()-1;
            pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> var0=this.getTrainingandTesingPatents(start,end);

            FMeasure.add(evaluateAClustering(var0.firstarg,var0.secondarg));
            //calculateTheBestThreshold(var0.firstarg,var0.secondarg);


        }

        logger.info("");
        double sum=0;
        for(double d:FMeasure) {
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error(sum/k);

    }

    /**
     * Evaluate a clustering
     * @param training training patents and training patents index
     * @param testing testing patents and testing patents index
     * @return the F measure of the clustering of the testing patents based on the distance function learnt by the training dataset.
     */
    public double evaluateAClustering(pair<ArrayList<patent>,ArrayList<String>>training,pair<ArrayList<patent>,ArrayList<String>>testing) {




        weightlearning.initilize(training.firstarg,training.secondarg);

        CosDistance d=(CosDistance)weightlearning.estimateDistanceFunction();
        logger.info(d);
        logger.info("THreshoold:"+weightlearning.getThreshold());
        logger.info("Training Data Size:"+training.firstarg.size());



        logger.info("Testing Data Size:"+testing.firstarg.size());
        clusteirngMethod.ininitialize(testing.firstarg);
        ((invClustering)clusteirngMethod).setClusteringThreshold(weightlearning.getThreshold());
        logger.error(weightlearning.getThreshold());
        clusteirngMethod.Cluster(d);
        logger.error("Clustering Result\n");
        logger.error(clusteirngMethod);

        double FMeasurement=getFScoreofClustering(clusteirngMethod.getClustersIndex(),testing.secondarg);

        logger.warn("F Measurement:"+ FMeasurement);

        return FMeasurement;
    }


    public void wrongPatents(){
        ArrayList<Integer> wrongIDs=new ArrayList<>();
        ArrayList<patent> training=new ArrayList<>();
        ArrayList<patent> testing=new ArrayList<>();
        ArrayList<String> trainingIDs=new ArrayList<>();
        ArrayList<String> testingIDs=new ArrayList<>();


        for(int i:this.wrongIndex) {
            wrongIDs.add(i);
        }

        for(int i=0;i<this.patents.size();i++) {
            if (wrongIDs.contains(i)) {
                testing.add(patents.get(i));
                testingIDs.add(patentsID.get(i));
            } else {
                training.add(patents.get(i));
                trainingIDs.add(patentsID.get(i));
            }
        }


        weightlearning.initilize(training,trainingIDs);

        CosDistance d=(CosDistance)weightlearning.estimateDistanceFunction();

        logger.info(d);
        logger.info("Training Data Size:"+training.size());
        logger.info("Testing Data Size:"+testing.size());

        double threshold=weightlearning.getThreshold();

        clusteirngMethod.ininitialize(testing);


        //((HierClusteringPatents)clusteirngMethod).setEps(threshold*di);


            clusteirngMethod.Cluster(d);
            logger.error(clusteirngMethod.getClustersIndex().size());


            double FMeasurement = getFScoreofClustering(clusteirngMethod.getClustersIndex(), testingIDs);

            logger.warn("F Measurement:" + FMeasurement);


    }

    public void calculateTheBestThreshold(pair<ArrayList<patent>,ArrayList<String>>training,pair<ArrayList<patent>,ArrayList<String>>testing) {
        weightlearning.initilize(training.firstarg,training.secondarg);

        CosDistance d=(CosDistance)weightlearning.estimateDistanceFunction();
        double threshold=weightlearning.getThreshold();
        System.out.println("Threshold:"+threshold);

        logger.info(d);
        logger.info("Training Data Size:"+training.firstarg.size());
        logger.info("Testing Data Size:"+testing.firstarg.size());
        clusteirngMethod.ininitialize(testing.firstarg);


        double fmax=-Double.MAX_VALUE;
        double timeM=0.0;

        for(double time=1.2;time>=0.0;time-=0.05) {

            ((HierClusteringPatents)clusteirngMethod).setEps(time*threshold);
            clusteirngMethod.Cluster(d);
            double FMeasurement = getFScoreofClustering(clusteirngMethod.getClustersIndex(), testing.secondarg);
            if (FMeasurement>fmax) {
                fmax=FMeasurement;
                timeM=time;
            }
        }
        logger.error("Best Result:"+fmax+" "+timeM+" "+threshold*timeM);

    }

    public static void main(String[] args){


       Evaluation evaluation=new Evaluation(new LRWeightLearning(),new invClustering());
        evaluation.evaluteAllClustering();

    }

}
