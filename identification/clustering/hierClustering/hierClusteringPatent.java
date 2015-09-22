package clustering.hierClustering;

import Base.patent;
import Base.patentCluster;
import clustering.patentClustering;
import clustering.patentDistance;
import clustering.simpleKmeans.simpleKMeansCore;
import preprocessing.patentPreprocessing;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/9/21.
 */
public class hierClusteringPatent extends patentClustering
{


    public hierClusteringPatent(ArrayList<patent> patents)
    {
        super(patents);
    }


    public void Cluster(ArrayList<patent> patents)
    {
        this.patents=patents;
        //HashMap<String,Instances> instances=new HashMap<>();

        patentPreprocessing preprocess=new patentPreprocessing(this.patents);
        preprocess.setClusterCount(this.number_Cluster);
        preprocess.setLanguage(this.language);
        preprocess.preprocess();
        this.patents=preprocess.getPatents();


        //run the simple k-means clustering
        hierClusteringCore hc=new hierClusteringCore();


        try {

            hc.set_NumClusters(this.number_Cluster);
            hc.setAttriInfor(this.attriInfo);
            hc.buildCluster(instances);
            ArrayList<hierCluster> hier_clusters=hc.get_Clusters();

            for(int i=0;i<hc.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:hier_clusters.get(i).getInstancesIndex())
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
