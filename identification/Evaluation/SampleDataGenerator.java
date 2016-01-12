package Evaluation;

import base.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preprocessing.IniFile;

import java.sql.*;

/**
 * Created by leisun on 15/12/24.
 */
public class SampleDataGenerator {
    private int numberofSample=8000;
    private String originalDatabasePath;
    private String outputSampleDataPAth;
    private static Logger logger= LogManager.getLogger(SampleDataGenerator.class.getName());

    public SampleDataGenerator(int numberofSample){
        this.numberofSample=numberofSample;
        IniFile iniFile=new IniFile();
        originalDatabasePath=iniFile.getTrainingDataOutputPath();
        outputSampleDataPAth=iniFile.getSamplePath();

        generateTheSampleData();

    }

    private void generateTheSampleData(){


        Connection connection_original=null;
        Statement stmt_original=null;
        Connection connection_sample=null;
        Statement stmt_sample=null;
        Connection connection_test=null;
        Statement stmt_test=null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection_original = DriverManager.getConnection("jdbc:sqlite:"+outputSampleDataPAth+"/trainingData.db");
            connection_sample = DriverManager.getConnection("jdbc:sqlite:"+outputSampleDataPAth+"sample.db");

            /**Test Data Generator**/
            connection_test = DriverManager.getConnection("jdbc:sqlite:"+outputSampleDataPAth+"testing.db");
            /****/

            stmt_original=connection_original.createStatement();


            String var0="select * from TrainingData order by Random() limit "+35000;
            ResultSet var1=stmt_original.executeQuery(var0);

            stmt_sample=connection_sample.createStatement();
            connection_sample.setAutoCommit(false);

            /**Test Data Generator **/
            stmt_test=connection_test.createStatement();
            connection_test.setAutoCommit(false);
            /****/

            try {
                String var2 = "select count(*) from TrainingData";
                stmt_sample.execute(var2);
                logger.info("Delete the original TrainingData Table");
                String var3= "Drop Table TrainingData";
                stmt_sample.executeUpdate(var3);
            } catch (SQLException e) {
                logger.info("TrainingData Table not exist, now creating the database!");
            }


            /**Test Data Generator**/

            try {
                String var2 = "select count(*) from TrainingData";
                stmt_test.execute(var2);
                logger.info("Delete the original TrainingData Table");
                String var3= "Drop Table TrainingData";
                stmt_test.executeUpdate(var3);
            } catch (SQLException e) {
                logger.info("TrainingData Table not exist, now creating the database!");
            }

            /***/

            logger.info("Create the new TrainigData Table");

            String sql = "CREATE TABLE TrainingData" +
                    "(ID TEXT NOT NULL," +
                    " Patent           TEXT    NOT NULL, " +
                    " LastName         TEXT    NOT NULL, " +
                    " FirstName        TEXT    NOT NULL," +
                    "UNIQUE(ID,Patent,LastName,FirstName))";
            stmt_sample.executeUpdate(sql);



            /**Test Data Generator**/
            stmt_test.executeUpdate(sql);


            logger.warn("Start to transfer data from the original database");
            int i=1;
            while(var1.next()) {
                if (i >= 1 && i <= this.numberofSample) {


                    String var4 = ProgressBar.barString((int) i * 100 / this.numberofSample);
                    System.out.print("\r" + var4);
                    String var7 = "insert into TrainingData (ID,Patent,LastName,FirstName)" + "Values ('" + var1.getString("ID") + "','" +
                            var1.getString("Patent") + "','" + var1.getString("LastName") + "','" + var1.getString("FirstName") + "');";
                    stmt_sample.executeUpdate(var7);
                }
                else {

                    /**Test Data Generator**/

                    String var7 = "insert into TrainingData (ID,Patent,LastName,FirstName)" + "Values ('" + var1.getString("ID") + "','" +
                       var1.getString("Patent") + "','" + var1.getString("LastName") + "','" + var1.getString("FirstName") + "');";
                    stmt_test.executeUpdate(var7);
                }
                i++;
            }
            System.out.println();
            System.out.println(i);
            logger.warn("Finish the transfer");

            stmt_original.close();
            connection_original.close();
            stmt_sample.close();
            stmt_test.close();;
            connection_test.commit();
            connection_test.close();
            connection_sample.commit();
            connection_sample.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args){

            new SampleDataGenerator(8000).generateTheSampleData();
    }
}
