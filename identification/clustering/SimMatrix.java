package clustering;

import base.patent;
import clustering.distancefunction.AbstractDistance;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/27.
 */
public class SimMatrix {

    ArrayList<ArrayList<Double>> simMatrix=new ArrayList<>();

    ArrayList<patent> patents;

    public ArrayList<String> patentsID;

    double threshold;

    AbstractDistance distance;

    public SimMatrix(ArrayList<patent> patents,AbstractDistance distance) {
        this.patents=patents;
        this.distance=distance;


        buildMatrix();



    }

    /**
     * Build the similarity matrix for the patents with distance function
     */
    private void buildMatrix() {

        for(int i=0;i<this.patents.size();i++) {
            ArrayList<Double> temp=new ArrayList<>();
            for (int j=0;j<this.patents.size();j++) {
                temp.add(0.0);
            }
            simMatrix.add(temp);
        }

        for(int i=0;i<this.patents.size()-1;i++) {
            for (int j=i+1;j<this.patents.size();j++) {
                double temp=distance.distance(this.patents.get(i),this.patents.get(j));

                simMatrix.get(i).set(j,temp);
                simMatrix.get(j).set(i,temp);
            }
        }
    }


    public void buildMatrix(double threshold,ArrayList<String> patentsID) {

        for(int i=0;i<this.patents.size();i++) {
            ArrayList<Double> temp=new ArrayList<>();
            for (int j=0;j<this.patents.size();j++) {
                temp.add(0.0);
            }
            simMatrix.add(temp);
        }

        for(int i=0;i<this.patents.size()-1;i++) {
            for (int j=i+1;j<this.patents.size();j++) {
                double temp=distance.distance(this.patents.get(i),this.patents.get(j));

                if (temp>threshold&&patentsID.get(i).equalsIgnoreCase(patentsID.get(j))) {
                double sum=0;


                    sum+=distance.weightLastName*distance.compareName(patents.get(i).getLastName(),patents.get(j).getLastName());
                    sum+=distance.weightFirstName*distance.compareName(patents.get(i).getFirstName(),patents.get(j).getFirstName());

                    if(sum<threshold) {
                        System.out.println(patents.get(i).getLastName()+" "+patents.get(i).getFirstName());
                        System.out.println(patents.get(j).getLastName()+" "+patents.get(j).getFirstName());
                    }
                }
                simMatrix.get(i).set(j,temp);
                simMatrix.get(j).set(i,temp);
            }
        }
    }

    public  ArrayList<ArrayList<Double>> getSimMatrix() {
        return simMatrix;
    }
    /**
     *
     * @param i patents i
     * @param j patens j
     * @return the similarity between the patent i and patent j
     */
    public double getSimbetweenPatents(int i,int j) {
        return simMatrix.get(i).get(j);
    }

}
