package clustering.distancefunction;

import base.patent;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;


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

    protected double weightFullText=1.0;
    protected double weightAbstract=1.0;
    protected double weightClaims=1.0;
    protected double weightDes=1.0;
    protected double weightAssignee=1.0;
    protected double weightCategory=1.0;


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
            this.weightFullText=Double.parseDouble(initalFile.get("Weights", "FullText"));
            this.weightAbstract=Double.parseDouble(initalFile.get("Weights","Abstract"));
            this.weightClaims=Double.parseDouble(initalFile.get("Weights","Claims"));
            this.weightDes=Double.parseDouble(initalFile.get("Weights","Description"));
            this.weightAssignee=Double.parseDouble(initalFile.get("Weights","Assignee"));
            this.weightCategory=Double.parseDouble(initalFile.get("Weights","Category"));

        } catch (IOException e)
        {
            System.out.println("Initial File 'invidenti.ini not found',distance function will use default options");
        }
    }

    /**
     * compare two patents assignees
     * @param str1 first assignee name
     * @param str2 second assignee name
     * @return the comparison result of two assignees;
     */
    protected double compareAssignee (String str1,String str2) {
        if (str1==null || str2==null) {
            return 0;
        }
        if (str1.equalsIgnoreCase(str2)) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    /**
     * compare two patent categories
     * @param str1 first category
     * @param str2 second category
     * @return the comparison result of two patent categories
     */
    protected double comapreCategories (String str1,String str2) {
        double result=0.0;
        if (str1==null || str2==null) {
            return 0;
        }
        String[] strs1=str1.split("/");
        String[] strs2=str2.split("/");


        for (String var1:strs1) {
            for (String var2:strs2) {
                if (var1.equalsIgnoreCase(var2)) {
                    result+=1;
                    break;
                }
            }
        }
        result=result/4;
        return result;
    }

}
