package clustering.hierarchy;

import base.pair;
import base.patent;
import clustering.SimMatrix;
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
    protected boolean pCorrelation=true;
    protected double eps=0;
    protected SimMatrix simMatrix;
    protected ArrayList<pair<ArrayList<HierCluster>,Double>> resultClustering=new ArrayList<>();
    /**
     * set the number of the clusters
     * @param number the number of the cluster
     */
    public void set_NumClusters(int number) {
        this.m_NumClusters=number;
    }



    public void setpCorrelation(boolean pCorrelation) {this.pCorrelation=pCorrelation;}


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


    public void setM_Distance(AbstractDistance distance) {
        this.m_Distance=distance;
    }

    /**
     * Run the patent clustering
     * @param patents the arraylist of the patents
     * @param distance the distance function
     */
    public void buildCluster(ArrayList<patent> patents,AbstractDistance distance)
    {


        this.simMatrix=new SimMatrix(patents,distance);
        this.m_Distance=distance;
        initializeCluster(patents);

        current_NumClusters=m_Clusters.size();

        while(current_NumClusters>m_NumClusters)
        {
            if (!mergeCluster(patents)) {
                break;
            } else {
                current_NumClusters=m_Clusters.size();
                ArrayList<HierCluster> temp_result=new ArrayList<>();
                temp_result.addAll(this.get_Clusters());
                System.out.println("Number of Clusters:"+this.numberOfClusters());
                resultClustering.add(new pair<ArrayList<HierCluster>, Double>(temp_result,getWithinSmilarity(temp_result,simMatrix)));
            }
        }




        double min=Double.MIN_VALUE;
        for (pair<ArrayList<HierCluster>, Double> p:this.resultClustering) {

            System.out.println(p.firstarg.size());
            System.out.println(p.secondarg);
            if (min>p.secondarg) {
                min=p.secondarg;
                this.m_Clusters=p.firstarg;

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
        double mostSim=HierCluster.maxDitanceBetweenClusters(this.m_Clusters.get(0),this.m_Clusters.get(1),simMatrix);
        int most_i=0;
        int most_j=1;

        for(int i=0;i<this.m_Clusters.size()-1;i++)
            for(int j=i+1;j<this.m_Clusters.size();j++)
            {
                double temp=HierCluster.maxDitanceBetweenClusters(m_Clusters.get(i), m_Clusters.get(j),simMatrix);


                if(pCorrelation) {

                    if (temp>mostSim)
                        {
                            mostSim=temp;
                            most_i=i;
                            most_j=j;
                        }
                } else {

                    if (temp<mostSim)
                    {
                        mostSim=temp;
                        most_i=i;
                        most_j=j;
                    }
                }
            }


        if (this.pCorrelation){
        if (mostSim>eps) {
        m_Clusters.get(most_i).getPatentsIndex().addAll(m_Clusters.get(most_j).getPatentsIndex());

        m_Clusters.remove(most_j);

            return true;
        }
        else
        {
            return false;
        }
        }
        else {
            if (mostSim<eps){

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

    public double getWithinSmilarity(ArrayList<HierCluster> clusters,SimMatrix simMatrix) {
        double result=0.0;

        for(HierCluster c:clusters) {
            double temp=0;
            for(int i=0;i<c.getPatentsIndex().size()-1;i++) {
                for(int j=i+1;j<c.getPatentsIndex().size();j++) {
                    temp+=simMatrix.getSimbetweenPatents(c.getPatentsIndex().get(i),c.getPatentsIndex().get(j));
                }
            }
            result+=temp;
        }
        return result;
    }
}
