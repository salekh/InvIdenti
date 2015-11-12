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

     * @return return the similarity between the two patents
     */

    public boolean s=false;

    public CosDistance()
    {
        super();
        this.distanceType="Cosine Distance";
    }

    public double distance(patent first,patent second) {
        double result=0.0D;

        if (this.fulltextCompare==true) {
            result+=(this.cosDistance(first.getTd(),second.getTd())*this.weightFullText);

            if (Double.isNaN(result)) System.out.println(1);
        }

        if (this.abstractCompare==true) {
            result+=(this.cosDistance(first.getTd_abs(),second.getTd_abs())*this.weightAbstract);
            if (show) {
                System.out.println("abs");
                System.out.println(this.cosDistance(first.getTd_abs(),second.getTd_abs()));
            }
            if (Double.isNaN(result)) System.out.println(2);
        }

        if (this.desComapre==true) {
            result+=(this.cosDistance(first.getTd_des(),second.getTd_des())*this.weightDes);
            if (show) {
                System.out.println("des");
                System.out.println(this.cosDistance(first.getTd_des(),second.getTd_des()));
            }
            if (Double.isNaN(result)) System.out.println(3);
        }

        if (this.claimsCompare==true) {
            result+=(this.cosDistance(first.getTd_claims(),second.getTd_claims())*this.weightClaims);
            if (show) {
                System.out.println("claims");
                System.out.println(this.cosDistance(first.getTd_claims(),second.getTd_claims()));
            }
            if (Double.isNaN(result)) System.out.println(4);
        }

        if (this.assigneeCompare==true) {
            result+=(this.compareAssignee(first.getAssignee(),second.getAssignee(),first.getAsgNum(),second.getAsgNum())*this.weightAssignee);
            if (show) {
                System.out.println("assignee");
                System.out.println(first.getAssignee() + " " + second.getAssignee());
                System.out.println(this.compareAssignee(first.getAssignee(), second.getAssignee(), first.getAsgNum(), second.getAsgNum()));
            }
                if (Double.isNaN(result)) System.out.println(5);
        }

        if (this.categoryCompare==true) {
            if (show) {
                System.out.println("category");
                System.out.println(first.getCategory()+" "+second.getCategory());
                System.out.println(this.comapreCategories(first.getCategory(),second.getCategory())*this.weightCategory);
            }

            result+=(this.comapreCategories(first.getCategory(),second.getCategory())*this.weightCategory);
            if (s) System.out.println(result);
            if (Double.isNaN(result)) System.out.println(6);
        }
        if (this.lastNameCompare==true) {

            if (show) System.out.println("Lastname");
            result+=(this.compareName(first.getLastName(),second.getLastName())*this.weightLastName);
            if (s) System.out.println(result);
            if (Double.isNaN(result)) System.out.println(7);
        }

        if (this.firstNameCompare==true) {

            if (show) System.out.println("Firstname");
            result+=(this.compareName(first.getFirstName(),second.getFirstName())*this.weightFirstName);
            if (s) System.out.println(result);
            if (Double.isNaN(result)) System.out.println(7);
        }

        if (this.coAuthorCompare==true) {
            result+=(this.compareCoAuthor(first.getCoAuthor(),second.getCoAuthor())*this.weightCoAuthor);
            if (show) {
                System.out.println("coAuthor");
                System.out.println(this.compareCoAuthor(first.getCoAuthor(),second.getCoAuthor()));
            }
            if (Double.isNaN(result)) System.out.println(8);
        }

        if (this.locationCompare==true) {
            if (show) {
                System.out.println("location");
                System.out.println(this.compareLocation(first.getCountry(),first.getLat(),first.getLng(),second.getCountry(),second.getLat(),second.getLng()));
            }
            result+=(this.compareLocation(first.getCountry(),first.getLat(),first.getLng(),second.getCountry(),second.getLat(),second.getLng())*this.weightLocation);

            if (Double.isNaN(result)) System.out.println(9);
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

        if (this.pCorrelation) {
            if (result<0) {
                result=0;
            } else if (result>1) {
                result=1;
            }
            return result;
        }
        else
        {
            if ((1-result)<0) {
                result=1;
            } else if ((1-result)>1) {
                result=0;
            }
            return (1-result);
        }
    }

}
