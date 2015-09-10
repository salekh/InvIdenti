package clustering;

import Base.patent;
import clustering.simpleKmeans.simpleKMeansPatent;
import preprocessing.USPTOSearch;

import java.util.ArrayList;

/**
 * Created by leisun on 15/9/8.
 */
public class testMain
{
    String patent_number_1="05754644,05757896,05815569,05828748,06067346,06104795,06128570,06212275,06263068,06353668,06393104";
    String patent_number_2="05871717,05891424,05891425,05902570";
    String patent_number_3="05718805,05853544,05972168";

    String patent_assignees_1="LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC,LUCENT TECHNOLOGIES INC";
    String patent_cat_1="379,379,379/323,379,379/340,379,701/123/180,379,379,379,379";
    String patent_assignees_2="PROCTER GAMBLE COMPANY,PROCTER GAMBLE,PROCTER GAMBLE COMPANY,PROCTER GAMBLE COMPANY";
    String patent_cat_2="424,424,424,424";
    String patent_assignees_3="J M VOITH GMBH,J M VOITH GMBH,VOITH SULZER PAPIERTECHNIK PATENT GMBH";
    String patent_cat_3="162,162,162";
    //String patent_number_4="06338395,07177676";
    String author_3="BUCK R";
    String author_2="BRETZLER E";
    String author_1="AKHTERUZZAMAN";
    //String author_4="ADISUSANTO";
    //String author_4="DILLON J";
    //String patent_number_4="06002589,06094075";

    ArrayList<patent> patents=new ArrayList<>();

    public void buildPatents()
    {

        String[] patents_number_1=this.patent_number_1.split(",");
        String[] assginees_1=this.patent_assignees_1.split(",");
        String[] catgories_1=this.patent_cat_1.split(",");
        int no=0;
        for(String str:patents_number_1)
        {
            USPTOSearch searchPatent=new USPTOSearch(str);
            patent p=new patent(str,searchPatent.getAbs(),searchPatent.getClaims(),searchPatent.getDescription(),searchPatent.getTitle(),catgories_1[no],assginees_1[no]);
            p.setAuthor(author_1);
            patents.add(p);
            no++;

        }


        String[] assginees_2=this.patent_assignees_1.split(",");
        String[] catgories_2=this.patent_cat_1.split(",");
        no=0;
        String[] patents_number_2=this.patent_number_2.split(",");
        for(String str:patents_number_2)
        {
            USPTOSearch searchPatent=new USPTOSearch(str);
            patent p=new patent(str,searchPatent.getAbs(),searchPatent.getClaims(),searchPatent.getDescription(),searchPatent.getTitle(),catgories_2[no],assginees_2[no]);
            p.setAuthor(author_2);
            patents.add(p);
            no++;

        }


        String[] assginees_3=this.patent_assignees_1.split(",");
        String[] catgories_3=this.patent_cat_1.split(",");
        no=0;
        String[] patents_number_3=this.patent_number_3.split(",");
        for(String str:patents_number_3)
        {
            USPTOSearch searchPatent=new USPTOSearch(str);
            patent p=new patent(str,searchPatent.getAbs(),searchPatent.getClaims(),searchPatent.getDescription(),searchPatent.getTitle(),catgories_3[no],assginees_3[no]);
            p.setAuthor(author_3);
            patents.add(p);
            no++;
         
        }
/*
        String[] patents_number_4=this.patent_number_4.split(",");
        for(String str:patents_number_4)
        {
            USPTOSearch searchPatent=new USPTOSearch(str);
            patent p=new patent(str,searchPatent.getAbs(),searchPatent.getClaims(),searchPatent.getDescription(),searchPatent.getTitle());
            p.setAuthor(author_4);
            patents.add(p);
        }
*/
        simpleKMeansPatent km2=new simpleKMeansPatent();
        km2.setClusterCount(3);
        km2.Cluster(patents);
        System.out.println(km2.toString());


    }

    public static void main(String[] args)
    {
        testMain m=new testMain();
        m.buildPatents();
      // new USPTOSearch("06393104");

    }

}
