package clustering.simpleKmeans;

import Base.pair;
import clustering.patentDistance;
import weka.classifiers.rules.DecisionTableHashKey;
import weka.clusterers.NumberOfClustersRequestable;
import weka.clusterers.RandomizableClusterer;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by sunlei on 15/9/7.
 */
public class simpleKMeansCore extends RandomizableClusterer {

    private int m_NumClusters = 2;
    private Instances m_ClusterCentroids;
    private int[][][] m_ClusterNominalCounts;
    private int[][] m_ClusterMissingCounts;
    private int[] m_ClusterSizes;
    private int m_MaxIterations = 500;
    private int m_Iterations = 0;
    private double[] m_squaredErrors;
    protected patentDistance m_DistanceFunction = new patentDistance();
    private boolean m_PreserveOrder = false;
    protected int[] m_Assignments = null;
    //dimension of the patent document tf vector
    protected int dimension=0;


    protected pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo;


    public simpleKMeansCore() {
        this.m_SeedDefault = 10;
        this.setSeed(this.m_SeedDefault);
        this.dimension=dimension;
    }

    public void setAttriInfor(pair<HashMap<String,Integer>,HashMap<String,Integer>> info)
    {
        this.attriInfo=info;
    }

    public void buildClusterer(Instances data) throws Exception {
        //this.getCapabilities().testWithFail(data);
        this.m_Iterations = 0;

        Instances instances = new Instances(data);
        instances.setClassIndex(-1);

        this.m_ClusterCentroids = new Instances(instances, this.m_NumClusters);

        int[] var16 = new int[instances.numInstances()];

        if(this.m_PreserveOrder) {
            this.m_Assignments = var16;
        }

        this.m_DistanceFunction.setInstances(instances);
        Random RandomO = new Random((long)this.getSeed());
        HashMap initC = new HashMap();
        DecisionTableHashKey hk = null;
        Instances initInstances = null;

        if(this.m_PreserveOrder) {
            initInstances = new Instances(instances);
        } else {
            initInstances = instances;
        }
        /**Initialize the centroids***********************************/
        int i;
        for(i = initInstances.numInstances() - 1; i >= 0; --i) {
            int instIndex = RandomO.nextInt(i + 1);
            hk = new DecisionTableHashKey(initInstances.instance(instIndex), initInstances.numAttributes(), true);
            if(!initC.containsKey(hk)) {
                this.m_ClusterCentroids.add(initInstances.instance(instIndex));
                initC.put(hk, (Object)null);
            }

            initInstances.swap(i, instIndex);
            if(this.m_ClusterCentroids.numInstances() == this.m_NumClusters) {
                break;
            }
        }
        /***********************************************************/
        this.m_NumClusters = this.m_ClusterCentroids.numInstances();
        initInstances = null;
        boolean converged = false;//judge for the convergence
        Instances[] tempI = new Instances[this.m_NumClusters];
        this.m_squaredErrors = new double[this.m_NumClusters];
        this.m_ClusterNominalCounts = new int[this.m_NumClusters][instances.numAttributes()][0];
        this.m_ClusterMissingCounts = new int[this.m_NumClusters][instances.numAttributes()];

        int j;
        while(!converged) {
            int emptyClusterCount = 0;
            ++this.m_Iterations;
            converged = true;
            /**Set every instance to the nearest centroid, if the centroid is different from the previous one,then not convergent**/
            for(i = 0; i < instances.numInstances(); ++i) {
                Instance vals2 = instances.instance(i);
                j = this.clusterProcessedInstance(vals2, true); //set every instance to the nearest centroid
                if(j != var16[i]) {
                    converged = false;
                }

                var16[i] = j;
            }
            /************************************************************************************************************/
            this.m_ClusterCentroids = new Instances(instances, this.m_NumClusters);

            for(i = 0; i < this.m_NumClusters; ++i) {
                tempI[i] = new Instances(instances, 0);
            }

            for(i = 0; i < instances.numInstances(); ++i) {
                tempI[var16[i]].add(instances.instance(i));
            }

            for(i = 0; i < this.m_NumClusters; ++i) {
                if(tempI[i].numInstances() == 0) {
                    ++emptyClusterCount;
                } else {
                    this.moveCentroid(i, tempI[i], true);
                }
            }

            if(this.m_Iterations == this.m_MaxIterations) {
                converged = true;
            }

            if(emptyClusterCount > 0) {
                this.m_NumClusters -= emptyClusterCount;
                if(!converged) {
                    tempI = new Instances[this.m_NumClusters];
                } else {
                    Instances[] var18 = new Instances[this.m_NumClusters];
                    j = 0;
                    int k = 0;

                    while(true) {
                        if(k >= tempI.length) {
                            tempI = var18;
                            break;
                        }

                        if(tempI[k].numInstances() > 0) {
                            var18[j] = tempI[k];

                            for(i = 0; i < tempI[k].numAttributes(); ++i) {
                                this.m_ClusterNominalCounts[j][i] = this.m_ClusterNominalCounts[k][i];
                            }

                            ++j;
                        }

                        ++k;
                    }
                }
            }

            if(!converged) {
                this.m_squaredErrors = new double[this.m_NumClusters];
                this.m_ClusterNominalCounts = new int[this.m_NumClusters][instances.numAttributes()][0];
            }
        }



        this.m_ClusterSizes = new int[this.m_NumClusters];

        for(i = 0; i < this.m_NumClusters; ++i) {
            this.m_ClusterSizes[i] = tempI[i].numInstances();
        }

        this.m_DistanceFunction.clean();
    }

