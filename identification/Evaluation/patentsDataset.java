package Evaluation;

import DatasetGenerator.PatentsGenerator;
import base.pair;
import base.patent;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/15.
 */
public class patentsDataset {

    //IniFile ini;
    String trainingDataPath;
    String trainingTextPath;
    String infoDataPath;
    int dataSize=Integer.MAX_VALUE;
    ArrayList<patent> patents;
    ArrayList<String> patentsID;



    public patentsDataset(String dataPath,String infoPath,int dataSize,String IDType) {
        //ini=new IniFile();

        if (!dataPath.substring(dataPath.length()-3,dataPath.length()).equalsIgnoreCase(".db"))
        {
            this.trainingDataPath=dataPath+"/trainingData.db";
        } else {
            System.out.println(dataPath);
            this.trainingDataPath=dataPath;
        }
        this.trainingTextPath=dataPath+"/PatentsText/";
        infoDataPath=infoPath;
        this.dataSize=dataSize;
        pair<ArrayList<patent>, ArrayList<String>> var0;
        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);
        if(!IDType.equalsIgnoreCase("Benchmark")) {
            patentGenerator.setIDType(IDType);
            var0 = patentGenerator.getTrainingPatentsWithEstimatedID("TrainingData", 1, dataSize);
        }
        else {

            var0=patentGenerator.getTrainingPatents("TrainingData", 1, dataSize);

        }
        patents=var0.firstarg;
        patentsID=var0.secondarg;
        patentGenerator.closeDatabase();
    }




    public patentsDataset(String dataPath,String infoPath,String textPath,int dataSize,String IDType) {
        //ini=new IniFile();

        if (!dataPath.substring(dataPath.length()-3,dataPath.length()).equalsIgnoreCase(".db"))
        {
            this.trainingDataPath=dataPath+"/trainingData.db";
        } else {
            System.out.println(dataPath);
            this.trainingDataPath=dataPath;
        }
        this.trainingTextPath=textPath+"/";
        infoDataPath=infoPath;
        this.dataSize=dataSize;
        pair<ArrayList<patent>, ArrayList<String>> var0;
        PatentsGenerator patentGenerator=new PatentsGenerator(infoDataPath,trainingTextPath,trainingDataPath);
        if(!IDType.equalsIgnoreCase("Benchmark")) {
            patentGenerator.setIDType(IDType);
            var0 = patentGenerator.getTrainingPatentsWithEstimatedID("TrainingData", 1, dataSize);
        }
        else {
            var0=patentGenerator.getTrainingPatents("TrainingData", 1, dataSize);

        }
        patents=var0.firstarg;
        patentsID=var0.secondarg;
        patentGenerator.closeDatabase();
    }






    public pair<ArrayList<patent>,ArrayList<String>>getPatents() {

        return new pair<>(patents,patentsID);
    }

    public pair<ArrayList<patent>,ArrayList<String>>getPatents(int start,int end) {
        ArrayList<patent> var0=new ArrayList<>();
        ArrayList<String> var1=new ArrayList<>();
        for (int i=start;i<end;i++) {
            var0.add(patents.get(i));
            var1.add(patentsID.get(i));
        }
        return new pair<>(var0,var1);
    }

    public pair<ArrayList<patent>,ArrayList<String>>getPatents(ArrayList<Integer> indexes) {
        ArrayList<patent> var0=new ArrayList<>();
        ArrayList<String> var1=new ArrayList<>();
        for (int i=0;i<patents.size();i++) {
            if (indexes.contains(i)) {
                var0.add(patents.get(i));
                var1.add(patentsID.get(i));
            }
        }
        return new pair<>(var0,var1);
    }
}
