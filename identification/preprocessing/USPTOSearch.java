package preprocessing;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leisun on 15/8/20.
 * Search the patents from the USPTO
 */
public class USPTOSearch
{


    //Get abstract of the patent
   public static String getAbstractByNunmber(String number)
   {
        Document doc=getDocumentByNumber(number);
        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Abstract").first();
            if (e==null) return null; else return e.parent().nextElementSibling().ownText();
        }

   }

    //Get Claims of the patent
    public static String getClaimsByNunmber(String number)
    {
        Document doc=getDocumentByNumber(number);
        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Claims").first();

            while(e.nextSibling()==null)
                e=e.parent();


            if (e==null) return null; else
            {
                String str=getTextBetweenTwoTags(e,"hr","hr");
                return str;//e.parent().nextElementSibling().ownText();
            }

        }

    }

    //get description of the patent.
    public static String getDescriptionByNunmber(String number)
    {
        Document doc=getDocumentByNumber(number);
        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Description").first();
            while(e.nextSibling()==null)
                e=e.parent();


            if (e==null) return null; else
            {
                String str=getTextBetweenTwoTags(e,"hr","hr");
                return str;//e.parent().nextElementSibling().ownText();
            }

        }

    }

    //get abstract claims and description of the patent by using the patent number.

    public static List<String> getTextByNumber(String number)
    {
        Document doc=getDocumentByNumber(number);
        ArrayList<String> strs=new ArrayList<String>();
        //Document doc=getDocumentByNumber(number);
        strs.add(getAbstract(number, doc));
        strs.add(getClaims(number, doc));
        strs.add(getDescription(number, doc));

        return strs;

    }
    //Get abstract of the patent
    public static String getAbstract(String number,Document doc)
    {

        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Abstract").first();
            if (e==null) return null; else return e.parent().nextElementSibling().ownText();
        }

    }

    //Get Claims of the patent
    public static String getClaims(String number,Document doc)
    {
        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Claims").first();
            //String str=getTextBetweenTwoTags(e.parent().parent(),"hr","hr");


            while(e.nextSibling()==null)
                e=e.parent();


            if (e==null) return null; else
            {
                String str=getTextBetweenTwoTags(e,"hr","hr");
                return str;//e.parent().nextElementSibling().ownText();
            }

        }

    }

    //get description of the patent.
    public static String getDescription(String number,Document doc)
    {

        if (doc==null)
        {
            return null;
        }
        else
        {
            Element e=doc.getElementsMatchingOwnText("Description").first();

            while(e.nextSibling()==null)
                e=e.parent();


            if (e==null) return null; else
            {
                String str=getTextBetweenTwoTags(e,"hr","hr");
                return str;//e.parent().nextElementSibling().ownText();
            }

        }

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

    //Get document from patft by using the patent number.
    private static Document getDocumentByNumber(String number)
    {

        String base_url="http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&p=1&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=1&f=G&l=50&d=PALL&";
        String pat_number="S1="+number+"&OS="+number+"&RS="+number;
        String full_path=base_url+pat_number;

        //S1=04105099&OS=04105099&RS=04105099
        Document doc=null;

        try {
            doc=Jsoup.connect(full_path).get();
        } catch (IOException e)
        {
            System.out.println("Failed to access the document");
            //e.printStackTrace();
        }

        return doc;
    }





    public static void main(String[] args)
    {
       System.out.println(USPTOSearch.getClaimsByNunmber("D0405326"));
    }

}
