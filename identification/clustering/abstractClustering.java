package clustering;



import base.pair;
import base.patent;
import base.patentCluster;
import preprocessing.patentPreprocessing;

import org.carrot2.core.LanguageCode;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leisun on 15/9/18.
 */
public abstract class abstractClustering
{
    protected ArrayList<patent> patents=new ArrayList<>();
    protected LanguageCode language=LanguageCode.ENGLISH;
    protected ArrayList<patentCluster> clusters=new ArrayList<>();
    protected HashMap<String,Instances> instances=new HashMap<>();
    protected pair<HashMap<String,Integer>,HashMap<String,Integer>> attributes;
    protected Instances final_instances;
    protected ArrayList<Integer> dims;
    private int number_Cluster=2;

    public void setLanguage(LanguageCode code)
    {
        this.language=code;
    }
    public void setNumberofCluster(Integer num)
    {
        this.number_Cluster=num;
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

    public abstractClustering(ArrayList<patent> patents)
    {
        this.patents=patents;
        preprocess();
    }

    /**Preprocessing for the clustering**/
    protected void preprocess()
    {
        patentPreprocessing preprocess=new patentPreprocessing(this.patents);
        preprocess.setLanguage(this.language);
        preprocess.setUseDimensionalityReduction(false);
        preprocess.preprocess();
        this.patents=preprocess.getPatents();
        instances.clear();

        /** Add other attributes in the hash table **/
        FastVector f_others=new FastVector();
        f_others.addElement(new Attribute("Assignee",(FastVector)null));
        f_others.addElement(new Attribute("Category",(FastVector)null));
        Instances instances_others=new Instances("other",f_others,patents.size());
        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(2);
            temp.setDataset(instances_others);
            temp.setValue(temp.attribute(0),patents.get(i).getCategory());
            temp.setValue(temp.attribute(1),patents.get(i).getAssignee());
            instances_others.add(temp);
        }
        instances.put("Others",instances_others);

        /** Add full text vector **/
        FastVector f_full=new FastVector();
        int dimension_full=patents.get(0).getTd().rows();
        for(int i=0;i<dimension_full;i++)
            f_full.addElement(new Attribute(Integer.toString(i)));
        Instances instances_full=new Instances("fulltext",f_full,patents.size());
        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(dimension_full);
            temp.setDataset(instances_full);
            for(int j=0;j<dimension_full;j++)
            {
                temp.setValue(j, patents.get(i).getTd().get(j, 0));
            }
            instances_full.add(temp);
        }
        instances.put("Fulltext",instances_full);

        /** Add abstract vector **/
        FastVector f_abs=new FastVector();
        int dimension_abs=patents.get(0).getTd_abs().rows();
        for(int i=0;i<dimension_abs;i++)
            f_full.addElement(new Attribute(Integer.toString(i)));
        Instances instances_abs=new Instances("abs",f_abs,patents.size());
        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(dimension_abs);
            temp.setDataset(instances_abs);
            for(int j=0;j<dimension_abs;j++)
            {
                temp.setValue(j, patents.get(i).getTd_abs().get(j, 0));
            }
            instances_abs.add(temp);
        }
        instances.put("Abstract",instances_abs);

        /** Add claims vector **/
        FastVector f_claims=new FastVector();
        int dimension_claims=patents.get(0).getTd_claims().rows();
        for(int i=0;i<dimension_claims;i++)
            f_full.addElement(new Attribute(Integer.toString(i)));
        Instances instances_claims=new Instances("claims",f_claims,patents.size());
        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(dimension_claims);
            temp.setDataset(instances_claims);
            for(int j=0;j<dimension_claims;j++)
            {
                temp.setValue(j, patents.get(i).getTd_claims().get(j, 0));
            }
            instances_claims.add(temp);
        }
        instances.put("Claims",instances_claims);

        /** Add descrption vector **/
        FastVector f_des=new FastVector();
        int dimension_des=patents.get(0).getTd_des().rows();
        for(int i=0;i<dimension_des;i++)
            f_full.addElement(new Attribute(Integer.toString(i)));
        Instances instances_des=new Instances("des",f_des,patents.size());

        for(int i=0;i<patents.size();i++)
        {
            Instance temp=new Instance(dimension_des);
            temp.setDataset(instances_des);
            for(int j=0;j<dimension_des;j++)
            {
                temp.setValue(j, patents.get(i).getTd_des().get(j, 0));
            }
            instances_des.add(temp);
        }
        instances.put("Description",instances_des);
    }

    protected void buildFinalInstances()
    {
        int n=2;
        FastVector final_f=new FastVector();
        final_f.addElement(new Attribute("Category",(FastVector)null));
        final_f.addElement(new Attribute("Assignee", (FastVector) null));


    }
}
