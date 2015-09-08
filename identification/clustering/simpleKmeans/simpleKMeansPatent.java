package clustering.simpleKmeans;

import Base.patent;
import Base.patentCluster;
import clustering.patentDistance;
import org.carrot2.core.LanguageCode;
import preprocessing.patentPreprocessing;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/9/7.
 */
public class simpleKMeansPatent
{
    ArrayList<patent> patents=new ArrayList<>();
    private int maxIteration=15;
    private int clusterCount=25;
    private LanguageCode language=LanguageCode.ENGLISH;
    ArrayList<patentCluster> clusters=new ArrayList<>();

    public void setClusterCount(int number)
    {
        this.clusterCount=number;
    }

    public void setMaxIteration(int number)
    {
        this.maxIteration=number;
    }

    public void setLanguage(LanguageCode code)
    {
        this.language=code;
    }

    public void Cluster(ArrayList<patent> patents)
    {
        this.patents=patents;
        patentPreprocessing preprocess=new patentPreprocessing(this.patents);
        preprocess.setClusterCount(this.clusterCount);
        preprocess.setLanguage(this.language);
        preprocess.preprocess();
        this.patents=preprocess.getPatents();



        //prepare for the clustering algorithm
        int tfDimension=patents.get(0).getTd().rows();
        FastVector f=new FastVector(tfDimension);

        for(int i=0;i<tfDimension;i++)
            f.addElement(new Attribute(Integer.toString(i)));
        Instances instances=new Instances("patent",f,patents.size());

        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(tfDimension);
            temp.setDataset(instances);
            for(int j=0;j<tfDimension;j++)
            {
                temp.setValue(j, patents.get(i).getTd().get(j, 0));
            }

            instances.add(temp);
        }


        //run the simple k-means clustering
        simpleKMeansCore km=new simpleKMeansCore(tfDimension);


        try {
            km.setMaxIterations(this.maxIteration);
            km.setNumClusters(this.clusterCount);
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



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<patentCluster> getClusters()
    {
        return this.clusters;
    }

    public String toString()
    {
        String str="";

        for(int i=0;i<this.clusters.size();i++)
        {
            str+=("Cluster "+i);
            str+=("\n==========\n");
            for(patent p:this.clusters.get(i).getPatents())
            {
                str+=p.getPatent_number()+"\n";
            }
        }

        return str;
    }


}
