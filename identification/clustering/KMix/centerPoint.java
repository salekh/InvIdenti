package clustering.KMix;

import base.patent;
import org.apache.mahout.math.matrix.DoubleMatrix2D;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/25.
 */
public class centerPoint {

    private DoubleMatrix2D td;
    private DoubleMatrix2D td_abs;
    private DoubleMatrix2D td_claims;
    private DoubleMatrix2D td_des;
    private ArrayList<String> assignee;
    private ArrayList<String> category;
    private ArrayList<String> names;

    public void setTd(DoubleMatrix2D M) {
        this.td=M.copy();
    }

    public void setTd_abs(DoubleMatrix2D M){
        this.td_abs=M.copy();
    }

    public void setTd_claims(DoubleMatrix2D M) {
        this.td_claims=M.copy();
    }

    public void setTd_des(DoubleMatrix2D M) {
        this.td_des=M.copy();
    }

    public void distanceToPatent(patent p) {

    }

}
