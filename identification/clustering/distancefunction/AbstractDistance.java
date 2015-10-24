package clustering.distancefunction;

import base.patent;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogManager;


/**
 * Created by leisun on 15/9/28.
 *
 */
public abstract class AbstractDistance {

    protected boolean fulltextCompare=false;
    protected boolean abstractCompare=true;
    protected boolean claimsCompare=true;
    protected boolean desComapre=true;
    protected boolean assigneeCompare=true;
    protected boolean categoryCompare=true;
    protected boolean nameCompare=true;
    protected boolean pCorrelation=true;

    protected double weightFullText=1.0;
    protected double weightAbstract=1.0;
    protected double weightClaims=1.0;
    protected double weightDes=1.0;
    protected double weightAssignee=1.0;
    protected double weightCategory=1.0;
    protected double weightName=1.0;

    protected int numofOptions = 7;

    protected String distanceType="AbstractDistance";

    private static Logger logger= org.apache.logging.log4j.LogManager.getLogger(AbstractDistance.class.getName());

    public AbstractDistance(){
        this.initialOption();

    }

    /**
     * Calculate the distance as the similarity between two patents
     * @param fisrt first patent
     * @param second second patent
     * @return the similarity of the two patents
     */
    public abstract double distance(patent fisrt,patent second);

    /**
     * Initialize the options based on the initial file
     */
    public void initialOption() {
        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));

            this.fulltextCompare=initalFile.get("DistanceOption","FullTextCompare").equalsIgnoreCase("true");
            this.categoryCompare=initalFile.get("DistanceOption","CategoryCompare").equalsIgnoreCase("true");
            this.assigneeCompare=initalFile.get("DistanceOption","AssigneeCompare").equalsIgnoreCase("true");
            this.abstractCompare=initalFile.get("DistanceOption","AbstractCompare").equalsIgnoreCase("true");;
            this.claimsCompare=initalFile.get("DistanceOption","ClaimsCompare").equalsIgnoreCase("true");
            this.desComapre=initalFile.get("DistanceOption","DescriptionCompare").equalsIgnoreCase("true");
            this.nameCompare=initalFile.get("DistanceOption","NameCompare").equalsIgnoreCase("true");
            this.weightFullText=Double.parseDouble(initalFile.get("Weights", "FullText"));
            this.weightAbstract=Double.parseDouble(initalFile.get("Weights","Abstract"));
            this.weightClaims=Double.parseDouble(initalFile.get("Weights","Claims"));
            this.weightDes=Double.parseDouble(initalFile.get("Weights","Description"));
            this.weightAssignee=Double.parseDouble(initalFile.get("Weights","Assignee"));
            this.weightCategory=Double.parseDouble(initalFile.get("Weights","Category"));
            this.weightName=Double.parseDouble(initalFile.get("Weights","Name"));
            this.pCorrelation=initalFile.get("DistanceOption","PCorrelation").equalsIgnoreCase("true");

        } catch (IOException e)
        {
           logger.info("Initial File 'invidenti.ini not found',distance function will use default options");
        }
    }

    /**
     * compare two patents assignees by using the levenshtein distance between two string
     * @param str1 first assignee name
     * @param str2 second assignee name
     * @return the comparison result of two assignees;
     */
    protected double compareAssignee (String str1,String str2) {
        if (str1==null || str2==null) {
          if (this.pCorrelation)  {
              return 0.5;
          } else {
              return 0.5;
          }
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        if (this.pCorrelation)
        {
            return (1-var0.distance(str1,str2));
        } else {
            return var0.distance(str1,str2);
        }


    }

    /**
     * compare two patent categories
     * @param str1 first category
     * @param str2 second category
     * @return the comparison result of two patent categories
     */
    public double comapreCategories (String str1,String str2) {
        double result=0.0;
        if (str1==null || str2==null) {
            return 0;
        }
        String[] strs_1=str1.split("-");
        String[] strs_2=str2.split("-");

        ArrayList<String> strs1=new ArrayList<>();
        ArrayList<String> strs2=new ArrayList<>();

        for (String var3:strs_1) {
            String[] var4=var3.split("/");
            for (String var5:var4) {
                if (!strs1.contains(var5)) strs1.add(var5);
            }
        }

        for (String var3:strs_2) {
            String[] var4=var3.split("/");
            for (String var5:var4) {
                if (!strs2.contains(var5)) strs2.add(var5);
            }
        }

        for (String var1:strs1) {
            for (String var2:strs2) {
                if (var1.equalsIgnoreCase(var2)) {
                    result+=1;
                    break;
                }
            }
        }


        result=result/4;



        /**
         * Need Change here
         */

        if (result>1) result=1;

        if (this.pCorrelation) {return result;}
        else
        {
            return 1-result;
        }
    }

    /**
     * Compare the names
     * @param name1 first name
     * @param name2 second name
     * @return the levenshtein distance between two names according to the pCorrelation
     */
    public double compareName(String name1,String name2) {
        if (name1==null || name2==null) {
            if (this.pCorrelation)  {
                return 0;
            } else {
                return 1;
            }
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        if (this.pCorrelation)
        {
            return (1-var0.distance(name1,name2));
        } else {
            return var0.distance(name1,name2);
        }
    }



    /**
     * Set the options of the distance
     * @param options the boolean array for the options
     * @return true if the functions set the options correctly
     */
    public boolean setOptions(boolean[] options) {

        if (options.length<this.numofOptions) {
            logger.warn("Option setting fails!");
            return false;
        }

        this.fulltextCompare=options[0];
        this.abstractCompare=options[1];
        this.claimsCompare=options[2];
        this.desComapre=options[3];
        this.assigneeCompare=options[4];
        this.categoryCompare=options[5];
        this.nameCompare=options[6];

        return true;
    }

    /**
     * Set the weights of the scores
     * @param options a double array for the weights of the scores
     * @return true if the setting is successful
     */
    public boolean setWeights(double[] options) {
        if (options.length<this.numofOptions) {
            logger.warn("Weights Setting fails");
            return false;
        }
        this.weightFullText=options[0];
        this.weightAbstract=options[1];
        this.weightClaims=options[2];
        this.weightDes=options[3];
        this.weightAssignee=options[4];
        this.weightCategory=options[5];
        this.weightName=options[6];

        return true;
    }

    public void setpCorrelation(boolean pCorrelation){
        this.pCorrelation=pCorrelation;
    }

    public String toString() {
        String var0="Distance Type:"+this.distanceType+"\n";

        var0+="Options:\n";

        var0+="\t"+"FullText    |"+fulltextCompare+"\n";
        var0+="\t"+"Abstract    |"+abstractCompare+"\n";
        var0+="\t"+"Claims      |"+claimsCompare+"\n";
        var0+="\t"+"Description |"+desComapre+"\n";
        var0+="\t"+"Assignee    |"+assigneeCompare+"\n";
        var0+="\t"+"Categories  |"+categoryCompare+"\n";
        var0+="\t"+"Name        |"+nameCompare+"\n";

        var0+="Weights:"+this.weightFullText+","+this.weightAbstract+","+weightClaims+","+weightDes+","+weightAssignee+","+weightCategory+","+weightName+"\n";

        return var0;
    }
}
