package preprocessing;

import base.patent;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.Document;
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

    private static Logger log= LogManager.getLogger(ParaIni.class.getName());
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    int size=50;
    Connection connectionTraining=null;
    Statement stmtTraining=null;
    Connection connectionInfo=null;
    Statement stmtInfo=null;
    LanguageCode language = LanguageCode.ENGLISH;

    /**
     * @beta used for guess the parametervalue
     */


    double beta=4;

    ArrayList<patent> patents=new ArrayList<>();
    ArrayList<patent> testP=new ArrayList<>();
    ArrayList<String> patentsID= new ArrayList<>();

    ArrayList<Double> weights=new ArrayList<>();

    double threshold=-Double.MAX_VALUE;

    public ParaIni() {
        Wini iniFile= null;
        try {
            iniFile = new Wini(new File("invidenti.ini"));
            this.trainingDataPath=iniFile.get("DataSet","TrainingDataOutputPath")+"/trainingData.db";
            this.trainingTextPath=iniFile.get("DataSet","TrainingDataOutputPath")+"/PatentsText/";
            this.infoDataPath=iniFile.get("DataSet","InfoDataPath");
            log.info(this.trainingDataPath);
            log.info("Initial File Loaded!");
            Class.forName("org.sqlite.JDBC");
            this.connectionTraining = DriverManager.getConnection("jdbc:sqlite:" + this.trainingDataPath);
            this.connectionTraining.setAutoCommit(false);
            this.stmtTraining=connectionTraining.createStatement();
            this.connectionInfo = DriverManager.getConnection("jdbc:sqlite:" + this.infoDataPath);
            this.connectionInfo.setAutoCommit(false);
            this.stmtInfo=connectionInfo.createStatement();
            log.info("Opened database successfully");

            this.getTrainingPatents("TrainingData");
            log.info(patentsID.size());


           // System.out.println(this.patents.get(90).getPatent_number());


            patentPreprocessing preprocess = new patentPreprocessing(this.patents);
            preprocess.setLanguage(this.language);
            preprocess.preprocess();
            this.patents = preprocess.getPatents();
            //System.out.println(this.patents.size());
            this.estimatePara();
            double[] weight={0,this.weights.get(0),this.weights.get(1),this.weights.get(2),this.weights.get(3),this.weights.get(4)};

            //patent test=this.getOnePatent("7865343","invpat");
            //String str=test.getAbs()+test.getClaims()+test.getDescription();
            //log.info(str);
            HierClusteringPatents hi=new HierClusteringPatents(this.patents);
            hi.setEps(this.threshold);
            //hi.setEps(0);
            CosDistance var0=new CosDistance();
            var0.setWeights(weight);

            hi.Cluster(var0);
            //System.out.println(hi.toString());
            log.info(hi.toString());

            //log.info(test.getAuthor()+"\n "+test.getAssignee()+"\n"+test.getCategory()+"\n"+test.getTitle()+"\n"+test.getClaims()+"\n"+test.getAbs());
            this.stmtInfo.close();
            this.stmtTraining.close();
            this.connectionInfo.close();
            this.connectionTraining.close();


        } catch (IOException e) {
            log.error("Initial File fail");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
           log.error("Database Initialization fail");
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
                patent var2=new patent(patent,abs,claims,description,title,category,assignee,authorLastName);

                return var2;
                }
            }
            return null;
        } catch (SQLException e) {
           log.error("No Information found for patent:"+patentNumber);
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
            int var1=size;
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


    public void estimatePara() {
        double d1,d2,d3,d4,d5;
        d1=d2=d3=d4=d5=0.0;
        CosDistance distance=new CosDistance();
        for(int i=0;i<patents.size()-1;i++)
        {
            for (int j=i+1;j<patents.size();j++) {
                if (!patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {

                    boolean[] dd1={false,true,false,false,false,false};
                    distance.setOptions(dd1);
                    d1+=distance.distance(patents.get(i),patents.get(j));
                    boolean[] dd2={false,false,true,false,false,false};
                    distance.setOptions(dd2);
                    d2+=distance.distance(patents.get(i),patents.get(j));
                    boolean[] dd3={false,false,false,true,false,false};
                    distance.setOptions(dd3);
                    d3+=distance.distance(patents.get(i),patents.get(j));
                    boolean[] dd4={false,false,false,false,true,false};;
                    distance.setOptions(dd4);
                    d4+=distance.distance(patents.get(i),patents.get(j));
                    boolean[] dd5={false,false,false,false,false,true};
                    distance.setOptions(dd5);
                    d5+=distance.distance(patents.get(i),patents.get(j));
                }
            }
        }

        ArrayList<Double> var0=new ArrayList<>();
        var0.add(d1);
        var0.add(d2);
        var0.add(d3);
        var0.add(d4);
        var0.add(d5);
        log.info("d:"+d1+" "+d2+" "+d3+" "+d4+" "+d5);
        for (Double var1:var0) {
            double var2=0;
            for (Double var3:var0) {
               var2+=Math.pow(var1/var3,(1/(beta-1)));
            }
            this.weights.add(Math.pow(1/var2,beta));

        }
        for (double w:weights) {
            log.error(w);
        }
        double min=Double.MAX_VALUE;
        double max=-Double.MAX_VALUE;
        for(int i=0;i<patents.size()-1;i++)
        {
            for (int j=i+1;j<patents.size();j++) {
                if (!patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    double sum=0;
                    boolean[] dd1={false,true,false,false,false,false};
                    distance.setOptions(dd1);
                    sum+=distance.distance(patents.get(i),patents.get(j))*this.weights.get(0);
                    boolean[] dd2={false,false,true,false,false,false};
                    distance.setOptions(dd2);
                    sum+=distance.distance(patents.get(i),patents.get(j))*this.weights.get(1);
                    boolean[] dd3={false,false,false,true,false,false};
                    distance.setOptions(dd3);
                    sum+=distance.distance(patents.get(i),patents.get(j))*this.weights.get(2);
                    boolean[] dd4={false,false,false,false,true,false};;
                    distance.setOptions(dd4);
                    sum+=distance.distance(patents.get(i),patents.get(j))*this.weights.get(3);
                    boolean[] dd5={false,false,false,false,false,true};
                    distance.setOptions(dd5);
                    sum+=distance.distance(patents.get(i),patents.get(j))*this.weights.get(4);
                    if (sum>max) max=sum;
                    if (sum<min) min=sum;
                }
            }
        }
        this.threshold=max;
        log.info(this.threshold);
    }

    public static void main(String[] args) {
        new ParaIni();
    }



}
