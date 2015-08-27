package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;

import cn.edu.nju.cs.paper.*;
import cn.edu.nju.cs.utility.ManagerIO;

public class NewSubmissionExtractor implements Extractor
{
    
    private Document doc           = null;
    private String   paperFilePath = null;
    
    public NewSubmissionExtractor() throws IOException
    {
        String url = ManagerIO.createNewSubmissionsURL();
        doc = Utilities.newConnection(url);
        
        String newSubmissionFileName = DocumentParser.parseNewSubmissionDate(this.doc);
        paperFilePath = ManagerIO.createNewSubmissionPaperFilePath(newSubmissionFileName);
    }
    
    @Override
    public void extract() throws IOException
    {
        ArrayList<Paper> newSubmissionPapers = DocumentParser.parseNewSubmissionPapers(this.doc);
        ManagerIO.savePapers(newSubmissionPapers, paperFilePath, false);
    }
    
    public String getPaperFilePath()
    {
        return paperFilePath;
    }
}
