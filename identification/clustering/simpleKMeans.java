package clustering;

import weka.classifiers.rules.DecisionTableHashKey;
import weka.clusterers.NumberOfClustersRequestable;
import weka.clusterers.RandomizableClusterer;
import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Created by sunlei on 15/9/7.
 */
public class simpleKMeans extends RandomizableClusterer implements NumberOfClustersRequestable, WeightedInstancesHandler {
    static final long serialVersionUID = -3235809600124455376L;
    private ReplaceMissingValues m_ReplaceMissingFilter;
    private int m_NumClusters = 2;
    private Instances m_ClusterCentroids;
    private Instances m_ClusterStdDevs;
    private int[][][] m_ClusterNominalCounts;
    private int[][] m_ClusterMissingCounts;
    private double[] m_FullMeansOrMediansOrModes;
    private double[] m_FullStdDevs;
    private int[][] m_FullNominalCounts;
    private int[] m_FullMissingCounts;
    private boolean m_displayStdDevs;
    private boolean m_dontReplaceMissing = false;
    private int[] m_ClusterSizes;
    private int m_MaxIterations = 500;
    private int m_Iterations = 0;
    private double[] m_squaredErrors;
    protected DistanceFunction m_DistanceFunction = new EuclideanDistance();
    private boolean m_PreserveOrder = false;
    protected int[] m_Assignments = null;

    public simpleKMeans() {
        this.m_SeedDefault = 10;
        this.setSeed(this.m_SeedDefault);
    }

