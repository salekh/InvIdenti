package preprocessing;

import base.pair;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.jblas.DoubleMatrix;

import java.time.Year;
import java.util.ArrayList;

/**
 * Created by leisun on 15/11/5.
 */
public class LRWeightLearning extends ParameterLearning {

    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();

    public AbstractDistance estimateDistanceFunction() {

        this.generateLRTraiingData();

        pair<DoubleMatrix,DoubleMatrix> result=this.logisticRTrainingDataGenerator();


        DoubleMatrix s=result.firstarg.transpose().mmul(result.firstarg);

        for(int i=0;i<s.rows;i++) {
            for (int j=0;j<s.columns;j++) {
                System.out.print(s.get(i,j)+";");
            }
            System.out.println();
        }

        DoubleMatrix s1=result.firstarg.transpose().mmul(result.secondarg);


        for(int i=0;i<s1.rows;i++) {
            for (int j=0;j<s1.columns;j++) {
                System.out.print(s1.get(i,j)+" ");
            }
            System.out.println();
        }

        return new CosDistance();
    }






    public void generateLRTraiingData() {
        for (int i = 0; i < this.patents.size() - 1; i++) {
            for (int j = i + 1; j < this.patents.size(); j++) {
                int[] tempint = new int[2];
                tempint[0] = i;
                tempint[1] = j;
                double result;
                if (patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                    result = 1.0;
                } else {
                    result = 0.0;
                }
                this.lrTrainingData.add(new pair<>(tempint, result));
            }
        }
    }

    public pair<DoubleMatrix,DoubleMatrix> logisticRTrainingDataGenerator() {
        double[][] var0=new double[this.lrTrainingData.size()][numberofOptions+1];
        double[][] var1=new double[this.lrTrainingData.size()][1];

        int i=0;


        for(pair<int[],Double> p:this.lrTrainingData) {
            var0[i][0]=1.0;
            for(int j=0;j<numberofOptions;j++) {
                var0[i][j+1]=distances.get(j).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));

            }
            var1[i][0]=p.secondarg;
            i++;
        }

        DoubleMatrix X=new DoubleMatrix(var0);
        DoubleMatrix Y=new DoubleMatrix(var1);
        return new pair<>(X, Y);
    }
}
