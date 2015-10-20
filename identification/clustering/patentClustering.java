package clustering;

import base.pair;
import base.patent;
import base.patentCluster;
import org.carrot2.core.LanguageCode;
import org.ini4j.Wini;
import preprocessing.patentPreprocessing;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
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
    protected int number_Cluster=4;
    protected boolean pCorrelation=true;

    protected Instances final_instances;
    protected ArrayList<Integer> dims;

    protected pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo;

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
        String str="Cluster Number:"+clusters.size()+"\n";
        str+=("=================================================================================================================\n");


        for(int i=0;i<this.clusters.size();i++)
        {
            str+=("Cluster "+i);
            str+=("\n----------\n");
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
            str+=("=================================================================================================================\n");
        }
        return str;
    }

    public patentClustering(ArrayList<patent> patents)
    {
        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));
            this.pCorrelation=initalFile.get("DistanceOption","PCorrelation").equalsIgnoreCase("true");

        } catch (IOException e)
        {
            System.out.println("Initial File 'invidenti.ini not found',distance function will use default options");
        }
        this.patents=patents;
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

    }
}
