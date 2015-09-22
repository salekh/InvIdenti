package clustering.hierClustering;

import Base.pair;
import clustering.patentDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sunlei on 15/9/21.
 */
public class hierCluster
{
    private ArrayList<Integer> instances=new ArrayList<>();

    public void addInstance(int num)
    {
        this.instances.add(num);
    }

    public void addInstances(ArrayList<Integer> instances)
    {
        this.instances.addAll(instances);
    }

    public ArrayList<Integer> getInstancesIndex()
    {
        return this.instances;
    }

    public static double maxDitanceBetweenClusters(Instances data,hierCluster c1,hierCluster c2, pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo)
    {
        patentDistance distance=new patentDistance(attriInfo);

        double maxdistance=distance.distance(data.instance(c1.getInstancesIndex().get(0)),data.instance(c2.getInstancesIndex().get(0)));

        for(Integer i:c1.getInstancesIndex())
        {
            for(Integer j:c2.getInstancesIndex())
            {
                double temp=distance.distance(data.instance(i),data.instance(j));
                if (temp>maxdistance) maxdistance=temp;
            }

        }

        return maxdistance;
    }
}
