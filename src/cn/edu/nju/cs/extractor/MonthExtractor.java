package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.nju.cs.paper.*;
import cn.edu.nju.cs.utility.ManagerIO;

public class MonthExtractor implements Extractor
{
    private static final int SUCCEED_FILE_EMPTY    = 1;
    private static final int SUCCEED_FILE_DONE     = 0;
    private static final int FAIL_CONNECTION_ERROR = -1;
    private String           month                 = null;
    private String           idFilePath            = null;
    private String           paperFilePath         = null;
    
    public MonthExtractor(String month)
    {
        this.month = month;
        this.idFilePath = ManagerIO.createMonthIdFilePath(this.month);
        this.paperFilePath = ManagerIO.createMonthPaperFilePath(this.month);
    }
    
    @Override
    public void extract() throws IOException
    {
        // Step1. extracts all paper IDs in that month, stored in a pre-defined file idFilePath.
        extractPaperIDs();
        // Step2. extracts all paper information in that month, by reading from the ID file
        // idFilePath and handle these IDs one by one. In case the procedure may fail,
        // we store the extracted papers into file paperFilePath.
        int exitCode = FAIL_CONNECTION_ERROR;
        while (FAIL_CONNECTION_ERROR == exitCode)
            exitCode = extractPapers();
        // The reason why I used a two-step method to extract all papers of the given month,
        // is that the connection is not stable, so I first store all paper ids in a temporary
        // file, even if the connection failed while processing a paper id, we can still
        // recover the procedure by backing up the remaining paper ids.
    }
    
    public String getPaperFilePath()
    {
        return paperFilePath;
    }
    
    // given a month 'xxxx', the page 'ARXIV_MIRROR_SITE/list/quant-ph/xxxx' stores papers
    // published in that month, but it doesn't show all papers in just one page.
    // This method will scan the page and find out how many papers
    // have been published in month xxxx, say 454, then create a new URL:
    // 'ARXIV_MIRROR_SITE/list/quant-ph/xxxx?show=454'
    // which contains all papers in just one page.
    // Then the method reads this page, extracts all paper IDs, stored them in the file.
    private void extractPaperIDs() throws IOException
    {
        // Step1. generate that month's arxiv new link URL
        String oldUrl = ManagerIO.createMonthArchiveURL(month);
        Document oldDoc = Utilities.newConnection(oldUrl);
        String newUrl = oldUrl + "?show=" + DocumentParser.parseArchiveNumberOfPapers(oldDoc);
        
        // Step2. using the URL, we read the page and extract that month's paper IDs
        Document newDoc = Utilities.newConnection(newUrl);
        ArrayList<String> archiveIds = DocumentParser.parseArchiveIDs(newDoc);
        // Step3. save the extracted ids.
        ManagerIO.savePaperIDs(archiveIds, idFilePath, true);
    }
    
    private int extractPapers() throws IOException
    {
        // Step1. load this month's arXiv ids from idFilePath into a
        // temporary storage array notExtractedArchiveIds
        ArrayList<String> notExtractedArchiveIds = new ArrayList<String>();
        ManagerIO.loadPaperIDs(notExtractedArchiveIds, idFilePath);
        if (notExtractedArchiveIds.isEmpty())
            return SUCCEED_FILE_EMPTY;
        
        // Step2. extract these not extracted arxiv ids one-by-one, store extracted papers
        // in an array list, after the method finished, stored them into file.
        ArrayList<Paper> papersList = new ArrayList<Paper>();
        while (!notExtractedArchiveIds.isEmpty())
        {
            String id = notExtractedArchiveIds.get(0);
            String url = ManagerIO.createArchivePaperURL(id);
            
            System.out.println("+++ processing " + url + " ...");
            
            Document doc = null;
            try
            {
                doc = Jsoup.connect(url).timeout(2000).get();
            }
            catch (IOException e)
            {
                ManagerIO.savePapers(papersList, paperFilePath, true);
                ManagerIO.savePaperIDs(notExtractedArchiveIds, idFilePath, false);
                return FAIL_CONNECTION_ERROR;
            }
            ;
            
            Paper paper = DocumentParser.parseArchivePaper(doc);
            papersList.add(paper);
            // For each extracted paper, remove its ID from the list notExtractedArxivIds
            notExtractedArchiveIds.remove(0);
            // Random void sleep, to sleep the program for several seconds.
            Utilities.randomAvoidSleep();
        }
        ManagerIO.savePapers(papersList, paperFilePath, true);
        ManagerIO.savePaperIDs(notExtractedArchiveIds, idFilePath, false);
        return SUCCEED_FILE_DONE;
    }
}
