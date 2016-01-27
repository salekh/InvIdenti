package Refinement;

import preprocessing.USPTOSpider;
import us.codecraft.webmagic.Spider;

/**
 * Created by leisun on 16/1/20.
 */
public class publicationSearch {

    private String name;

    String base_url;

    publicationSearch(String name) {
        this.name = name;
        String[] var0=this.name.split(" ");
        base_url="http://www.ebi.ac.uk/europepmc/webservices/rest/search?query=auth:%22"+var0[0]+"%20"+var0[1]+"%22&resulttype=core&pageSize=1000";
        extractionFromTheAPI();
    }

    public void extractionFromTheAPI(){
        publicationSpider g=new publicationSpider();
        g.setName(this.name);
        Spider.create(g).addUrl(base_url).thread(1).run();

    }

    public static void main(String[] args) {
       // publicationSearch p=new publicationSearch("JOHNSON TC");
       // publicationSearch p1=new publicationSearch("WEIS JK");
       // publicationSearch p2=new publicationSearch("APGAR ME");
        publicationSearch p3=new publicationSearch("István R");
    }


}
