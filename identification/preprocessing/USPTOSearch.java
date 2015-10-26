package preprocessing;
import us.codecraft.webmagic.Spider;


/**
 * Created by leisun on 15/8/20.
 * Search the patents from the USPTO
 */
public class USPTOSearch
{

   public String patetnt_number;

    private String abs;//Get the abstract of the patent

    private String claims;//Get the claims of the patent

    private String description;//Get the description of the patent

    private String title;


    public USPTOSearch(String patent_number) {
        this.patetnt_number = patent_number;
        String patent_number_c;
        int num = 0;

        for (int i = 0; i < this.patetnt_number.length(); i++) {
            if (this.patetnt_number.charAt(i) != '0') num = i;
        }


        USPTOSpider g = new USPTOSpider();

        patent_number_c = patent_number.substring(num, patent_number.length());


        String base_url = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&p=1&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=1&f=G&l=50&d=PALL&";

        String pat_number = "S1=" + this.patetnt_number + "&OS=" + this.patetnt_number + "&RS=" + this.patetnt_number;
        String full_path = base_url + pat_number;

            try {
                Spider.create(g).addUrl(full_path).thread(1).run();
                if (g.getHead().contains(patent_number_c)) {
                    abs = g.getAbs();
                    claims = g.getClaims();
                    description = g.getDescription();
                    title = g.getTitle();
                    return;
                }
            } catch (Exception e)
            {

            }

            if (patent_number.length()<7) {

                int var0=(7-patent_number.length());

                while (var0>0) {

                    patent_number="0"+patent_number;
                    var0--;
                }

            }

            base_url = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm&r=1&f=G&l=50&";
            pat_number = "s1=" + patent_number + ".PN.&OS=PN/" + patent_number + "&RS=PN/" + patent_number;

            try {
                Spider.create(g).addUrl(base_url + pat_number).thread(1).run();
                if (g.getHead().contains(patent_number_c)) {
                    abs = g.getAbs();
                    claims = g.getClaims();
                    description = g.getDescription();
                    title = g.getTitle();
                } else {
                    System.out.println("failed to find the patent,please check the patent number:" + this.patetnt_number);
                    this.abs = null;
                    this.claims = null;
                    this.description = null;
                    this.title=null;
                }
                } catch (Exception e)
                {
                    this.abs = null;
                    this.claims = null;
                    this.description = null;
                    this.title=null;
                }



    }




    public String getAbs()
    {
        return abs;
    }

    public String getClaims()
    {
        return claims;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTitle(){return title;}



}
