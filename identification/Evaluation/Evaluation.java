package Evaluation;

import DatasetGenerator.PatentsGenerator;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierCluster;
import clustering.patentClustering;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preprocessing.IniFile;
import preprocessing.ParameterLearning;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/3.
 */
public class Evaluation {

    private static Logger logger= LogManager.getLogger(Evaluation.class.getName());

    IniFile ini=new IniFile();
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    ParameterLearning weightlearning;
    patentClustering clusteirngMethod;

    boolean shuffle=true;

    int k=5;

    public Evaluation(ParameterLearning p,patentClustering c) {
        ini=new IniFile();
        this.trainingDataPath=ini.getTrainingDataOutputPath()+"/trainingData.db";
        this.trainingTextPath=ini.getTrainingDataOutputPath()+"/PatentsText/";
        infoDataPath=ini.getInfoDataPath();
        this.weightlearning=p;
        this.clusteirngMethod=c;
        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);

        pair<ArrayList<patent>,ArrayList<String>> var0=patentGenerator.getTrainingPatents("TrainingData",1,290);

        ArrayList<patent>patentsI=var0.firstarg;
        ArrayList<String> patentsIDI=var0.secondarg;

        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();

        patentGenerator.closeDatabase();

        if(this.shuffle) {
            ArrayList<Integer> shuffleIndex = new ArrayList<>();
            for (int i = 0; i < 290; i++) {
                shuffleIndex.add(i);
            }


            Collections.shuffle(shuffleIndex);
            for (int i = 0; i < 290; i++) {
                patents.add(patentsI.get(shuffleIndex.get(i)));
                patentsID.add(patentsIDI.get(shuffleIndex.get(i)));
            }
        }else {
            patents=patentsI;
            patentsID=patentsIDI;
        }
    }

    /**
     * Evaluate the clustering result using F-measure
     * @param clusters clustering result
     * @param patentsID true patents clustering index
     * @return F measurement
     */
    public double evaluateClustering(ArrayList<HierCluster> clusters,ArrayList<String> patentsID) {
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

    public double evaluteAClustering(pair<ArrayList<patent>,ArrayList<String>>training,pair<ArrayList<patent>,ArrayList<String>>testing) {
        CosDistance d=(CosDistance)weightlearning.estimateDistanceFunction();
        logger.info(d);
        clusteirngMethod.Cluster(d);
        logger.error(clusteirngMethod);
        logger.warn(evaluateClustering(testing));
    }

}
