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

import cn.edu.nju.cs.paper.PaperIO;

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
                    .println("newConnection(): connecting error, cannot download the page,"
                            + " please run the program again...");
            return doc;
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
    
    public static String createNewSubmissionsURL() throws IOException
    {
        String mirrorSite = PaperIO.readArxivConfigFile(PaperIO.MIRROR_SITE_VALUE_TYPE);
        String subject = PaperIO.readArxivConfigFile(PaperIO.SUBJECT_VALUE_TYPE);
        return mirrorSite + "/list/" + subject + "/new";
    }

    public static String createArchiveURLByMonth(String month) throws IOException
    {
        String mirrorSite = PaperIO.readArxivConfigFile(PaperIO.MIRROR_SITE_VALUE_TYPE);
        String subject = PaperIO.readArxivConfigFile(PaperIO.SUBJECT_VALUE_TYPE);
        return mirrorSite + "/list/" + subject + "/" + month;
    }
    
    public static String createArchivePaperURL(String paperID) throws IOException
    {
        String mirrorSite = PaperIO.readArxivConfigFile(PaperIO.MIRROR_SITE_VALUE_TYPE);
        return mirrorSite + "/abs/" + paperID;
    }
}
