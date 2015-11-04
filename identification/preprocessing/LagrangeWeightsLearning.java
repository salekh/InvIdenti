package preprocessing;

import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/11/3.
 */
public class LagrangeWeightsLearning extends ParameterLearning {


    double beta=4;


    public LagrangeWeightsLearning() {
        super();
    }



    /**
     *
     * @return the estimated distance function by using Lagrange method.
     */
    public AbstractDistance estimateDistanceFunction() {
        if (initialization) {
            return estimatePara(beta);
        } else {
        logger.error("parameter learning haven't been initialized!");
        return new CosDistance();
        }

    }

    /**
     * Estimate the distance function with the weigths
     * @param beta weight learning parameter
     * @return the distance function
     */
    public CosDistance estimatePara(double beta) {
        ArrayList<Double> weights = new ArrayList<>();
        double[] sums = new double[numberofOptions];
        for (double var0 : sums) {
            var0 = 0.0;
        }


        for (int i = 0; i < patents.size() - 1; i++) {
            for (int j = i + 1; j < patents.size(); j++) {
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    for (int m = 0; m < optionsName.size(); m++) {
                        if (ini.getOptionValue(optionsName.get(m))) {
                            sums[m] += distances.get(m).distance(patents.get(i), patents.get(j));
                        }
                    }
                }
            }
        }


        for (int i = 0; i < optionsName.size(); i++) {
            if (ini.getOptionValue(optionsName.get(i))) {
                double var2 = 0;
                for (int j = 0; j < optionsName.size(); j++) {
                    if (ini.getOptionValue(optionsName.get(j))) {
                        var2 += Math.pow(sums[i] / sums[j], (1 / (beta - 1)));
                    }
                }
                weights.add(Math.pow(1 / var2, beta));
            } else {
                weights.add(0.0);
            }
        }

        CosDistance estimatedDistance = this.generateDistanceFunction(null, weights);

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < patents.size() - 1; i++) {
            for (int j = i + 1; j < patents.size(); j++) {
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    double var5 = estimatedDistance.distance(patents.get(i), patents.get(j));
                    if (var5 > max) max = var5;
                    if (var5 < min) min = var5;
                }
            }
        }

        this.threshold = max;


        return estimatedDistance;
    }
}
