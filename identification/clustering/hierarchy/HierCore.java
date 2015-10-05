package clustering.hierarchy;

import base.patent;
import clustering.distancefunction.AbstractDistance;

import java.util.ArrayList;

/**
 * Created by leisun on 15/9/29.
 */
public class HierCore {
    private int m_NumClusters=1;
    private ArrayList<HierCluster> m_Clusters=new ArrayList<>();
    protected AbstractDistance m_Distance;
    protected int current_NumClusters;

    protected double eps=0;
    /**
     * set the number of the clusters
     * @param number the number of the cluster
     */
    public void set_NumClusters(int number) {
        this.m_NumClusters=number;
    }

    /**
     *
     * @return the number of the clusters
     */
    public int numberOfClusters()
    {
        return current_NumClusters;
    }

    public void setEps(double eps) {
        this.eps=eps;
    }

    /**
     *
     * @return the patent clusters
     */
    public ArrayList<HierCluster> get_Clusters()
    {
        return this.m_Clusters;
    }

    /**
     * Run the patent clustering
     * @param patents the arraylist of the patents
     * @param distance the distance function
     */
    public void buildCluster(ArrayList<patent> patents,AbstractDistance distance)
    {

        System.out.println(this.eps);

        this.m_Distance=distance;
        initializeCluster(patents);
        current_NumClusters=m_Clusters.size();

        while(current_NumClusters>m_NumClusters)
        {


            if (!mergeCluster(patents)) {

                break;
            } else {
                current_NumClusters=m_Clusters.size();
            }

        }

    }

    /**
     * initilize the clusters
     * @param patents ArrayList of the patents
     */
    public void initializeCluster(ArrayList<patent> patents)
    {
        for(int i=0;i<patents.size();i++)
        {
            HierCluster current=new HierCluster();
            current.addInstance(i);
            m_Clusters.add(current);
        }
    }

    /**
     * Merge the cluster with the max similarity
     * @param patents the arraylist of the patents
     */
    public boolean mergeCluster(ArrayList<patent> patents)
    {
        double mostSim=HierCluster.maxDitanceBetweenClusters(patents,this.m_Clusters.get(0),this.m_Clusters.get(1));
        int most_i=0;
        int most_j=1;

        for(int i=0;i<this.m_Clusters.size()-1;i++)
            for(int j=i+1;j<this.m_Clusters.size();j++)
            {
                double temp=HierCluster.maxDitanceBetweenClusters(patents,m_Clusters.get(i), m_Clusters.get(j));

                //      System.out.print(temp+" ");

                if (temp>mostSim)
                {

                    mostSim=temp;
                    most_i=i;
                    most_j=j;

                }
            }

        if (mostSim>eps){
        m_Clusters.get(most_i).getPatentsIndex().addAll(m_Clusters.get(most_j).getPatentsIndex());

        m_Clusters.remove(most_j);

            return true;
        }
        else
        {
            return false;
        }
    }
}
