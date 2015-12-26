import org.apache.logging.log4j.Logger;

        import org.apache.logging.log4j.LogManager;

/**
 * Created by leisun on 15/10/7.
 */
public class test {
    private static Logger l= LogManager.getLogger("sadsd");


    public static void main(String[] args) {

        String last="a.b";
        System.out.println(last);
        last=last.replaceAll("\\."," ");
        System.out.println(last);
    }
}
