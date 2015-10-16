package clustering.hierarchy;


import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;


import java.util.ArrayList;


/**
 * Created by leisun on 15/9/29.
 */
public class HierCluster {
    private ArrayList<Integer> patentsIndex=new ArrayList<>();
    private int centroidIndex=-1;


    /**
     * add a new patents index into the cluster
     * @param num the new patent index
     */
    public void addInstance(int num) { this.patentsIndex.add(num);}

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
    public static double maxDitanceBetweenClusters(ArrayList<patent> patents,HierCluster c1,HierCluster c2,AbstractDistance distance)
    {

        //c1.computeCentroid(patents,distance);
        //c2.computeCentroid(patents,distance);
       // double maxdistance=distance.distance(patents.get(c1.getPatentsIndex().get(c1.getCentroidIndex())),patents.get(c2.getPatentsIndex().get(c2.getCentroidIndex())));
        double maxdistance=distance.distance(patents.get(c1.getPatentsIndex().get(0)),patents.get(c2.getPatentsIndex().get(0)));

        double sum=0;
        for(Integer i:c1.getPatentsIndex()) {

            for(Integer j:c2.getPatentsIndex())
            {
               double temp=distance.distance(patents.get(i),patents.get(j));
                sum+=temp;
                if (temp<maxdistance) maxdistance=temp;
            }

        }
    return maxdistance;
       // return sum/(c1.getPatentsIndex().size()*c2.getPatentsIndex().size());
    }

    public void computeCentroid(ArrayList<patent> pts,AbstractDistance distance) {
        if (this.getPatentsIndex().size()==0) this.centroidIndex=-1;
        if (this.getPatentsIndex().size()==1||this.getPatentsIndex().size()==2) this.centroidIndex=this.getPatentsIndex().get(0);
        double max=-Double.MAX_VALUE;
        this.centroidIndex=0;
        for (int i=0;i<this.getPatentsIndex().size();i++) {
            double sum=0;
            for (int j=0;j<this.getPatentsIndex().size();j++) {
                if (i!=j) sum+=distance.distance(pts.get(i),pts.get(j));
            }
            if (sum>max)
            {
                max=sum;
                this.centroidIndex=i;
            }
        }
    }

    public int getCentroidIndex() {
        return this.centroidIndex;
    }
}
