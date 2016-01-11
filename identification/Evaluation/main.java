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
    ArrayList<Double> lumpings=new ArrayList<>();
    ArrayList<Double> splittings=new ArrayList<>();
    public double suml=0;
    public double sums=0;
    private static Logger logger= LogManager.getLogger(main.class.getName());

    IniFile ini=new IniFile();
    String traingPath="/Users/leisun/Desktop/ThesisData/ES/training.db";
    String testingPath="/Users/leisun/Desktop/ThesisData/TrainingData/E&STest";
    String infoPath="/Users/leisun/Desktop/ThesisData/ES/PatTest.sqlite";

    pair<ArrayList<patent>,ArrayList<String>> training;
    pair<ArrayList<patent>,ArrayList<String>> testing;


    public main(int num){
<<<<<<< HEAD
        training=new patentsDataset(traingPath,infoPath,ini.getTextPath(),4000,"Benchmark").getPatents();
=======
        training=new patentsDataset(traingPath,infoPath,ini.getTextPath(),2000,"Benchmark").getPatents();
>>>>>>> origin/master
        System.out.println(training.firstarg.size());
       subsetofTrainingwithRandomly(num);

//        testing=new patentsDataset(testingPath,infoPath,48,"Benchmark").getPatents();

    }


    public double test(){
        return testingWithTraining(training,null);
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

    public double testingWithTraining(pair<ArrayList<patent>,ArrayList<String>> training,pair<ArrayList<patent>,ArrayList<String>> testing) {

        Training var4=new Training(training.firstarg,training.secondarg,new LRWeightLearning());


        pair<AbstractDistance,Double> var5=var4.estimateParameter();


        storeText("ClusteringDistance.txt",true,var5.firstarg.getWeights()+" "+var5.secondarg+"\n");



        logger.warn(var5.firstarg);
        logger.warn("Threshold: " + var5.secondarg );
        Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
//      SimMatrix s=new SimMatrix(testing.firstarg,var5.firstarg);
  //    s.buildMatrix(var5.secondarg,testing.secondarg);
        double FMeasure=e.evaluate(var5.firstarg,var5.secondarg,new DBScanClusteringPatents());


        this.lumpings.add(e.lumping);
        this.splittings.add(e.splitting);

        return FMeasure;

    }

    public double crossValidate(){

        int k=5;
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

        storeText("ClusteringDistance.txt",true,"Patents:"+patents.size()+"\n");

        double startT=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(patents);

        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        patents = preprocess.getPatents();
        double endT=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(endT-startT));


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
        String result="Patent Numer: "+patents.size()+"\n";
        logger.info("");
        double sum=0;
        for(double d:FMeasure) {
            result+=d+" ";
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error(sum/k);
        double F1=sum/k;
        result+=F1+"\n";
        sum=0;
        for(double d:lumpings) {
            result+=d+" ";
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        result+=suml+"\n";
        sum=0;
        for(double d:splittings) {
            result+=d+" ";
            sum+=d;
        }
        sums=sum/k;
        result+=sums+"\n";
        storeText("Clustering.txt",true,result);
        logger.error("Average splitting:"+sum/k);

        return (F1);
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





        long begintime=System.currentTimeMillis();
        double sum=0;
        double sumL=0;
        double sumS=0;
        ArrayList<Double> F1s=new ArrayList<>();
        ArrayList<Double> lumpings=new ArrayList<>();
        ArrayList<Double> splittings=new ArrayList<>();

<<<<<<< HEAD
        for(int i=2000;i<=18000;i+=2000) {
=======
        for(int i=1000;i<=18000;i+=20000) {
>>>>>>> origin/master
        logger.warn("Size: "+i);
            main temp=new main(i);
            F1s.add(temp.crossValidate());
            lumpings.add(temp.lumpings.get(0));
            splittings.add(temp.splittings.get(0));
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

        long endtime=System.currentTimeMillis();

        System.out.println("Time Cost:"+(endtime-begintime));

    }



}
