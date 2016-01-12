package Evaluation;

import base.pair;
import base.patent;
import clustering.Dbscan.DBScanClusteringPatents;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.carrot2.core.LanguageCode;
import preprocessing.IniFile;
import preprocessing.LRWeightLearning;
import preprocessing.patentPreprocessingTF;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/15.
 */
public class main {
    ArrayList<Double> lumpings_db=new ArrayList<>();
    ArrayList<Double> splittings_db=new ArrayList<>();
    ArrayList<Double> lumpings_hi=new ArrayList<>();
    ArrayList<Double> splittings_hi=new ArrayList<>();

    public double suml=0;
    public double sums=0;
    private static Logger logger= LogManager.getLogger(main.class.getName());

    IniFile ini=new IniFile();
    String traingPath="/Users/sunlei/Desktop/ThesisData/ES/training.db";
    String testingPath="/Users/sunlei/Desktop/ThesisData/TrainingData/E&STest";
    String infoPath="/Users/sunlei/Desktop/ThesisData/ES/PatTest.sqlite";

    pair<ArrayList<patent>,ArrayList<String>> training;
    pair<ArrayList<patent>,ArrayList<String>> testing;


    public main(int num){

        training=new patentsDataset(traingPath,infoPath,ini.getTextPath(),8000,"Benchmark").getPatents();

        System.out.println(training.firstarg.size());
        subsetofTrainingwithRandomly(num);
        //testing=new patentsDataset(testingPath,infoPath,48,"Benchmark").getPatents();

    }


    public double test(){
        return 0;
        //return testingWithTraining(training,null);
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

        System.out.println(var3.secondarg);

        //Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
        Evaluation e=new Evaluation(training.firstarg,training.secondarg);
        e.evaluate(var3.firstarg,var3.secondarg,new HierClusteringPatents());
       // e.evaluate(new CosDistance(),8.68,new HierClusteringPatents());

    }

    /**
     * Training the model and test the performance on a test dataset.
     * @param training training dataset
     * @param testing testing dataset
     * @return
     */
    public pair<Double,Double> testingWithTraining(pair<ArrayList<patent>,ArrayList<String>> training,pair<ArrayList<patent>,ArrayList<String>> testing) {

        Training var4=new Training(training.firstarg,training.secondarg,new LRWeightLearning());


        pair<AbstractDistance,Double> var5=var4.estimateParameter();

        storeText("ClusteringDistance.txt",true,var5.firstarg.getWeights()+" "+var5.secondarg+"\n");

        logger.warn(var5.firstarg);
        logger.warn("Threshold: " + var5.secondarg );


        Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
        double FMeasure_DB=e.evaluate(var5.firstarg,var5.secondarg,new DBScanClusteringPatents());
        this.lumpings_db.add(e.lumping);
        this.splittings_db.add(e.splitting);
        double FMeasure_Hi=e.evaluate(var5.firstarg,var5.secondarg,new HierClusteringPatents());
        this.lumpings_hi.add(e.lumping);
        this.splittings_hi.add(e.splitting);

        return new pair<>(FMeasure_DB,FMeasure_Hi);

    }

    /**
     * Cross validation
     * @param k K-fold
     * @return the average Fmeasure
     */
    public double crossValidate(int k){


        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Boolean shuffle=true;
        ArrayList<Integer> shuffleIndex;

        shuffleIndex = new ArrayList<>();
        for (int i = 0; i < training.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }

        //Shuffle the patents list
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

        storeText("ClusteringDistance.txt",true,"Patents:"+patents.size()+"\n");

        int numberoftesting=patents.size()/k;
        ArrayList<Double> FMeasure_db=new ArrayList<>();
        ArrayList<Double> FMeasure_hi=new ArrayList<>();

        for(int i=0;i<k;i++){
            logger.info("The"+i+"th cross validation for "+patents.size());
            int start=i*numberoftesting;
            int end=start+numberoftesting;
            if (end>patents.size()-1) end=patents.size()-1;
            pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> var0=this.getTrainingandTesingPatents(start,end,patents,patentsID,shuffleIndex);
            pair<Double,Double> fs=testingWithTraining(var0.firstarg,var0.secondarg);
            FMeasure_db.add(fs.firstarg);
            FMeasure_hi.add(fs.secondarg);
        }



        String result="Patent Numer: "+patents.size()+"\n"+"DBScan:\n";
        logger.info("");
        double sum=0;
        for(double d:FMeasure_db) {
            result+=d+" ";
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error("F measure for the DBScan:"+ sum/k);
        double F1=sum/k;
        result+=F1+"\n";
        sum=0;
        for(double d:lumpings_db) {
            result+=d+" ";
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        result+=suml+"\n";
        sum=0;
        for(double d:splittings_db) {
            result+=d+" ";
            sum+=d;
        }
        sums=sum/k;
        result+=sums+"\n";
        logger.error("Average splitting:"+sum/k);

        result+="Hierarchical Clustering:\n";
        sum=0;
        for(double d:FMeasure_hi) {
            result+=d+" ";
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error("F measure for the Hierarchical Clustering:"+ sum/k);
        F1=sum/k;
        result+=F1+"\n";
        sum=0;
        for(double d:lumpings_db) {
            result+=d+" ";
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        result+=suml+"\n";
        sum=0;
        for(double d:splittings_db) {
            result+=d+" ";
            sum+=d;
        }
        sums=sum/k;
        result+=sums+"\n";
        storeText("Clustering.txt",true,result);
        logger.error("Average splitting:"+sum/k);

        return (F1);
    }



    /**
     * Store the text into a text file
     * @param path text path
     * @param follow rewrite option
     * @param str content to write
     */

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
     * Seperate the patents set into training set and testing set
     * @param start start index
     * @param end end index
     * @param patents patents list
     * @param patentsID patents id list
     * @param shuffleIndex randonm index list
     * @return training set and testing set
     */
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

        return new pair<>(one,second);

    }


    public static void main(String[] args) {
        long begintime=System.currentTimeMillis();





        for(int i=1000;i<=8000;i+=1000) {

        logger.warn("Size: "+i);
            main temp=new main(i);
            temp.crossValidate(5);

        }


        long endtime=System.currentTimeMillis();

        System.out.println("Time Cost:"+(endtime-begintime));

    }



}
