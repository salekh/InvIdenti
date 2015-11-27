package clustering.Dbscan;

import base.patentCluster;
import clustering.distancefunction.AbstractDistance;
import clustering.hierarchy.HierCluster;
import clustering.hierarchy.HierCore;
import clustering.patentClustering;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/25.
 */
public class DBScanClusteringPatents extends patentClustering {

    ArrayList<DBCluster> DBclusters;

    public DBScanClusteringPatents() {
        super();
        this.clusteringType="DBSCAN Clustering";
    }

    /**
     * Clustering based on a specific function
     * @param distance distance function
     */
    public void Cluster(AbstractDistance distance) {
        if (!initilization) {
            logger.error("Clustering method: no patents are initialized");
            return;
        }



       DBScanCore var0=new DBScanCore();

        var0.setRadius(this.threshold);



        try {





            var0.buildCluster(patents,distance);

            clusters.clear();

            DBclusters=var0.get_Clusters();

            clustersIndex.clear();

            for(DBCluster cluster:this.DBclusters) {
                this.clustersIndex.add(cluster);
            }

            // System.out.println(hier_clusters.size());


            for(int i=0;i<var0.get_Clusters().size();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:DBclusters.get(i).getPatentsIndex())
                {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<DBCluster> getHier_clusters() {
        return this.DBclusters;
    }
}
