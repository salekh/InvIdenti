package Base;

import org.apache.mahout.math.matrix.DoubleMatrix2D;

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

    public patent(String patent_number,String abs,String claims,String description)
    {
        this.patent_number=patent_number;
        this.abs=abs;
        this.claims=claims;
        this.description=description;
    }

    public patent(String patent_number,String str)
    {
        this.patent_number=patent_number;
        this.description=str;
    }

    public void setTd(DoubleMatrix2D M)
    {
        td=M.copy();
    }

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
}
