package base;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * Created by leisun on 15/10/1.
 */
public class IniFileReader {
    /**
     * @param iniPath initial file path
     */
    private String iniPath="invidenti.ini";

    /**
     * Set the initial file path
     * @param path initial file path
     */
    public void setPath (String path) {
        this.iniPath=path;
    }

    /**
     *
     * @return the initial file path
     */
    public String getIniPath () {
        return this.iniPath;
    }

    /**
     *
     * @param str represent the section and the option example "abc:def" Section abc and Attribute def
     * @return the value of the option from the initial file
     */
    public String getValue (String str)  {

        String[] strs=str.split(":");

        if (strs.length!=2) {
            return "Wrong Format Query";
        }

        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));
            return initalFile.get(strs[0],strs[1]);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }


    }


}
