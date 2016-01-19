package base;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by leisun on 16/1/19.
 */
public class textOperator {
    /**
     * Store the text into a text file
     * @param path text path
     * @param follow rewrite option
     * @param str content to write
     */

    public static void storeText(String path,boolean follow,String str){
        if (follow) {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
