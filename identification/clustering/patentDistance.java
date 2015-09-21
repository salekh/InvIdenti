package clustering;

import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.ini4j.Wini;
import weka.core.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by leisun on 15/9/7.
 */
public class patentDistance implements Cloneable, TechnicalInformationHandler {

    private Instances m_Data;
    private boolean fulltextCompare;
    private boolean abstractCompare;
    private boolean claimsCompare;
    private boolean desComapre;
    private boolean assigneeCompare;
    private boolean categoryCompare;

    public patentDistance()
    {
        initialOption();
    }

    public void setInstances(Instances data)
    {
        this.m_Data=data;
    }

    public void clean() {
        this.m_Data = new Instances(this.m_Data, 0);
    }

    public patentDistance(Instances data) {
        this.m_Data = new Instances(data);
    }


    public String globalInfo() {
        return "Implement the patent distance function. More information:" + this.getTechnicalInformation().toString();
    }

    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
        result.setValue(TechnicalInformation.Field.AUTHOR, "Lei Sun");
        result.setValue(TechnicalInformation.Field.TITLE, "Patent Distance");
        result.setValue(TechnicalInformation.Field.URL, "");
        return result;
    }


    public double distance(HashMap<String,Integer> attrinum,Instance first,Instance second)
    {
        double result=0.0D;

        //Text Compare
        if(this.fulltextCompare==true) {
            double[][] dd = new double[dimension][2];
            for (int i = 0; i < dimension; i++) {
                dd[i][0] = first.value(i);
            }
            for (int i = 0; i < dimension; i++) {
                dd[i][1] = second.value(i);
            }
            DoubleMatrix2D d = new DenseDoubleMatrix2D(dd);
            DoubleMatrix2D result_m = new DenseDoubleMatrix2D(2, 2);
            d.zMult(d, result_m, 1.0D, 0.0, true, false);
            if (result_m.get(0, 0) != 0 && result_m.get(1, 0) != 0)
                result += 1-result_m.get(0, 1) / (Math.sqrt(result_m.get(0, 0)) * Math.sqrt(result_m.get(1, 1)));
            else result += 1;
        }
        System.out.println("before"+result);
        //Class Compare
        if(this.categoryCompare==true)
        {
            if(!first.stringValue(first.attribute(attrinum.get("Category"))).equalsIgnoreCase(second.stringValue(second.attribute(attrinum.get("Category")))))
                result+=1;
        }

        //Assign Compare
        if(this.assigneeCompare==true)
        {
            if(!first.stringValue(first.attribute(attrinum.get("Assignee"))).equalsIgnoreCase(second.stringValue(second.attribute(attrinum.get("Assignee")))))
                result+=1;
        }
    }


    public double distance(Instance first,Instance second,int dimension)
    {
        double result=0.0D;

        //Text Compare
        if(this.fulltextCompare==true) {
            double[][] dd = new double[dimension][2];
            for (int i = 0; i < dimension; i++) {
                dd[i][0] = first.value(i);
            }
            for (int i = 0; i < dimension; i++) {
                dd[i][1] = second.value(i);
            }
            DoubleMatrix2D d = new DenseDoubleMatrix2D(dd);
            DoubleMatrix2D result_m = new DenseDoubleMatrix2D(2, 2);
            d.zMult(d, result_m, 1.0D, 0.0, true, false);
            if (result_m.get(0, 0) != 0 && result_m.get(1, 0) != 0)
                result += 1-result_m.get(0, 1) / (Math.sqrt(result_m.get(0, 0)) * Math.sqrt(result_m.get(1, 1)));
            else result += 1;
        }
        System.out.println("before"+result);
        //Class Compare
        if(this.categoryCompare==true)
        {
            if(!first.stringValue(first.attribute(dimension)).equalsIgnoreCase(second.stringValue(second.attribute(dimension))))
                result+=1;
        }

        //Assign Compare

        if(this.assigneeCompare==true)
        {
            if(!first.stringValue(first.attribute(dimension+1)).equalsIgnoreCase(second.stringValue(second.attribute(dimension+1))))
                result+=1;
        }
        System.out.println("after"+result);
        return result;
    }

    //Initialize the options
    public void initialOption()
    {
        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));
            this.fulltextCompare=initalFile.get("DistanceOption","FullTextCompare").equalsIgnoreCase("true");
            this.categoryCompare=initalFile.get("DistanceOption","CategoryCompare").equalsIgnoreCase("true");
            this.assigneeCompare=initalFile.get("DistanceOption","AssigneeCompare").equalsIgnoreCase("true");
            this.abstractCompare=initalFile.get("DistanceOption","AbstractCompare").equalsIgnoreCase("true");;
            this.claimsCompare=initalFile.get("DistanceOption","ClaimsCompare").equalsIgnoreCase("true");
            this.desComapre=initalFile.get("DistanceOption","DescriptionCompare").equalsIgnoreCase("true");
        } catch (IOException e)
        {
            System.out.println("Initial File 'invidenti.ini not found',distance function will use default options");
            this.categoryCompare=true;
            this.assigneeCompare=true;
            this.fulltextCompare=true;
            this.abstractCompare=false;
            this.claimsCompare=false;
            this.desComapre=false;
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.13 $");
    }
}