package Refinement;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by leisun on 16/1/20.
 */
public class publicationSpider implements PageProcessor {

    String name;

    private Site site=Site.me().setRetryTimes(3).setRetryTimes(1000);

    public void setName(String name){
        this.name=name;
    }

    @Override
    public void process(Page page) {
        Document doc= Jsoup.parse(page.getHtml().toString());
        Elements e=doc.getElementsByTag("result");

        for(Element var0:e) {
           processOneResult(var0);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public boolean processOneResult(Element e) {
        String ID;
        String abstractText="";
        String affiliation="";
        String title;
        String authorID="";


        Elements var0=e.getElementsByTag("AuthorString");



        if (!var0.text().contains(this.name)) return false;

        Elements var1=e.getElementsByTag("ID");

        ID=var1.get(0).text();

        Elements var2=e.getElementsByTag("title");

        title=var2.get(0).text();




        Elements var3=e.getElementsByTag("affiliation");

        if (var3.size()>0) {       affiliation=var3.get(0).text();}

        Elements var4=e.getElementsByTag("author");

        for(Element var5:var4) {
            if (var5.text().equalsIgnoreCase(this.name)) {
                Elements var6=var5.getElementsByTag("authorID");
                if (var6.size()>0) {
                    authorID=var6.get(0).text();
                }
            }
        }


        Elements var7=e.getElementsByTag("abstractText");
        if (var7.size()>0) {
            abstractText=var7.get(0).text();
        }


        org.dom4j.Document document= DocumentHelper.createDocument();


        org.dom4j.Element rootElement = document.addElement("Publication");

        org.dom4j.Element E_ID=rootElement.addElement("ID");
        E_ID.setText(ID);

        org.dom4j.Element E_title=rootElement.addElement("title");
        E_title.setText(title);
if (affiliation.length()!=0) {
    org.dom4j.Element E_affiliation = rootElement.addElement("affiliation");
    E_affiliation.setText(affiliation);
}
        if (authorID.length()!=0) {
            org.dom4j.Element E_auth=rootElement.addElement("authorID");
            E_auth.setText(authorID);
        }


        if (abstractText.length()!=0) {
            org.dom4j.Element E_abs=rootElement.addElement("abstract");
            E_abs.setText(abstractText);
        }

        try {
            File file=new File("/Users/leisun/Desktop/ThesisData/ES/PBMED/Publication/"+this.name+"/"+ID+".xml");
            File dir=new File("/Users/leisun/Desktop/ThesisData/ES/PBMED/Publication/"+this.name+"/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileWriter fileWriter = new FileWriter(file);
            XMLWriter xmlWriter = new XMLWriter(fileWriter);
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }




        return true;
    }
}
