package Base;

import org.apache.mahout.math.matrix.DoubleMatrix2D;

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
    private DoubleMatrix2D td;
    private String author;
    private String title;
    private String category;
    private String Assignee;

    public patent(String patent_number,String abs,String claims,String description,String title,String category,String Assignee)
    {
        this.patent_number=patent_number;
        this.abs=abs;
        this.claims=claims;
        this.description=description;
        this.title=title;
        this.category=category;
        this.Assignee=Assignee;
    }

    public patent(String patent_number,String str)
    {
        this.patent_number=patent_number;
        this.description=str;
    }

    public void setAuthor(String author)
    {
        this.author=author;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public void setTd(DoubleMatrix2D M)
    {
        td=M.copy();
    }

    public void setAssignee(String assignee){this.Assignee=assignee;}

    public void setCategory(String category){this.Assignee= category;}

    public DoubleMatrix2D getTd()
    {
        return td;
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

    public String getAssignee(){return this.Assignee;}

    public String getCategory(){return this.category;}

}
