package preprocessing;

import base.patent;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierCluster;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import org.ini4j.Wini;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Using a training dataset to initialize the parameter value
 * Created by sunlei on 15/10/16.
 */
public class ParaIni {

    private static Logger logger= LogManager.getLogger(ParaIni.class.getName());
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    int trainingSize=50;
    int tesingSize=100;
    Connection connectionTraining=null;
    Statement stmtTraining=null;
    Connection connectionInfo=null;
    Statement stmtInfo=null;
    LanguageCode language = LanguageCode.ENGLISH;

    /**
     * @beta used for guess the parametervalue
     */


    double beta=4;

    IniFile ini;
    ArrayList<patent> patents=new ArrayList<>();
    ArrayList<patent> testingPatents=new ArrayList<>();
    ArrayList<String> patentsID= new ArrayList<>();
    ArrayList<String> testingPatentsID=new ArrayList<>();

    ArrayList<Double> weights=new ArrayList<>();

    double threshold=-Double.MAX_VALUE;

    public ParaIni() {

        try {

            ini=new IniFile();

            this.trainingDataPath=ini.getTrainingDataOutputPath()+"/trainingData.db";
            this.trainingTextPath=ini.getTrainingDataOutputPath()+"/PatentsText/";
            this.infoDataPath=ini.getInfoDataPath();

            Class.forName("org.sqlite.JDBC");
            this.connectionTraining = DriverManager.getConnection("jdbc:sqlite:" + this.trainingDataPath);
            this.connectionTraining.setAutoCommit(false);
            this.stmtTraining=connectionTraining.createStatement();
            this.connectionInfo = DriverManager.getConnection("jdbc:sqlite:" + this.infoDataPath);
            this.connectionInfo.setAutoCommit(false);
            this.stmtInfo=connectionInfo.createStatement();
            logger.info("Opened database successfully");

            this.getTrainingPatents("TrainingData");
            this.getTestingPatents("TrainingData");
            logger.info(patentsID.size());



            patentPreprocessing preprocess = new patentPreprocessing(this.patents);
            preprocess.setLanguage(this.language);
            preprocess.preprocess();
            this.patents = preprocess.getPatents();

            CosDistance distance=this.estimatePara();



            HierClusteringPatents hi=new HierClusteringPatents(this.testingPatents);





            double dmax=0;
            double maxf=0;



            ArrayList<Double> matlab=new ArrayList<>();
           for(double d=0.0;d<2.1;d+=0.1) {

               hi.setEps(this.threshold*d);
               hi.Cluster(distance);
                double tempf=evaluateClustering(hi.getHier_clusters(), testingPatentsID);
                if (tempf>maxf) {
                    maxf=tempf;
                    dmax=d;
                }
               matlab.add(tempf);
               logger.info("F-Measure for " + d  + " :" + tempf);

           }


            for(double d1:matlab){
                System.out.print(d1+" ");
            }
            System.out.println();
            logger.error("d:"+dmax);
            hi.setEps(this.threshold*dmax);
            hi.Cluster(distance);
            logger.error(hi);



            this.stmtInfo.close();
            this.stmtTraining.close();
            this.connectionInfo.close();
            this.connectionTraining.close();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
           logger.error("Database Initialization fail");
        }

    }

    public patent getOnePatent(String patentNumber,String table,String lastname) {

        String var1;
        var1=patentNumber;
        if (patentNumber.length()<8) {
            for(int i=0;i<8-patentNumber.length();i++) {
                var1="0"+var1;
            }
        }

        String sql="Select * from "+table+" where "+table+".patent"+"='"+var1+"';";


        try {
            ResultSet var0=stmtInfo.executeQuery(sql);
            while (var0.next()) {

                String patent=var0.getString("Patent");
                String authorLastName=var0.getString("Lastname");
                String assignee=var0.getString("Assignee");
                String category=var0.getString("Class");


                String abs=readText(this.trainingTextPath + patentNumber + "/" + "Abstract.txt");
                String claims=readText(this.trainingTextPath + patentNumber + "/" + "Claims.txt");
                String description=readText(this.trainingTextPath+patentNumber+"/"+"Description.txt");
                String title=readText(this.trainingTextPath+patentNumber+"/"+"Title.txt");
                if (authorLastName.equalsIgnoreCase(lastname))
                {
                    if(abs.length()==0||claims.length()==0||description.length()==0) {
                        System.out.println(patent);
                }


                    patent var2=new patent(patent,abs,claims,description,title,category,assignee,authorLastName);

                return var2;
                }
            }
            return null;
        } catch (SQLException e) {
           logger.error("No Information found for patent:"+patentNumber);
        }

        return null;
    }


