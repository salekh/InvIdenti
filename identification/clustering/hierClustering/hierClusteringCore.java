package clustering.hierClustering;

import base.pair;
import clustering.patentDistance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sunlei on 15/9/21.
 */
public class hierClusteringCore
{
    private int m_NumClusters=2;
    private ArrayList<hierCluster> m_Clusters=new ArrayList<>();
    protected patentDistance m_Distance;
    protected int current_NumClusters;

    pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo;

    public void setAttriInfor(pair<HashMap<String,Integer>,HashMap<String,Integer>> info)
    {
        this.attriInfo=info;
    }


    public void set_NumClusters(int number)
    {
        this.m_NumClusters=number;
    }


    public int numberOfClusters()
    {
        return current_NumClusters;
    }

    public ArrayList<hierCluster> get_Clusters()
    {
        return this.m_Clusters;
    }

    public void buildCluster(Instances data)
    {

        for(int i=0;i<data.numInstances()-1;i++) {

            for (int j = i + 1; j < data.numInstances(); j++)
                 if (i==6||j==6)   System.out.print(new patentDistance(this.attriInfo).distance(data.instance(i),data.instance(j))+" ");

            System.out.println();
        }





        m_Distance=new patentDistance(this.attriInfo);
        initializeCluster(data);
        current_NumClusters=m_Clusters.size();

        while(current_NumClusters>m_NumClusters)
        {
            mergeCluster(data);
            current_NumClusters=m_Clusters.size();
        }

    }

    public void initializeCluster(Instances data)
    {
        for(int i=0;i<data.numInstances();i++)
        {
            hierCluster current=new hierCluster();
            current.addInstance(i);
            m_Clusters.add(current);
        }
    }

    public void mergeCluster(Instances data)
    {
        double mostSim=hierCluster.maxDitanceBetweenClusters(data,m_Clusters.get(0), m_Clusters.get(1),this.attriInfo);
        int most_i=0;
        int most_j=1;
        //System.out.print("{");
        for(int i=0;i<this.m_Clusters.size()-1;i++)
            for(int j=i+1;j<this.m_Clusters.size();j++)
            {
                double temp=hierCluster.maxDitanceBetweenClusters(data,m_Clusters.get(i), m_Clusters.get(j),this.attriInfo);

          //      System.out.print(temp+" ");

                if (temp>mostSim)
                {

                    mostSim=temp;
                    most_i=i;
                    most_j=j;

                }
            }
        //System.out.print("}");
        m_Clusters.get(most_i).getInstancesIndex().addAll(m_Clusters.get(most_j).getInstancesIndex());
        System.out.print("{");
        for(Integer t:m_Clusters.get(most_i).getInstancesIndex())
        {
            System.out.print(t+" ");
        }
        System.out.println("}");
        m_Clusters.remove(most_j);
    }

}
