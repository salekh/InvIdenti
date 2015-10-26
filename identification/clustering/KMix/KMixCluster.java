package clustering.KMix;

import base.patent;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/25.
 */
public class KMixCluster {

    ArrayList<Integer> patentsIndex=new ArrayList<>();

    /**
     * add a new patents index into the cluster
     * @param num the new patent index
     */
    public void addInstance(int num) {
        this.patentsIndex.add(num);
    }

    /**
     *
     * @return the patents index in the cluster
     */
    public ArrayList<Integer> getPatentsIndex() {
        return this.patentsIndex;
    }



}
