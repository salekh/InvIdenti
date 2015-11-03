package clustering.hierarchy;

import base.patent;
import base.patentCluster;

import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.patentClustering;

import java.util.ArrayList;

/**
 * Created by leisun on 15/9/29.
 */
public class HierClusteringPatents extends patentClustering
{

    private double eps=Double.MAX_VALUE;
    ArrayList<HierCluster> hier_clusters;

    public HierClusteringPatents(ArrayList<patent> patents) {
        super(patents);
    }


    public void setEps(double eps) {
        this.eps=eps;
    }

    public void getEps() {
        System.out.println(eps);
    }

    /**
     * run clustering on the patents
     */
    public void Cluster()
    {

        HierCore hc=new HierCore();
        hc.setEps(this.eps);

        try {

            hc.set_NumClusters(this.number_Cluster);

            hc.buildCluster(patents,new CosDistance());
            clusters.clear();

          hier_clusters=hc.get_Clusters();

            for(int i=0;i<hc.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:hier_clusters.get(i).getPatentsIndex())
                {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clustering based on a specific function
     * @param distance distance function
     */
    public void Cluster(AbstractDistance distance) {
        HierCore hc=new HierCore();
        hc.setEps(this.eps);
        hc.setpCorrelation(this.pCorrelation);

        try {



            hc.set_NumClusters(this.number_Cluster);

            hc.buildCluster(patents,distance);

            clusters.clear();

            hier_clusters=hc.get_Clusters();

           // System.out.println(hier_clusters.size());


            for(int i=0;i<hc.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:hier_clusters.get(i).getPatentsIndex())
                {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HierCluster> getHier_clusters() {
        return this.hier_clusters;
    }
}
