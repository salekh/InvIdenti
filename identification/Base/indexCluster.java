package base;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/4.
 */
public abstract class indexCluster  {
    protected ArrayList<Integer> patentsIndex=new ArrayList<>();
    /**
     *
     * @return the patents index in the cluster
     */
    public ArrayList<Integer> getPatentsIndex() {
        return this.patentsIndex;
    }
}
