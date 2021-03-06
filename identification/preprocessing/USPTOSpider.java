package preprocessing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by sunlei on 15/9/1.
 */
public class USPTOSpider implements PageProcessor
{
    private String abs;//Get the abstract of the patent

    private String claims;//Get the claims of the patent

    private String description;//Get the description of the patent

    private String head;

    private String title;

    private String NPRS;

    private Site site=Site.me().setRetryTimes(3).setRetryTimes(1000);

    public void setCharset(String code)
    {
        this.site.setCharset(code);
    }

    public void process(Page page)
    {

        Document doc= Jsoup.parse(page.getHtml().toString());

        Elements e=doc.getElementsByTag("Title");

        if(e==null||e.size()==0) this.head=null; else this.head=e.first().toString();

        e=doc.getElementsMatchingOwnText("Abstract");

        if (e==null||e.size()==0) {
            abs=null;
            this.title=null;
        } else {
            this.abs=e.first().parent().nextElementSibling().ownText();
            String str=e.first().parent().previousElementSibling().previousElementSibling().previousElementSibling().ownText();

            this.title=str;
        }

        e=doc.getElementsContainingOwnText("Claims");


        if (e==null||e.size()==0) claims=null; else
        {
            int claims_number=0;
            for(Element temp:e) {
                if (temp.ownText().equalsIgnoreCase("Claims")) break;
                claims_number++;
            }
            if (claims_number==e.size()) this.claims=null; else {
                Element current = e.get(claims_number);
                while(current.nextSibling()==null) current=current.parent();
                this.claims = getTextBetweenTwoTags(current.parent(),"hr","hr");
            }

        }
        e=doc.getElementsMatchingOwnText("Description");
        if (e==null||e.size()==0) claims=null; else
        { int description_number=0;
            for(Element temp:e)
            {
                if (temp.ownText().equalsIgnoreCase("Description")) break;
                description_number++;
            }
            if (description_number==e.size()) this.claims=null; else {
                Element current = e.get(description_number);
                while(current.nextSibling()==null) current=current.parent();
                this.description = getTextBetweenTwoTags(current.parent(),"hr","hr");
            }
        }

        e=doc.getElementsMatchingOwnText("Other Reference");

      if (e!=null&&e.size()>0) {
         NPRS=getAllNPRs(e.get(0).parent().nextElementSibling());


      } else
      {
          NPRS=" ";
      }



    }


    public String getNPRS(){
        return this.NPRS;
    }

    private static String getAllNPRs(Element e){


        while(e.nodeName()!="br") {
            e=e.child(0);
        }

        String str="";
        while(e.nextElementSibling()!=null) {
            String var0=((TextNode)e.nextSibling()).text();
            if (var0.contains("et al., ")) {
                int start=var0.indexOf("et al., ");
                str+=getANPR(start,var0)+"\n";
            }
            e=e.nextElementSibling();
        }
        return str;
    }


    private static String getANPR(int start,String str) {
        int start_n=start;
        while(str.charAt(start_n)!=',') {
            start_n++;
        }
        start_n+=2;
       // String temp = "";
       /*
        if (str.charAt(start_n)=='"') {
           start_n++;

           while (start_n < str.length() && str.charAt(start_n) != '"') {
               temp += str.charAt(start_n);
               start_n++;
           }
       } else {
           while (start_n < str.length() && (str.charAt(start_n)!=','&&str.charAt(start_n)!='.')) {
               temp += str.charAt(start_n);
               start_n++;
           }
           System.out.println("as "+temp);
       }*/


        return str.substring(start_n,str.length()-1);
    }

    //Get texts between start tag and end tag.
    private static String getTextBetweenTwoTags(Element e,String start,String end)
    {
        String str="";

        Node current=e.nextSibling();

        while(!current.nodeName().equalsIgnoreCase(start))
        {
            current=current.nextSibling();
        }

        current=current.nextSibling();

        while(!current.nodeName().equalsIgnoreCase(end))
        {
            if (current instanceof TextNode) str+=((TextNode) current).text();
            current=current.nextSibling();
        }

        return str;
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

    public Site getSite()
    {
        return site;
    }

    public String getHead(){return head;}
}
