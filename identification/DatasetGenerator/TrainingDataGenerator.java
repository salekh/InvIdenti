package DatasetGenerator;

import base.patent;
import org.ini4j.Wini;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/4.
 */
public class TrainingDataGenerator {

    private int size=300;
    private String inputPath;
    private String outputPath;


    public TrainingDataGenerator() {
        try {
            Wini iniFile=new Wini(new File("invidenti.ini"));
            this.inputPath=iniFile.get("DataSet","TrainingDataInputPath");
            System.out.println(this.inputPath);
            this.outputPath=iniFile.get("DataSet","TrainingDataOutputPath");
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

        this.buildPatentInfDataSet();

        System.out.println("Finish building the training dataset");
    }

    /**
     *
     * @return the arraylist of the patent in the
     */
    public ArrayList<patent> buildPatentInfDataSet() {
        ArrayList<patent> patents=new ArrayList<>();


        System.out.println(this.inputPath);
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

                System.out.println("Benchmark file format is not right.");
                return patents;
            }

            var2=var1.readLine();
            int var4=size;

            while (var2!=null) {
                //Control the dataset Size
                if (var4>=0)
                {
                    if (var4<1) {
                        break;
                    }
                    else {
                        var4--;
                    }
                }
                var3=var2.split(",");
                System.out.println(var3[0]+" "+var3[1]+" "+var3[2]+" "+var3[3]);
                var2=var1.readLine();
            }

            System.out.println("Patent information dataset is finished building");


        } catch (FileNotFoundException e) {
            System.out.println("Benchmark file not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patents;
    }


}
