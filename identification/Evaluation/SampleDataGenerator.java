package Evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preprocessing.IniFile;

import java.sql.*;

/**
 * Created by leisun on 15/12/24.
 */
public class SampleDataGenerator {
    private int numberofSample=5000;
    private String originalDatabasePath;
    private String outputSampleDataPAth;
    private static Logger logger= LogManager.getLogger(SampleDataGenerator.class.getName());

    public SampleDataGenerator(){
        IniFile iniFile=new IniFile();
        originalDatabasePath=iniFile.getTrainingDataOutputPath();
        outputSampleDataPAth=iniFile.getSamplePath();
        logger.info(originalDatabasePath);
        logger.info(outputSampleDataPAth);
        generateTheSampleData();

    }

    private void generateTheSampleData(){

        Connection connection_original=null;
        Statement stmt_original=null;
        Connection connection_sample=null;
        Statement stmt_sample=null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection_original = DriverManager.getConnection("jdbc:sqlite:"+originalDatabasePath+"/trainingData.db");
            //connection_sample = DriverManager.getConnection("jdbc:sqlite:"+outputSampleDataPAth+"/trainingData.db");
            stmt_original=connection_original.createStatement();
            String var0="select * from TrainingData order by Random() limit 10";
            ResultSet var1=stmt_original.executeQuery(var0);
            while (var1.next()) {
                String patent_number=var1.getString("Patent");

                logger.warn(patent_number);

               
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args){
        SampleDataGenerator sampleDataGenerator=new SampleDataGenerator();

    }
}
