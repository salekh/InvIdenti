package preprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/24.
 */
public class IniFile {

    private ArrayList<String> optionsNames=new ArrayList<>();
    private ArrayList<Boolean> optionsValues=new ArrayList<>();
    private ArrayList<Double> optionsWeights=new ArrayList<>();

    private String trainingDataInputPath;
    private String trainingDataOutputPath;
    private String infoDataPath;

    private static Logger logger= LogManager.getLogger(IniFile.class.getName());

    public IniFile() {

        try {
            Wini ini=new Wini(new File("invidenti.ini"));
            String optionsName=ini.get("DistanceOption","Options");
            String[] var0=optionsName.split(",");

            for(String var1:var0) {
                optionsNames.add(var1);
            }


            for(String var2:var0) {
                String var3=ini.get("DistanceOption",var2+"Compare");
                if (var3==null) {
                    optionsValues.add(false);
                } else {
                    optionsValues.add(var3.equalsIgnoreCase("true"));
                }
            }

           for (String var4:var0) {
               String var5=ini.get("Weights",var4);
               if (var5==null) {
                   optionsWeights.add(0.0);
               } else {
                   optionsWeights.add(Double.parseDouble(var5));
               }
           }

            trainingDataInputPath=ini.get("DataSet","TrainingDataInputPath");
            trainingDataOutputPath=ini.get("DataSet","TrainingDataOutputPath");
            infoDataPath=ini.get("DataSet","InfoDataPath");


        } catch (IOException e) {
            logger.error("Initial File not Found");
        }
    }

    public String getTrainingDataInputPath() {
        return this.trainingDataInputPath;
    }

    public String getTrainingDataOutputPath() {
        return this.trainingDataOutputPath;
    }

    public String getInfoDataPath() {
        return this.infoDataPath;
    }

    public int getOptionIndex(String option) {
        return optionsNames.indexOf(option);
    }

    public boolean getOptionValue(String option) {
        return optionsValues.get(optionsNames.indexOf(option));
    }

    public double getOptionWeight(String option) {
        return optionsWeights.get(this.getOptionIndex(option));
    }

    public ArrayList<String> getOptionsNames() {
        return this.optionsNames;
    }

}