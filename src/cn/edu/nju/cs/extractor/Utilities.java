// /////////////////////////////////////////////////////////////////////////////
// @author
// ------ Wang Kun
// ------ Email: nju.wangkun@gmail.com
// @date Aug 17, 2015 11:01:10 AM
// @file arxiv-manager/cn.edu.nju.cs/Utilities.java
// @brief
// /////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Utilities
{
    
    public static Document newConnection(String url) throws IOException
    {
        Document doc = null;
        try
        {
            doc = Jsoup.connect(url).timeout(5000).get();
        }
        catch (IOException e)
        {
            System.err
                    .println("newConnection(): connecting error, program terminated,"
                            + " please run it again...");
            System.exit(-1);
        }
        ;
        return doc;
    }
    
    // randomAvoidSleep() generates a random number x in [0, 20].
    // This number is used to sleep current process for x*1000 milliseconds.
    public static void randomAvoidSleep()
    {
        Random random = new Random();
        try
        {
            Thread.sleep(random.nextInt(20) * 1000);
        }
        catch (InterruptedException e)
        {
            System.out.println("Sleeping failed...");
            e.printStackTrace();
        }
    }
}
