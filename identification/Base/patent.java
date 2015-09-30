package base;

import org.apache.mahout.math.matrix.DoubleMatrix2D;
import preprocessing.USPTOSearch;

import java.util.Locale;

/**
 * Created by sunlei on 15/9/6.
 */
public class patent
{
    private String patent_number;
    private String abs;
    private String claims;
    private String description;
    /**Text vector representation of the patent**/
    private DoubleMatrix2D td;
    private DoubleMatrix2D td_abs;
    private DoubleMatrix2D td_claims;
    private DoubleMatrix2D td_des;
    /**Attributes of the patent**/
    private String author;
    private String title;
    private String category;
    private String assignee;

    public patent(String patent_number,String abs,String claims,String description,String title,String category,String Assignee,String name)
    {
        this.patent_number=patent_number;
        this.abs=abs;
        this.claims=claims;
        this.description=description;
        this.title=title;
        this.category=category;
        this.assignee=Assignee;
        this.author=name;
    }

   public patent(String patent_number,String category,String assignee,String author){
       this.patent_number=patent_number;
       this.category=category;
       this.assignee=assignee;
       this.author=author;

       USPTOSearch searchPatent=new USPTOSearch(patent_number);
       this.setAbs(searchPatent.getAbs());
       this.setClaims(searchPatent.getClaims());
       this.setDescription(searchPatent.getDescription());
       this.setTitle(searchPatent.getTitle());

   }


    /**Set patent attributes value**/
    public void setAuthor(String author)
    {
        this.author=author;
    }

    public void setTd(DoubleMatrix2D M)
    {
        td=M.copy();
    }

    public void setTd_abs(DoubleMatrix2D M)
    {
        this.td_abs=M.copy();
    }

    public void setTd_des(DoubleMatrix2D M)
    {
        this.td_des=M.copy();
    }

    public void setTd_claims(DoubleMatrix2D M)
    {
        this.td_claims=M.copy();
    }

    public void setAssignee(String assignee){this.assignee=assignee;}

    public void setCategory(String category){this.assignee= category;}

    public void setAbs(String abs) {
        this.abs=abs;
    }

    public void setClaims(String claims){
        this.claims=claims;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public void setTitle(String title){
        this.title=title;
    }

    /**Get the attribute value of the patent**/

    public String getAuthor()
    {
        return this.author;
    }

    public DoubleMatrix2D getTd()
    {
        return td;
    }

    public DoubleMatrix2D getTd_abs()
    {
        return td_abs;
    }

    public DoubleMatrix2D getTd_des()
    {
        return td_des;
    }

    public DoubleMatrix2D getTd_claims()
    {
        return td_claims;
    }

    public String getPatent_number()
    {
        return this.patent_number;
    }

    public String getAbs()
    {
        return this.abs;
    }

    public String getClaims()
    {
        return this.claims;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getTitle() {return this.title;}

    public String getAssignee(){return this.assignee;}

    public String getCategory(){return this.category;}

}
