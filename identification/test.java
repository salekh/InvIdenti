import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Created by leisun on 15/10/7.
 */
public class test {
    private static Logger l= Logger.getLogger(test.class);

    static {
        DOMConfigurator.configure("log4j.xml");
    }

    public static void main(String[] args) {

        l.info("Hallo World");
    }
}
/**
 * log4j.rootLogger=debug,stdout

 ##Console output configuration##
 log4j.appender.stdout = org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target = System.out
 log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern = %m%n
 **/