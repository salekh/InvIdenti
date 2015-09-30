package clustering.hierarchy;


import base.patent;
import clustering.distancefunction.CosDistance;


import java.util.ArrayList;


/**
 * Created by leisun on 15/9/29.
 */
public class HierCluster {
    private ArrayList<Integer> patentsIndex=new ArrayList<>();

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

    /**
     * Calculate the max distance between two clusters
     * @param patents patents list
     * @param c1 first patent cluster
     * @param c2 second patent cluster
     * @return the maxDistance between two patent clusters
     */
    public static double maxDitanceBetweenClusters(ArrayList<patent> patents,HierCluster c1,HierCluster c2)
    {
        CosDistance distance=new CosDistance();

        double maxdistance=distance.distance(patents.get(c1.getPatentsIndex().get(0)),patents.get(c2.getPatentsIndex().get(0)));

        for(Integer i:c1.getPatentsIndex()) {
            for(Integer j:c2.getPatentsIndex())
            {
                double temp=distance.distance(patents.get(i),patents.get(j));
                if (temp>maxdistance) maxdistance=temp;
            }

        }

        return maxdistance;
    }

}
