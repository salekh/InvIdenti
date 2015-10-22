package clustering.distancefunction;

import base.patent;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;

/**
 *
 * Created by leisun on 15/9/29.
 */
public class CosDistance extends AbstractDistance {

    /**
     * calculate the distance between two patents as the similarity
     * @param first first patent
     * @param second second patent
     * @return return the similarity between the two patents
     */

    public CosDistance()
    {
        super();
        this.distanceType="Cosine Distance";
    }

    public double distance(patent first,patent second) {
        double result=0.0D;

        if (this.fulltextCompare==true) {
            result+=(this.cosDistance(first.getTd(),second.getTd())*this.weightFullText);
        }

        if (this.abstractCompare==true) {
            result+=(this.cosDistance(first.getTd_abs(),second.getTd_abs())*this.weightAbstract);
        }

        if (this.desComapre==true) {
            result+=(this.cosDistance(first.getTd_des(),second.getTd_des())*this.weightDes);
        }

        if (this.claimsCompare==true) {
            result+=(this.cosDistance(first.getTd_claims(),second.getTd_claims())*this.weightClaims);
        }

        if (this.assigneeCompare==true) {
            result+=(this.compareAssignee(first.getAssignee(),second.getAssignee())*this.weightAssignee);
        }

        if (this.categoryCompare==true) {
            result+=(this.comapreCategories(first.getCategory(),second.getCategory())*this.weightCategory);
        }
        if (this.nameCompare==true) {
            result+=(this.compareName(first.getCategory(),second.getCategory())*this.weightName);
        }

        return result;
    }


    /**
     * calculate the cosine vector between two vectors;
     * @param first first vector
     * @param second second vector
     * @return return the cosine value of the two vectors.
     */
    public double cosDistance(DoubleMatrix2D first,DoubleMatrix2D second) {
        double result=0.0D;

        int dim=first.rows();
        double[][] dd=new double[dim][2];
        for(int i=0;i<dim;i++) {
            dd[i][0]=first.get(i,0);
        }

        for(int i=0;i<dim;i++) {
            dd[i][1]=second.get(i,0);
        }

        DoubleMatrix2D d = new DenseDoubleMatrix2D(dd);
        DoubleMatrix2D result_m = new DenseDoubleMatrix2D(2, 2);
        d.zMult(d, result_m, 1.0D, 0.0, true, false);

        if (result_m.get(0, 0) != 0 && result_m.get(1, 1) != 0)
            result = (result_m.get(0, 1) / (Math.sqrt(result_m.get(0, 0)) * Math.sqrt(result_m.get(1, 1))));
        else result =0 ;

        if (this.pCorrelation)
             return result;
        else return (1-result);
    }

}
