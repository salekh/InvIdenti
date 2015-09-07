package clustering;

import org.apache.mahout.math.function.Functions;
import org.apache.mahout.math.matrix.DoubleMatrix1D;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix1D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import weka.core.*;


/**
 * Created by leisun on 15/9/7.
 */
public class patentDistance implements Cloneable, TechnicalInformationHandler {

    private Instances m_Data;

    public patentDistance() {
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

    public double distance(Instance first,Instance second,int dimension)
    {
        double result=0.0D;
        double[][] dd=new double[dimension][2];

        for(int i=0;i<dimension;i++)
        {
            dd[i][0]=first.value(i);
        }

        for(int i=0;i<dimension;i++)
        {
            dd[i][1]=second.value(i);
        }

        DoubleMatrix2D d=new DenseDoubleMatrix2D(dd);

        DoubleMatrix2D result_m=new DenseDoubleMatrix2D(2,2);

        d.zMult(d,result_m,1.0D,0.0,true,false);


        if (result_m.get(0,0)!=0&&result_m.get(1,0)!=0)
            result=result_m.get(0,1)/(Math.sqrt(result_m.get(0,0))*Math.sqrt(result_m.get(1,1)));
        else result=0;

        return result;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.13 $");
    }
}