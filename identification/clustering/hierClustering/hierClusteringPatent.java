package clustering.hierClustering;

import base.patent;
import base.patentCluster;
import clustering.patentClustering;
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

        System.out.println("+++++++++++++");
        for(int i=0;i<this.patents.get(0).getTd_abs().rows();i++)
            System.out.print(this.patents.get(0).getTd_abs().get(i,0));
        System.out.println();

        for(int i=0;i<this.patents.get(0).getTd_claims().rows();i++)
            System.out.print(this.patents.get(0).getTd_claims().get(i,0));
        System.out.println();

        for(int i=0;i<this.patents.get(0).getTd_des().rows();i++)
            System.out.print(this.patents.get(0).getTd_des().get(i,0));
        System.out.println();





        System.out.println("+++++++++++++");
        //run the simple k-means clustering
        hierClusteringCore hc=new hierClusteringCore();


        try {

            hc.set_NumClusters(this.number_Cluster);
            hc.setAttriInfor(this.attriInfo);
            hc.buildCluster(instances);
            ArrayList<hierCluster> hier_clusters=hc.get_Clusters();

            clusters.clear();
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
