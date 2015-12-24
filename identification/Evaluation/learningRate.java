package Evaluation;

import preprocessing.IniFile;

/**
 * Created by leisun on 15/12/22.
 */
public class learningRate {
    IniFile iniFile=new IniFile();
    String trainingPath;
    String textPath;

    public learningRate() {
        trainingPath=iniFile.getTrainingDataInputPath();
        textPath=iniFile.getTextPath();
    }
}
