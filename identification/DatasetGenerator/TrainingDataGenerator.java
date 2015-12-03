package DatasetGenerator;

import org.ini4j.Wini;
import preprocessing.USPTOSearch;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/4.
 */
public class TrainingDataGenerator {

    private int size=1000;
    private String inputPath;
    private String outputPath;


    public TrainingDataGenerator() {
        try {
            Wini iniFile=new Wini(new File("invidenti.ini"));
            this.inputPath=iniFile.get("DataSet","TrainingDataInputPath");
            this.outputPath=iniFile.get("DataSet","TrainingDataOutputPath");
            File f = new File(this.outputPath);
            if (!f.exists()) f.mkdirs();
        } catch (IOException e) {
            System.out.println("Initial File not Found");
        }
    }

    /**
     * Set the size of the training dataset
     * @param number the size of the training dataset
     */
    public void setSize(int number) {
        this.size=number;
    }

    /**
     *
     * @return the size of the training data
     */
    public int getSize() {
        return size;
    }

    /**
     * Build the dataset and store it in the output path
     */
    public void buildData() {
        System.out.println("Start to build the training dataset");

        ArrayList<String> patentsIndex=this.buildPatentInfDataSet();


        System.out.println("Start to extract the training patent texts");
        File var0=new File(outputPath+"/PatentsText");

        if (!var0.exists()) {
            var0.mkdirs();
        }


        for(String var1:patentsIndex) {
            USPTOSearch var2=new USPTOSearch(var1);
            String var3=outputPath+"/PatentsText/"+var1;
            var0=new File(var3);

            if (!var0.exists()) {
                var0.mkdirs();
            }

            this.storeText(var3+"/Abstract.txt",var2.getAbs());
            this.storeText(var3+"/Claims.txt",var2.getClaims());
            this.storeText(var3+"/Description.txt",var2.getDescription());
            this.storeText(var3+"/Title.txt",var2.getTitle());
        }

        System.out.println("Finish extracting the texts");


        System.out.println("Finish building the training dataset");
    }

    /**
     *
     * @return the arraylist of the patent in the
     */
    public ArrayList<String> buildPatentInfDataSet() {

        ArrayList<String> patents=new ArrayList<>();

        File var0=new File(inputPath);

        try {
            BufferedReader var1=new BufferedReader(new FileReader(var0));
            String var2=var1.readLine();
            String[] var3=var2.split(",");
            // Check the benchmark file format
            if (var3.length<4) {
                System.out.println("Benchmark file format is not right.");
                return patents;
            }
            if (!(var3[0].equalsIgnoreCase("ID")&&var3[2].equalsIgnoreCase("LastName")&&var3[3].equalsIgnoreCase("FirstName")&&var3[1].equalsIgnoreCase("Patent"))) {

                System.out.println(var3[0]+var3[1]+var3[2]+var3[3]);
                System.out.println("Benchmark file format is not right.");
                return patents;
            }

            Connection connection=null;
            Statement stmt=null;

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+outputPath+"/trainingData.db");

            connection.setAutoCommit(false);
            stmt = connection.createStatement();

            try {
                String var4 = "select count(*) from TrainingData";
                stmt.execute(var4);
            } catch (SQLException e) {
                System.out.println("TrainingData Table not exist, now creating the database!");
                String sql = "CREATE TABLE TrainingData" +
                        "(ID TEXT NOT NULL," +
                        " Patent           TEXT    NOT NULL, " +
                        " LastName         TEXT    NOT NULL, " +
                        " FirstName        TEXT    NOT NULL," +
                        "UNIQUE(ID,Patent,LastName,FirstName))";
                stmt.executeUpdate(sql);
            }

            var2=var1.readLine();
            int var5=10000;
            int var=9795;
            while(var>0) {
                var2=var1.readLine();
                var--;
            }
            while (var2!=null) { //Control the dataset Size
                if (var5>=0) {
                    if (var5<1) {
                        break;
                    } else {
                        var5--;
                    }
                }
                String[] var6=var2.split(",");;
                try {
                    String var7 = "insert into TrainingData (ID,Patent,LastName,FirstName)" + "Values ('"+var6[0]+"','"+
                            var6[1]+"','"+var6[2]+"','"+var6[3]+"');";
                    stmt.executeUpdate(var7);

                } catch(SQLException e) {

                }
                patents.add(var6[1]);
                var2=var1.readLine();
            }

            stmt.close();
            connection.commit();
            connection.close();

            System.out.println("Patent information dataset is finished building");


        } catch (FileNotFoundException e) {
            System.out.println("Benchmark file not found!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patents;
    }

    /**
     *
     * @param path store path
     * @param str the test to store
     */
    public void storeText(String path,String str){
        try {
            FileWriter f=new FileWriter(path);
            if(str!=null) {
                f.write(str);
            }
            else {
                f.write("");
            }

            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        TrainingDataGenerator t=new TrainingDataGenerator();
        //t.buildPatentInfDataSet();
        t.buildData();
    }
}
