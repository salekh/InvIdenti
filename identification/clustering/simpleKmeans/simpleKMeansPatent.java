package clustering.simpleKmeans;

import Base.patent;
import Base.patentCluster;
import clustering.patentClustering;
import clustering.patentDistance;

import org.carrot2.core.LanguageCode;
import preprocessing.patentPreprocessing;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sunlei on 15/9/7.
 */
public class simpleKMeansPatent extends patentClustering
{

    private int maxIteration=15;
    private int clusterCount=25;


    private double oversim;

    public void setClusterCount(int number)
    {
        this.clusterCount=number;
    }

    public void setMaxIteration(int number)
    {
        this.maxIteration=number;
    }

    public simpleKMeansPatent(ArrayList<patent> patents)
    {
        super(patents);
    }


    public void Cluster(ArrayList<patent> patents)
    {
        this.patents=patents;
        //HashMap<String,Instances> instances=new HashMap<>();

        patentPreprocessing preprocess=new patentPreprocessing(this.patents);
        preprocess.setClusterCount(this.clusterCount);
        preprocess.setLanguage(this.language);
        preprocess.preprocess();
        this.patents=preprocess.getPatents();


        //run the simple k-means clustering
        simpleKMeansCore km=new simpleKMeansCore();


        try {
            km.setMaxIterations(this.maxIteration);
            km.setNumClusters(this.clusterCount);
            km.setAttriInfor(this.attriInfo);
            km.buildClusterer(instances);

            for(int i=0;i<km.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                clusters.add(temp);
            }

            for(int i=0;i<patents.size();i++)
            {
                clusters.get(km.clusterInstance(instances.instance(i))).addPatent(patents.get(i));
            }

            //Calculating the overall similarity
            oversim=0.0;
            for(patentCluster cluster:clusters)
            {
                double sim=0.0;
                for(int i=0;i<cluster.getPatents().size()-1;i++)
                {
                    for(int j=i+1;j<cluster.getPatents().size();j++)
                    {
                        int temp1=this.getIndex(cluster.getPatents().get(i));
                        int temp2=this.getIndex(cluster.getPatents().get(j));
                        sim+=new patentDistance(this.attriInfo).distance(instances.instance(i),instances.instance(j));
                    }

                }
                if(cluster.getPatents().size()>1) sim=2*sim/(cluster.getPatents().size()*cluster.getPatents().size()-1);
                oversim+=sim;
            }
            oversim=oversim/clusters.size();
            System.out.println(oversim);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
