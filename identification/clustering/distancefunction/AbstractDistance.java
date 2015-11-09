package clustering.distancefunction;

import base.patent;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



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
    protected boolean locationCompare=true;
    protected boolean coAuthorCompare=true;
    protected boolean pCorrelation=true;

    protected double weightFullText=1.0;
    protected double weightAbstract=1.0;
    protected double weightClaims=1.0;
    protected double weightDes=1.0;
    protected double weightAssignee=1.0;
    protected double weightCategory=1.0;
    protected double weightName=1.0;
    protected double weightLocation=1.0;
    protected double weightCoAuthor=1.0;


    protected int numofOptions=9;

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
            this.abstractCompare=initalFile.get("DistanceOption","AbstractCompare").equalsIgnoreCase("true");
            this.claimsCompare=initalFile.get("DistanceOption","ClaimsCompare").equalsIgnoreCase("true");
            this.desComapre=initalFile.get("DistanceOption","DescriptionCompare").equalsIgnoreCase("true");
            this.nameCompare=initalFile.get("DistanceOption","NameCompare").equalsIgnoreCase("true");
            this.coAuthorCompare=initalFile.get("DistanceOption","CoAuthorCompare").equalsIgnoreCase("true");
            this.locationCompare=initalFile.get("DistanceOption","LocationCompare").equalsIgnoreCase("true");


            this.weightFullText=Double.parseDouble(initalFile.get("Weights", "FullText"));
            this.weightAbstract=Double.parseDouble(initalFile.get("Weights","Abstract"));
            this.weightClaims=Double.parseDouble(initalFile.get("Weights","Claims"));
            this.weightDes=Double.parseDouble(initalFile.get("Weights","Description"));
            this.weightAssignee=Double.parseDouble(initalFile.get("Weights","Assignee"));
            this.weightCategory=Double.parseDouble(initalFile.get("Weights","Category"));
            this.weightName=Double.parseDouble(initalFile.get("Weights","Name"));
            this.weightCoAuthor= Double.parseDouble(initalFile.get("Weights","CoAuthor"));
            this.weightLocation = Double.parseDouble(initalFile.get("Weights","Location"));


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
        if (str1==null || str2==null||str1.length()==0||str2.length()==0) {
          if (this.pCorrelation)  {
              return 0.2;
          } else {
              return 0.8;
          }
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        double var1=var0.distance(str1,str2);



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
        if (str1==null || str2==null||str1.length()==0||str2.length()==0) {
           if(pCorrelation) {
               return 0.2;
           } else {
               return 0.8;
           }
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
        if (name1==null || name2==null||name1.length()==0||name2.length()==0) {
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
     * Calculate the similarity by using the coAuthors
     * @param coAuthor1 first patent coAuthor1
     * @param coAuthor2 second patent coAuthor2
     * @return the similarity between coAuthor1 and coAuthor2
     */
    public double compareCoAuthor(String coAuthor1,String coAuthor2) {
        double result=0.0;

        if (coAuthor1==null||coAuthor2==null||coAuthor1.length()==0||coAuthor2.length()==0) {
            if (this.pCorrelation) {
                return 0.2;
            } else {
                return 0.8;
            }

        }



        String[] coAuthorNames1=coAuthor1.split(";");
        String[] coAuthorNames2=coAuthor2.split(";");
        String[] lessNames;
        String[] moreNames;
        if (coAuthorNames1.length>coAuthorNames2.length) {
            lessNames=coAuthorNames2;
            moreNames=coAuthorNames1;
        } else {
            moreNames=coAuthorNames2;
            lessNames=coAuthorNames1;
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        for(String var1:lessNames) {
            double max=-1;
            for (String var2: moreNames) {
                double temp=(1-var0.distance(var1,var2));
                if (temp>max) max=temp;
            }
            result+=max;
        }
        if (result>6) {
            result=6;
        }
        if (this.pCorrelation) {
            return result/6;
        } else {
        return (1-result/6);
        }
    }


    public  double compareLocation(String country1,String lat1,String lng1,String country2,String lat2,String lng2) {
        double result=0;
        if (country1.equalsIgnoreCase(country2)) {
            if (lat1==null||lat2==null||lng1==null||lng2==null) {
                result=1;
            } else {
                double lat1_d = Double.parseDouble(lat1)/180;
                double lng1_d = Double.parseDouble(lng1)/180;
                double lat2_d = Double.parseDouble(lat2)/180;
                double lng2_d = Double.parseDouble(lng2)/180;
                double distance = getDistance(lat1_d,lng1_d,lat2_d,lng2_d);
                if ( distance < 1.0 )
                    result = 5;
                else if ( distance < 10 )
                    result = 4;
                else if ( distance < 25)
                    result = 3;
                else if ( distance < 50 )
                    result = 2;
                else
                    result = 1;

            }

        }

        if (this.pCorrelation) {
            return result/5;
        } else {
            return (1-result/5);
        }
    }


    public  double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double a = lat1 - lat2;
        double b = lng1 - lng2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6371;
        s = Math.round(s * 10000) / 10000;
        return s;
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
        this.coAuthorCompare=options[7];
        this.locationCompare=options[8];

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
        this.weightCoAuthor=options[7];
        this.weightLocation=options[8];

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
        var0+="\t"+"CoAuthor    |"+coAuthorCompare+"\n";
        var0+="\t"+"Location    |"+locationCompare+"\n";

        var0+="Weights:"+this.weightFullText+","+this.weightAbstract+","+weightClaims+","+weightDes+","+weightAssignee+","+weightCategory+","+weightName+" "+weightCoAuthor+" "+weightLocation;

        return var0;
    }
}
