package Evaluation;

import base.pair;
import base.patent;
import clustering.Dbscan.DBScanClusteringPatents;
import clustering.distancefunction.AbstractDistance;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import preprocessing.IniFile;
import preprocessing.LRWeightLearning;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by sunlei on 15/11/15.
 */
public class main {
    ArrayList<Double> lumpings=new ArrayList<>();
    ArrayList<Double> splittings=new ArrayList<>();
    public double suml=0;
    public double sums=0;
    private static Logger logger= LogManager.getLogger(main.class.getName());


    String traingPath="/Users/leisun/Desktop/ThesisData/TrainingData/E&STest";
    String testingPath="/Users/leisun/Desktop/ThesisData/TrainingData";
    String infoPath="/Users/leisun/Desktop/ThesisData/PatentData/PatTest.sqlite";

    pair<ArrayList<patent>,ArrayList<String>> training;
    pair<ArrayList<patent>,ArrayList<String>> testing;


    public main(int num){
        training=new patentsDataset(traingPath,infoPath,1000,"Benchmark").getPatents();

        subsetofTrainingwithRandomly(num);

        testing=new patentsDataset(testingPath,infoPath,1,"Benchmark").getPatents();

    }

    public void subsetofTrainingwithRandomly(int num){
        ArrayList<Integer> shuffleIndex = new ArrayList<>();
        for (int i = 0; i < training.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }
        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Collections.shuffle(shuffleIndex);
        if (num>training.firstarg.size()) num=training.firstarg.size();
        for (int i = 0; i < num; i++) {
            patents.add(training.firstarg.get(shuffleIndex.get(i)));
            patentsID.add(training.secondarg.get(shuffleIndex.get(i)));
        }
        training=new pair<>(patents,patentsID);

    }

    public void testingWithTraining(){


        Training var2=new Training(training.firstarg,training.secondarg,new LRWeightLearning());
        pair<AbstractDistance,Double> var3=var2.estimateParameter();
        System.out.println(var3.firstarg);

        Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
        e.evaluate(var3.firstarg,var3.secondarg,new HierClusteringPatents());

    }

    public double testingWithTraining(pair<ArrayList<patent>,ArrayList<String>> training,pair<ArrayList<patent>,ArrayList<String>> testing) {

        Training var4=new Training(training.firstarg,training.secondarg,new LRWeightLearning());
        pair<AbstractDistance,Double> var5=var4.estimateParameter();

        System.out.println(var5.firstarg);

        Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
        double FMeasure=e.evaluate(var5.firstarg,var5.secondarg,new HierClusteringPatents());


        this.lumpings.add(e.lumping);
        this.splittings.add(e.splitting);

        return FMeasure;

    }

    public double crossValidate(){




        int k=10;

        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Boolean shuffle=true;
        ArrayList<Integer> shuffleIndex;

        shuffleIndex = new ArrayList<>();
        for (int i = 0; i < training.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }

        if(shuffle) {
            Collections.shuffle(shuffleIndex);
            for (int i = 0; i < training.firstarg.size(); i++) {
                patents.add(training.firstarg.get(shuffleIndex.get(i)));
                patentsID.add(training.secondarg.get(shuffleIndex.get(i)));
            }
        }else {
            patents=training.firstarg;
            patentsID=training.secondarg;
        }

        logger.info("Patents Initialized");
        logger.info("Patents total number:"+patents.size());
        logger.info("");


        int numberoftesting=patents.size()/k;

        ArrayList<Double> FMeasure=new ArrayList<>();
        for(int i=0;i<k;i++){
            int start=i*numberoftesting;
            int end=start+numberoftesting;
            if (end>patents.size()-1) end=patents.size()-1;
            pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> var0=this.getTrainingandTesingPatents(start,end,patents,patentsID,shuffleIndex);
            FMeasure.add(testingWithTraining(var0.firstarg,var0.secondarg));

        }

        logger.info("");
        double sum=0;
        for(double d:FMeasure) {
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error(sum/k);
        double F1=sum/k;
        sum=0;
        for(double d:lumpings) {
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        sum=0;
        for(double d:splittings) {
            sum+=d;
        }
        sums=sum/k;
        logger.error("Average splitting:"+sum/k);
        return (F1);
    }

    public pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> getTrainingandTesingPatents(int start, int end, ArrayList<patent> patents, ArrayList<String> patentsID, ArrayList<Integer> shuffleIndex) {
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


    public static void main(String[] args) {

        double sum=0;
        double sumL=0;
        double sumS=0;
        ArrayList<Double> F1s=new ArrayList<>();
        ArrayList<Double> lumpings=new ArrayList<>();
        ArrayList<Double> splittings=new ArrayList<>();

        for(int i=100;i<1001;i+=100) {
         logger.warn("Iteration:"+i);

            main temp=new main(i);
            F1s.add(temp.crossValidate());
            lumpings.add(temp.suml);
            splittings.add(temp.sums);
        }

        for(double d:F1s) {
            System.out.print(d+" ");
        }
        System.out.println();

        for(double d:lumpings) {
            System.out.print(d+" ");
        }
        System.out.println();

        for(double d:splittings) {
            System.out.print(d+" ");
        }
        System.out.println();

       // new main().testingWithTraining();

    }



}
