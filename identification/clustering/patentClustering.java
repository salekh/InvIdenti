package clustering;

import Base.pair;
import Base.patent;
import Base.patentCluster;
import org.carrot2.core.LanguageCode;
import preprocessing.patentPreprocessing;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leisun on 15/9/18.
 */
public abstract class patentClustering
{

    protected ArrayList<patent> patents=new ArrayList<>();
    protected LanguageCode language=LanguageCode.ENGLISH;
    protected ArrayList<patentCluster> clusters=new ArrayList<>();
    protected Instances instances;

    protected Instances final_instances;
    protected ArrayList<Integer> dims;

    protected HashMap<String,Integer> attriNum;
    protected pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo;

    public void setLanguage(LanguageCode code)
    {
        this.language=code;
    }


    public ArrayList<patentCluster> getClusters()
    {
        return this.clusters;
    }
    /**Get original index of the patent**/
    public int getIndex(patent p)
    {

        for(int i=0;i<this.patents.size();i++)
        {
            if(patents.get(i).getPatent_number().equals(p.getPatent_number())) return i;
        }

        return -1;
    }

    /**output the clustering result**/
    public String toString()
    {
        String str="Cluster Number:"+clusters.size();

        for(int i=0;i<this.clusters.size();i++)
        {
            str+=("Cluster "+i);
            str+=("\n================================\n");
            for(patent p:this.clusters.get(i).getPatents())
            {
                str+=p.getPatent_number()+"\t"+p.getAuthor();
                if(p.getAuthor().length()<13)
                    for(int j=0;j<(13-p.getAuthor().length());j++)
                    {
                        str+=" ";
                    }
                str+="\t"+p.getTitle()+"\n";
            }
        }
        return str;
    }

    public patentClustering(ArrayList<patent> patents)
    {
        this.patents=patents;
        //Set Option

        preprocess();
    }

    /**Preprocessing for the clustering**/
    protected void preprocess() {
        ArrayList<Attribute> attributes=new ArrayList<>();
        HashMap<String,Integer> attributeIndex=new HashMap<>();
        HashMap<String,Integer> attributesNumber=new HashMap<>();

        patentPreprocessing preprocess = new patentPreprocessing(this.patents);
        preprocess.setLanguage(this.language);
        preprocess.preprocess();
        this.patents = preprocess.getPatents();
        /**Set dataset format**/

        FastVector var0 = new FastVector();

        attributes.add(new Attribute("Assignee", (FastVector) null));
        attriNum.put("Assignee", attriNum.size());
        attributes.add(new Attribute("Category", (FastVector) null));
        attriNum.put("Category", attriNum.size());


        if (true) {
            for (int i = 0; i < patents.get(0).getTd().rows(); i++) {
                attributes.add(new Attribute("FullText" + Integer.toString(i)));
                attriNum.put("FullText" + Integer.toString(i), attriNum.size());
            }
            attributesNumber.put("FullText",patents.get(0).getTd().rows());
        }
        if (true) {

            for (int i = 0; i < patents.get(0).getTd_abs().rows(); i++) {
                attributes.add(new Attribute("Abstract" + Integer.toString(i)));
                attriNum.put("Abstract" + Integer.toString(i), attriNum.size());
            }
            attributesNumber.put("Abstract",patents.get(0).getTd_abs().rows());
        }
        if (true) {

            for (int i = 0; i < patents.get(0).getTd_claims().rows(); i++) {
                attributes.add(new Attribute("Claims" + Integer.toString(i)));
                attriNum.put("Claims" + Integer.toString(i), attriNum.size());
            }
            attributesNumber.put("Claims",patents.get(0).getTd_claims().rows());
        }
        if (true) {

            for (int i = 0; i < patents.get(0).getTd_des().rows(); i++) {
                attributes.add(new Attribute("Description" + Integer.toString(i)));
                attriNum.put("Description" + Integer.toString(i), attriNum.size());
            }
            attributesNumber.put("Description",patents.get(0).getTd().rows());
        }
        int index=0;
        for (Attribute var1 : attributes) {
            var0.addElement(var1);
            attributeIndex.put(var1.name(), index);
            index++;
        }
        this.attriInfo=new pair<>(attributeIndex,attributesNumber);

        /**Initialize the dataset**/
        instances = new Instances("Patent", var0, patents.size());

        for (int i = 0; i < patents.size(); i++) {
            Instance var3 = new Instance(2);
            var3.setDataset(instances);
            var3.setValue(var3.attribute(attriNum.get("Assginee")), patents.get(i).getAssignee());
            var3.setValue(var3.attribute(attriNum.get("Category")), patents.get(i).getCategory());

            if ( true) {
                for (int j = 0; j < patents.get(0).getTd().rows(); j++) {
                    var3.setValue(var3.attribute(attriNum.get("FullText" + Integer.toString(j))), patents.get(i).getTd().get(j, 0));
                }
            }
            if (true) {
                for (int j = 0; j < patents.get(0).getTd_abs().rows(); j++) {
                    var3.setValue(var3.attribute(attriNum.get("Abstract" + Integer.toString(j))), patents.get(i).getTd_abs().get(j, 0));
                }
            }
            if ( true) {
                for (int j = 0; j < patents.get(0).getTd_claims().rows(); j++) {
                    var3.setValue(var3.attribute(attriNum.get("Claims" + Integer.toString(j))), patents.get(i).getTd_claims().get(j, 0));
                }
            }
            if ( true) {
                for (int j = 0; j < patents.get(0).getTd_des().rows(); j++) {
                    var3.setValue(var3.attribute(attriNum.get("Description" + Integer.toString(j))), patents.get(i).getTd_des().get(j, 0));
                }
            }
            instances.add(var3);
        }
    }
}
