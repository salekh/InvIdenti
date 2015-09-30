package clustering.hierarchy;

import base.patent;
import base.patentCluster;

import clustering.distancefunction.CosDistance;
import clustering.patentClustering;
import preprocessing.patentPreprocessing;

import java.util.ArrayList;

/**
 * Created by leisun on 15/9/29.
 */
public class HierClusteringPatents extends patentClustering
{

    private double eps=0;

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
            System.out.println(this.number_Cluster);
            hc.set_NumClusters(this.number_Cluster);

            hc.buildCluster(patents,new CosDistance());
            ArrayList<HierCluster> hier_clusters=hc.get_Clusters();

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
}