    protected double[] moveCentroid(int centroidIndex, Instances members, boolean updateClusterInfo) {
        double[] vals = new double[members.numAttributes()];
        for(int j = 0; j < members.numAttributes(); ++j) {
            vals[j] = members.meanOrMode(j);
            if(updateClusterInfo) {
                this.m_ClusterMissingCounts[centroidIndex][j] = members.attributeStats(j).missingCount;
                this.m_ClusterNominalCounts[centroidIndex][j] = members.attributeStats(j).nominalCounts;
                if(members.attribute(j).isNominal()) {
                    if(this.m_ClusterMissingCounts[centroidIndex][j] > this.m_ClusterNominalCounts[centroidIndex][j][Utils.maxIndex(this.m_ClusterNominalCounts[centroidIndex][j])]) {
                        vals[j] = Instance.missingValue();
                    }
                } else if(this.m_ClusterMissingCounts[centroidIndex][j] == members.numInstances()) {
                    vals[j] = Instance.missingValue();
                }
            }
        }
        if(updateClusterInfo) {
            this.m_ClusterCentroids.add(new Instance(1.0D, vals));
        }
        return vals;
    }

    private int clusterProcessedInstance(Instance instance, boolean updateErrors) {
        double minDist = 0;
        int bestCluster = 0;

        for(int i = 0; i < this.m_NumClusters; ++i) {
            double dist = this.m_DistanceFunction.distance(this.attriInfo,instance,this.m_ClusterCentroids.instance(i));
            if(dist > minDist) {
                minDist = dist;
                bestCluster = i;
            }
        }

        if(updateErrors) {

            this.m_squaredErrors[bestCluster] += minDist;
        }

        return bestCluster;
    }

    public int clusterInstance(Instance instance) throws Exception {
        Instance inst = instance;
        return this.clusterProcessedInstance(inst, false);
    }

    /***********Doesn't to Change******************/

    public int numberOfClusters() throws Exception {
        return this.m_NumClusters;
    }



    public String numClustersTipText() {
        return "set number of clusters";
    }

    public void setNumClusters(int n) throws Exception {
        if(n <= 0) {
            throw new Exception("Number of clusters must be > 0");
        } else {
            this.m_NumClusters = n;
        }
    }

    public int getNumClusters() {
        return this.m_NumClusters;
    }

    public String maxIterationsTipText() {
        return "set maximum number of iterations";
    }

    public void setMaxIterations(int n) throws Exception {
        if(n <= 0) {
            throw new Exception("Maximum number of iterations must be > 0");
        } else {
            this.m_MaxIterations = n;
        }
    }

    public int getMaxIterations() {
        return this.m_MaxIterations;
    }




    /*************************************************************************************************************/



    public String preserveInstancesOrderTipText() {
        return "Preserve order of instances.";
    }

    public void setPreserveInstancesOrder(boolean r) {
        this.m_PreserveOrder = r;
    }

    public boolean getPreserveInstancesOrder() {
        return this.m_PreserveOrder;
    }


    public Instances getClusterCentroids() {
        return this.m_ClusterCentroids;
    }


    public int[][][] getClusterNominalCounts() {
        return this.m_ClusterNominalCounts;
    }

    public double getSquaredError() {
        return Utils.sum(this.m_squaredErrors);
    }

    public int[] getClusterSizes() {
        return this.m_ClusterSizes;
    }

    public int[] getAssignments() throws Exception {
        if(!this.m_PreserveOrder) {
            throw new Exception("The assignments are only available when order of instances is preserved (-O)");
        } else if(this.m_Assignments == null) {
            throw new Exception("No assignments made.");
        } else {
            return this.m_Assignments;
        }
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 10537 $");
    }

}