    public String readText(String path){
        File f= null;
        try {
            f = new File(path);
            if (!f.exists()) {
                return null;
            } else {
                BufferedReader r=new BufferedReader(new FileReader(path));
                String str="";
                String line=r.readLine();
                while(line!=null){
                    str+=line;
                    line=r.readLine();
                }

                //System.out.println(str);
                return str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void getTrainingPatents(String table)  {
        String sql="select * from "+table;
        try {
            ResultSet var0=stmtTraining.executeQuery(sql);
            int var10=0;

            while (var0.next()) {
                if (var10 < 1) {
                    break;

                } else {
                    var10--;
                }
            }



            int var1=trainingSize;
            while (var0.next()) {
                if (var1<1) {
                   break;

                } else {
                    var1--;
                }
                patent var2=this.getOnePatent(var0.getString("Patent"), "invpat",var0.getString("LastName"));
                if (var2!=null)
                {
                    this.patents.add(var2);

                    this.patentsID.add(var0.getString("ID"));
                } else {
                    var1++;
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void getTestingPatents(String table)  {
        String sql="select * from "+table;
        try {
            ResultSet var0=stmtTraining.executeQuery(sql);
            int var1=0;

            while (var0.next()) {
                if (var1 < 1) {
                    break;

                } else {
                    var1--;
                }
            }

            int var3=tesingSize;
            while(var0.next())
            {
                if (var3 < 1) {
                    break;

                } else {
                    var3--;
                }
                patent var2=this.getOnePatent(var0.getString("Patent"), "invpat",var0.getString("LastName"));
                if (var2!=null)
                {
                    this.testingPatents.add(var2);

                    this.testingPatentsID.add(var0.getString("ID"));
                } else {
                    var3++;
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }




    public CosDistance estimatePara() {
        ArrayList<String> optionsName=ini.getOptionsNames();
        double[] sums=new double[optionsName.size()];

        for(double var0:sums) {
            var0=0;
        }

        ArrayList<CosDistance> distances=new ArrayList<>();

        for(int i=0;i<optionsName.size();i++) {
            ArrayList<Integer> var1=new ArrayList<>();
            var1.add(i);
            distances.add(this.generateDistanceFunction(var1,null));
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
                logger.warn(1 / var2);
                this.weights.add(Math.pow(1 / var2, beta));
            } else {
                this.weights.add(0.0);
            }
        }


        for(Double d:this.weights) {
            logger.info(d);

        }

        CosDistance estimatedDistance=this.generateDistanceFunction(null,this.weights);
        logger.error(estimatedDistance);


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


        logger.info("threshold:"+this.threshold);



        return estimatedDistance;
    }


    public  CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex,ArrayList<Double> weights) {
        CosDistance var0=new CosDistance();
        if (attrIndex!=null) {
            boolean[] var1=new  boolean[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    var1[i]=true;
                } else {
                    var1[i]=false;
                }
            }
            var0.setOptions(var1);
        }
        if (weights!=null&&weights.size()>=this.ini.getOptionsNames().size()) {
            double[] var2=new double[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                var2[i]=weights.get(i);
            }

            var0.setWeights(var2);
        }
        return var0;
    }


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
        else {
            return 0;
        }
    }

    public static void main(String[] args) {
        new ParaIni();
        //logger.info(new NormalizedLevenshtein().distance("DE JONGHE","DEJONGHE"));
        //logger.info(new NormalizedLevenshtein().distance("DE JONGHE","CRAWFORD"));
    }



}