    public String globalInfo() {
        return "Cluster data using the k means algorithm. Can use either the Euclidean distance (default) or the Manhattan distance. If the Manhattan distance is used, then centroids are computed as the component-wise median rather than mean.";
    }

    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capabilities.Capability.NO_CLASS);
        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capabilities.Capability.MISSING_VALUES);
        return result;
    }

    public void buildClusterer(Instances data) throws Exception {
        this.getCapabilities().testWithFail(data);
        this.m_Iterations = 0;
        this.m_ReplaceMissingFilter = new ReplaceMissingValues();
        Instances instances = new Instances(data);
        instances.setClassIndex(-1);
        if(!this.m_dontReplaceMissing) {
            this.m_ReplaceMissingFilter.setInputFormat(instances);
            instances = Filter.useFilter(instances, this.m_ReplaceMissingFilter);
        }

        this.m_FullMissingCounts = new int[instances.numAttributes()];
        if(this.m_displayStdDevs) {
            this.m_FullStdDevs = new double[instances.numAttributes()];
        }

        this.m_FullNominalCounts = new int[instances.numAttributes()][0];
        this.m_FullMeansOrMediansOrModes = this.moveCentroid(0, instances, false);

        for(int clusterAssignments = 0; clusterAssignments < instances.numAttributes(); ++clusterAssignments) {
            this.m_FullMissingCounts[clusterAssignments] = instances.attributeStats(clusterAssignments).missingCount;
            if(instances.attribute(clusterAssignments).isNumeric()) {
                if(this.m_displayStdDevs) {
                    this.m_FullStdDevs[clusterAssignments] = Math.sqrt(instances.variance(clusterAssignments));
                }

                if(this.m_FullMissingCounts[clusterAssignments] == instances.numInstances()) {
                    this.m_FullMeansOrMediansOrModes[clusterAssignments] = 0.0D / 0.0;
                }
            } else {
                this.m_FullNominalCounts[clusterAssignments] = instances.attributeStats(clusterAssignments).nominalCounts;
                if(this.m_FullMissingCounts[clusterAssignments] > this.m_FullNominalCounts[clusterAssignments][Utils.maxIndex(this.m_FullNominalCounts[clusterAssignments])]) {
                    this.m_FullMeansOrMediansOrModes[clusterAssignments] = -1.0D;
                }
            }
        }

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
        /**Initialize the centroids**/
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
        /****************************/
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

        if(this.m_displayStdDevs) {
            this.m_ClusterStdDevs = new Instances(instances, this.m_NumClusters);
        }

        this.m_ClusterSizes = new int[this.m_NumClusters];

        for(i = 0; i < this.m_NumClusters; ++i) {
            if(this.m_displayStdDevs) {
                double[] var17 = new double[instances.numAttributes()];

                for(j = 0; j < instances.numAttributes(); ++j) {
                    if(instances.attribute(j).isNumeric()) {
                        var17[j] = Math.sqrt(tempI[i].variance(j));
                    } else {
                        var17[j] = Instance.missingValue();
                    }
                }

                this.m_ClusterStdDevs.add(new Instance(1.0D, var17));
            }

            this.m_ClusterSizes[i] = tempI[i].numInstances();
        }

        this.m_DistanceFunction.clean();
    }

    protected double[] moveCentroid(int centroidIndex, Instances members, boolean updateClusterInfo) {
        double[] vals = new double[members.numAttributes()];
        Instances sortedMembers = null;
        int middle = 0;
        boolean dataIsEven = false;
        if(this.m_DistanceFunction instanceof ManhattanDistance) {
            middle = (members.numInstances() - 1) / 2;
            dataIsEven = members.numInstances() % 2 == 0;
            if(this.m_PreserveOrder) {
                sortedMembers = members;
            } else {
                sortedMembers = new Instances(members);
            }
        }

        for(int j = 0; j < members.numAttributes(); ++j) {
            if(!(this.m_DistanceFunction instanceof EuclideanDistance) && !members.attribute(j).isNominal()) {
                if(this.m_DistanceFunction instanceof ManhattanDistance) {
                    if(members.numInstances() == 1) {
                        vals[j] = members.instance(0).value(j);
                    } else {
                        vals[j] = sortedMembers.kthSmallestValue(j, middle + 1);
                        if(dataIsEven) {
                            vals[j] = (vals[j] + sortedMembers.kthSmallestValue(j, middle + 2)) / 2.0D;
                        }
                    }
                }
            } else {
                vals[j] = members.meanOrMode(j);
            }

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
        double minDist = 2.147483647E9D;
        int bestCluster = 0;

        for(int i = 0; i < this.m_NumClusters; ++i) {
            double dist = this.m_DistanceFunction.distance(instance, this.m_ClusterCentroids.instance(i));
            if(dist < minDist) {
                minDist = dist;
                bestCluster = i;
            }
        }

        if(updateErrors) {
            if(this.m_DistanceFunction instanceof EuclideanDistance) {
                minDist *= minDist;
            }

            this.m_squaredErrors[bestCluster] += minDist;
        }

        return bestCluster;
    }

    public int clusterInstance(Instance instance) throws Exception {
        Instance inst = null;
        if(!this.m_dontReplaceMissing) {
            this.m_ReplaceMissingFilter.input(instance);
            this.m_ReplaceMissingFilter.batchFinished();
            inst = this.m_ReplaceMissingFilter.output();
        } else {
            inst = instance;
        }

        return this.clusterProcessedInstance(inst, false);
    }

    /***********Doesn't to Change******************/

    public int numberOfClusters() throws Exception {
        return this.m_NumClusters;
    }

    public Enumeration listOptions() {
        Vector result = new Vector();
        result.addElement(new Option("\tnumber of clusters.\n\t(default 2).", "N", 1, "-N <num>"));
        result.addElement(new Option("\tDisplay std. deviations for centroids.\n", "V", 0, "-V"));
        result.addElement(new Option("\tDon\'t replace missing values with mean/mode.\n", "M", 0, "-M"));
        result.add(new Option("\tDistance function to use.\n\t(default: weka.core.EuclideanDistance)", "A", 1, "-A <classname and options>"));
        result.add(new Option("\tMaximum number of iterations.\n", "I", 1, "-I <num>"));
        result.addElement(new Option("\tPreserve order of instances.\n", "O", 0, "-O"));
        Enumeration en = super.listOptions();

        while(en.hasMoreElements()) {
            result.addElement(en.nextElement());
        }

        return result.elements();
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

    public String displayStdDevsTipText() {
        return "Display std deviations of numeric attributes and counts of nominal attributes.";
    }

    public void setDisplayStdDevs(boolean stdD) {
        this.m_displayStdDevs = stdD;
    }

    public boolean getDisplayStdDevs() {
        return this.m_displayStdDevs;
    }

    public String dontReplaceMissingValuesTipText() {
        return "Replace missing values globally with mean/mode.";
    }

    public void setDontReplaceMissingValues(boolean r) {
        this.m_dontReplaceMissing = r;
    }

    public boolean getDontReplaceMissingValues() {
        return this.m_dontReplaceMissing;
    }

    public String distanceFunctionTipText() {
        return "The distance function to use for instances comparison (default: weka.core.EuclideanDistance). ";
    }

    public DistanceFunction getDistanceFunction() {
        return this.m_DistanceFunction;
    }

    /*************************************************************************************************************/

    public void setDistanceFunction(DistanceFunction df) throws Exception {
        if(!(df instanceof EuclideanDistance) && !(df instanceof ManhattanDistance)) {
            throw new Exception("SimpleKMeans currently only supports the Euclidean and Manhattan distances.");
        } else {
            this.m_DistanceFunction = df;
        }
    }

    public String preserveInstancesOrderTipText() {
        return "Preserve order of instances.";
    }

    public void setPreserveInstancesOrder(boolean r) {
        this.m_PreserveOrder = r;
    }

    public boolean getPreserveInstancesOrder() {
        return this.m_PreserveOrder;
    }

    public void setOptions(String[] options) throws Exception {
        this.m_displayStdDevs = Utils.getFlag("V", options);
        this.m_dontReplaceMissing = Utils.getFlag("M", options);
        String optionString = Utils.getOption('N', options);
        if(optionString.length() != 0) {
            this.setNumClusters(Integer.parseInt(optionString));
        }

        optionString = Utils.getOption("I", options);
        if(optionString.length() != 0) {
            this.setMaxIterations(Integer.parseInt(optionString));
        }

        String distFunctionClass = Utils.getOption('A', options);
        if(distFunctionClass.length() != 0) {
            String[] distFunctionClassSpec = Utils.splitOptions(distFunctionClass);
            if(distFunctionClassSpec.length == 0) {
                throw new Exception("Invalid DistanceFunction specification string.");
            }

            String className = distFunctionClassSpec[0];
            distFunctionClassSpec[0] = "";
            this.setDistanceFunction((DistanceFunction)Utils.forName(DistanceFunction.class, className, distFunctionClassSpec));
        } else {
            this.setDistanceFunction(new EuclideanDistance());
        }

        this.m_PreserveOrder = Utils.getFlag("O", options);
        super.setOptions(options);
    }

    public String[] getOptions() {
        Vector result = new Vector();
        if(this.m_displayStdDevs) {
            result.add("-V");
        }

        if(this.m_dontReplaceMissing) {
            result.add("-M");
        }

        result.add("-N");
        result.add("" + this.getNumClusters());
        result.add("-A");
        result.add((this.m_DistanceFunction.getClass().getName() + " " + Utils.joinOptions(this.m_DistanceFunction.getOptions())).trim());
        result.add("-I");
        result.add("" + this.getMaxIterations());
        if(this.m_PreserveOrder) {
            result.add("-O");
        }

        String[] options = super.getOptions();

        for(int i = 0; i < options.length; ++i) {
            result.add(options[i]);
        }

        return (String[])((String[])result.toArray(new String[result.size()]));
    }

    public String toString() {
        if(this.m_ClusterCentroids == null) {
            return "No clusterer built yet!";
        } else {
            int maxWidth = 0;
            int maxAttWidth = 0;
            boolean containsNumeric = false;

            int plusMinus;
            int temp;
            for(plusMinus = 0; plusMinus < this.m_NumClusters; ++plusMinus) {
                for(temp = 0; temp < this.m_ClusterCentroids.numAttributes(); ++temp) {
                    if(this.m_ClusterCentroids.attribute(temp).name().length() > maxAttWidth) {
                        maxAttWidth = this.m_ClusterCentroids.attribute(temp).name().length();
                    }

                    if(this.m_ClusterCentroids.attribute(temp).isNumeric()) {
                        containsNumeric = true;
                        double cSize = Math.log(Math.abs(this.m_ClusterCentroids.instance(plusMinus).value(temp))) / Math.log(10.0D);
                        if(cSize < 0.0D) {
                            cSize = 1.0D;
                        }

                        cSize += 6.0D;
                        if((int)cSize > maxWidth) {
                            maxWidth = (int)cSize;
                        }
                    }
                }
            }

            String i;
            int var23;
            for(plusMinus = 0; plusMinus < this.m_ClusterCentroids.numAttributes(); ++plusMinus) {
                if(this.m_ClusterCentroids.attribute(plusMinus).isNominal()) {
                    Attribute var20 = this.m_ClusterCentroids.attribute(plusMinus);

                    for(var23 = 0; var23 < this.m_ClusterCentroids.numInstances(); ++var23) {
                        i = var20.value((int)this.m_ClusterCentroids.instance(var23).value(plusMinus));
                        if(i.length() > maxWidth) {
                            maxWidth = i.length();
                        }
                    }

                    for(var23 = 0; var23 < var20.numValues(); ++var23) {
                        i = var20.value(var23) + " ";
                        if(i.length() > maxAttWidth) {
                            maxAttWidth = i.length();
                        }
                    }
                }
            }

            if(this.m_displayStdDevs) {
                for(plusMinus = 0; plusMinus < this.m_ClusterCentroids.numAttributes(); ++plusMinus) {
                    if(this.m_ClusterCentroids.attribute(plusMinus).isNominal()) {
                        temp = Utils.maxIndex(this.m_FullNominalCounts[plusMinus]);
                        byte var25 = 6;
                        i = "" + this.m_FullNominalCounts[plusMinus][temp];
                        if(i.length() + var25 > maxWidth) {
                            maxWidth = i.length() + 1;
                        }
                    }
                }
            }

            int[] var19 = this.m_ClusterSizes;
            temp = var19.length;

            String attName;
            int var24;
            for(var23 = 0; var23 < temp; ++var23) {
                var24 = var19[var23];
                attName = "(" + var24 + ")";
                if(attName.length() > maxWidth) {
                    maxWidth = attName.length();
                }
            }

            if(this.m_displayStdDevs && maxAttWidth < "missing".length()) {
                maxAttWidth = "missing".length();
            }

            String var22 = "+/-";
            maxAttWidth += 2;
            if(this.m_displayStdDevs && containsNumeric) {
                maxWidth += var22.length();
            }

            if(maxAttWidth < "Attribute".length() + 2) {
                maxAttWidth = "Attribute".length() + 2;
            }

            if(maxWidth < "Full Data".length()) {
                maxWidth = "Full Data".length() + 1;
            }

            if(maxWidth < "missing".length()) {
                maxWidth = "missing".length() + 1;
            }

            StringBuffer var21 = new StringBuffer();
            var21.append("\nkMeans\n======\n");
            var21.append("\nNumber of iterations: " + this.m_Iterations + "\n");
            if(this.m_DistanceFunction instanceof EuclideanDistance) {
                var21.append("Within cluster sum of squared errors: " + Utils.sum(this.m_squaredErrors));
            } else {
                var21.append("Sum of within cluster distances: " + Utils.sum(this.m_squaredErrors));
            }

            if(!this.m_dontReplaceMissing) {
                var21.append("\nMissing values globally replaced with mean/mode");
            }

            var21.append("\n\nCluster centroids:\n");
            var21.append(this.pad("Cluster#", " ", maxAttWidth + maxWidth * 2 + 2 - "Cluster#".length(), true));
            var21.append("\n");
            var21.append(this.pad("Attribute", " ", maxAttWidth - "Attribute".length(), false));
            var21.append(this.pad("Full Data", " ", maxWidth + 1 - "Full Data".length(), true));

            for(var23 = 0; var23 < this.m_NumClusters; ++var23) {
                i = "" + var23;
                var21.append(this.pad(i, " ", maxWidth + 1 - i.length(), true));
            }

            var21.append("\n");
            String var27 = "(" + Utils.sum(this.m_ClusterSizes) + ")";
            var21.append(this.pad(var27, " ", maxAttWidth + maxWidth + 1 - var27.length(), true));

            for(var24 = 0; var24 < this.m_NumClusters; ++var24) {
                var27 = "(" + this.m_ClusterSizes[var24] + ")";
                var21.append(this.pad(var27, " ", maxWidth + 1 - var27.length(), true));
            }

            var21.append("\n");
            var21.append(this.pad("", "=", maxAttWidth + maxWidth * (this.m_ClusterCentroids.numInstances() + 1) + this.m_ClusterCentroids.numInstances() + 1, true));
            var21.append("\n");

            for(var24 = 0; var24 < this.m_ClusterCentroids.numAttributes(); ++var24) {
                attName = this.m_ClusterCentroids.attribute(var24).name();
                var21.append(attName);

                for(int strVal = 0; strVal < maxAttWidth - attName.length(); ++strVal) {
                    var21.append(" ");
                }

                String valMeanMode;
                String var26;
                if(this.m_ClusterCentroids.attribute(var24).isNominal()) {
                    if(this.m_FullMeansOrMediansOrModes[var24] == -1.0D) {
                        valMeanMode = this.pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                    } else {
                        valMeanMode = this.pad(var26 = this.m_ClusterCentroids.attribute(var24).value((int)this.m_FullMeansOrMediansOrModes[var24]), " ", maxWidth + 1 - var26.length(), true);
                    }
                } else if(Double.isNaN(this.m_FullMeansOrMediansOrModes[var24])) {
                    valMeanMode = this.pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                } else {
                    valMeanMode = this.pad(var26 = Utils.doubleToString(this.m_FullMeansOrMediansOrModes[var24], maxWidth, 4).trim(), " ", maxWidth + 1 - var26.length(), true);
                }

                var21.append(valMeanMode);

                for(int stdDevVal = 0; stdDevVal < this.m_NumClusters; ++stdDevVal) {
                    if(this.m_ClusterCentroids.attribute(var24).isNominal()) {
                        if(this.m_ClusterCentroids.instance(stdDevVal).isMissing(var24)) {
                            valMeanMode = this.pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                        } else {
                            valMeanMode = this.pad(var26 = this.m_ClusterCentroids.attribute(var24).value((int)this.m_ClusterCentroids.instance(stdDevVal).value(var24)), " ", maxWidth + 1 - var26.length(), true);
                        }
                    } else if(this.m_ClusterCentroids.instance(stdDevVal).isMissing(var24)) {
                        valMeanMode = this.pad("missing", " ", maxWidth + 1 - "missing".length(), true);
                    } else {
                        valMeanMode = this.pad(var26 = Utils.doubleToString(this.m_ClusterCentroids.instance(stdDevVal).value(var24), maxWidth, 4).trim(), " ", maxWidth + 1 - var26.length(), true);
                    }

                    var21.append(valMeanMode);
                }

                var21.append("\n");
                if(this.m_displayStdDevs) {
                    String var29 = "";
                    if(this.m_ClusterCentroids.attribute(var24).isNominal()) {
                        Attribute var28 = this.m_ClusterCentroids.attribute(var24);

                        int count;
                        int k;
                        for(count = 0; count < var28.numValues(); ++count) {
                            String percent = "  " + var28.value(count);
                            var21.append(this.pad(percent, " ", maxAttWidth + 1 - percent.length(), false));
                            int percentS = this.m_FullNominalCounts[var24][count];
                            k = (int)((double)this.m_FullNominalCounts[var24][count] / (double)Utils.sum(this.m_ClusterSizes) * 100.0D);
                            String percentS1 = "" + k + "%)";
                            percentS1 = this.pad(percentS1, " ", 5 - percentS1.length(), true);
                            var29 = "" + percentS + " (" + percentS1;
                            var29 = this.pad(var29, " ", maxWidth + 1 - var29.length(), true);
                            var21.append(var29);

                            for(int k1 = 0; k1 < this.m_NumClusters; ++k1) {
                                percentS = this.m_ClusterNominalCounts[k1][var24][count];
                                k = (int)((double)this.m_ClusterNominalCounts[k1][var24][count] / (double)this.m_ClusterSizes[k1] * 100.0D);
                                percentS1 = "" + k + "%)";
                                percentS1 = this.pad(percentS1, " ", 5 - percentS1.length(), true);
                                var29 = "" + percentS + " (" + percentS1;
                                var29 = this.pad(var29, " ", maxWidth + 1 - var29.length(), true);
                                var21.append(var29);
                            }

                            var21.append("\n");
                        }

                        if(this.m_FullMissingCounts[var24] > 0) {
                            var21.append(this.pad("  missing", " ", maxAttWidth + 1 - "  missing".length(), false));
                            count = this.m_FullMissingCounts[var24];
                            int var30 = (int)((double)this.m_FullMissingCounts[var24] / (double)Utils.sum(this.m_ClusterSizes) * 100.0D);
                            String var31 = "" + var30 + "%)";
                            var31 = this.pad(var31, " ", 5 - var31.length(), true);
                            var29 = "" + count + " (" + var31;
                            var29 = this.pad(var29, " ", maxWidth + 1 - var29.length(), true);
                            var21.append(var29);

                            for(k = 0; k < this.m_NumClusters; ++k) {
                                count = this.m_ClusterMissingCounts[k][var24];
                                var30 = (int)((double)this.m_ClusterMissingCounts[k][var24] / (double)this.m_ClusterSizes[k] * 100.0D);
                                var31 = "" + var30 + "%)";
                                var31 = this.pad(var31, " ", 5 - var31.length(), true);
                                var29 = "" + count + " (" + var31;
                                var29 = this.pad(var29, " ", maxWidth + 1 - var29.length(), true);
                                var21.append(var29);
                            }

                            var21.append("\n");
                        }

                        var21.append("\n");
                    } else {
                        if(Double.isNaN(this.m_FullMeansOrMediansOrModes[var24])) {
                            var29 = this.pad("--", " ", maxAttWidth + maxWidth + 1 - 2, true);
                        } else {
                            var29 = this.pad(var26 = var22 + Utils.doubleToString(this.m_FullStdDevs[var24], maxWidth, 4).trim(), " ", maxWidth + maxAttWidth + 1 - var26.length(), true);
                        }

                        var21.append(var29);

                        for(int j = 0; j < this.m_NumClusters; ++j) {
                            if(this.m_ClusterCentroids.instance(j).isMissing(var24)) {
                                var29 = this.pad("--", " ", maxWidth + 1 - 2, true);
                            } else {
                                var29 = this.pad(var26 = var22 + Utils.doubleToString(this.m_ClusterStdDevs.instance(j).value(var24), maxWidth, 4).trim(), " ", maxWidth + 1 - var26.length(), true);
                            }

                            var21.append(var29);
                        }

                        var21.append("\n\n");
                    }
                }
            }

            var21.append("\n\n");
            return var21.toString();
        }
    }

    private String pad(String source, String padChar, int length, boolean leftPad) {
        StringBuffer temp = new StringBuffer();
        int i;
        if(leftPad) {
            for(i = 0; i < length; ++i) {
                temp.append(padChar);
            }

            temp.append(source);
        } else {
            temp.append(source);

            for(i = 0; i < length; ++i) {
                temp.append(padChar);
            }
        }

        return temp.toString();
    }

    public Instances getClusterCentroids() {
        return this.m_ClusterCentroids;
    }

    public Instances getClusterStandardDevs() {
        return this.m_ClusterStdDevs;
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

    public static void main(String[] argv) {
        runClusterer(new simpleKMeans(), argv);
    }
}
