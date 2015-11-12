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
    //int[] wrongIndex={305, 124 ,167, 359 ,209 ,59 ,446 ,89 ,156 ,461 ,96 ,150, 429 ,145 ,39 ,79 ,47, 56, 129 ,355 ,456 ,179 ,394 ,44 ,316 ,123 ,409 ,309 ,420 ,424 ,141 ,104 ,369 ,237 ,477 ,388 ,377, 378 ,375 ,387 ,214 ,438 ,453 ,293 ,376 ,400 ,22 ,55 ,457 ,42 ,206 ,290 ,212,459,63,445,64,418,21,82,390,386,115,146,114,105,54,342,319,404,285,292,349,83,339,225,261,255,138,392,15,35,318,384,283,61,210,11,260,116,199,130,32,287,271,313,397,259,217,474};
    //int[] wrongIndex={90,273,197,125,12,136,44,180,222,33,113,151,254,105,116,154,265,165,232,202,249,149,6,218,56,239,215,8,288,80};
    int[] wrongIndex={29,53,88,180,127,100,171,35,153,23,49,16,190,47,56,145,32,97,31,118};
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

    int datasize=1300;
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
                for (int j = 0 ; j < c.getPatentsIndex().size(); j++) {
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
            for (int j = 0; j < clusters.size(); j++) {
                if (j != i) {
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
        }
        TN = TN - FN;


        double precision;
        double recall;
        System.out.println(TP + " " + FP + " " + " " + TN + " " + FN);
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
        logger.info("Threshold Estimated:"+weightlearning.getThreshold());
        logger.info("Training Data Size:"+training.firstarg.size());

        logger.info("Testing Data Size:"+testing.firstarg.size());

        clusteirngMethod.ininitialize(testing.firstarg);
        clusteirngMethod.setThreshold(weightlearning.getThreshold());

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
            if (patents.get(i).getPatent_number().equalsIgnoreCase("05166131")||patents.get(i).getPatent_number().equalsIgnoreCase("07026071"))
            {
                System.out.println(i);
            }
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

        clusteirngMethod.setThreshold(threshold);

        logger.warn(threshold);




         clusteirngMethod.Cluster(d);


        logger.error(clusteirngMethod);


        d.show=false;


//        logger.error(d.distance(patents.get(232),patents.get(239)));



        //logger.error(d.compareAssignee(patents.get(232).getAssignee(),patents.get(239).getAssignee(),patents.get(232).getAsgNum(),patents.get(239).getAsgNum()));

        //logger.error(d.compareLocation(patents.get(232).getCountry(),patents.get(232).getLat(),patents.get(232).getLng(),patents.get(239).getCountry(),patents.get(239).getLat(),patents.get(239).getLng()));


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

            clusteirngMethod.setThreshold(time*threshold);
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


       Evaluation evaluation=new Evaluation(new LRWeightLearning(),new HierClusteringPatents());
        evaluation.evaluteAllClustering();
     //   evaluation.wrongPatents();
    }

}
