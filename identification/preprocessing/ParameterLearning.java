package preprocessing;

import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/3.
 */
public abstract class ParameterLearning {


        public abstract AbstractDistance estimateDistanceFunction();

        protected String trainingDataPath;
        protected String trainingTextPath;
        protected String infoDataPath;
        protected ArrayList<patent> patents;
        protected ArrayList<String> patentsID;
        protected ArrayList<String> optionsName;
        protected int numberofOptions=8;

        protected ArrayList<AbstractDistance> distances;
        IniFile ini;


        public ParameterLearning(ArrayList<patent> patents,ArrayList<String> patentsID){
            ini=new IniFile();
            this.trainingDataPath = ini.getTrainingDataOutputPath() + "/trainingData.db";
            this.trainingTextPath = ini.getTrainingDataOutputPath() + "/PatentsText/";
            this.infoDataPath = ini.getInfoDataPath();
            this.patents=patents;
            this.patentsID=patentsID;
            this.optionsName=ini.getOptionsNames();
            generateSeperatedDisFunctions();
        }


    /**
     * Generate a distance function based on a arraylist of weights and a arraylist of index
     * @param attrIndex distance function index
     * @param weights distance function weights
     * @return the generated distance function
     */
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex,ArrayList<Double> weights) {
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

    /**
     * geneerate all the seperated distance functions based on the options needed
     */

    public void generateSeperatedDisFunctions(){
        ArrayList<String> optionsName=ini.getOptionsNames();


        this.distances=new ArrayList<>();
        int var0=0;

        for(int i=0;i<optionsName.size();i++) {

                ArrayList<Integer> var1 = new ArrayList<>();
                var1.add(i);
                distances.add(this.generateDistanceFunction(var1, null));
                var0++;

        }

        this.numberofOptions=var0;
    }




}
