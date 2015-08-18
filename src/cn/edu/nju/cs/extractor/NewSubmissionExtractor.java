package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;

import cn.edu.nju.cs.paper.*;

public class NewSubmissionExtractor implements Extractor
{
    private static final String NEW_SUBMISSION_PAPERS_FILE_PATH_PREFIX = "D:/workspaces/arxiv/new-submissions/";
    private static final String NEW_SUBMISSION_PAPERS_FILE_PATH_POSFIX = "-papers.txt";
    
    private Document            doc                                    = null;
    private String              paperFilePath                          = null;
    
    public NewSubmissionExtractor() throws IOException
    {
        String url = Utilities.createNewSubmissionsURL();
        doc = Utilities.newConnection(url);
        
        String newSubmissionFileName = DocumentParser.parseNewSubmissionDate(this.doc);
        paperFilePath = NEW_SUBMISSION_PAPERS_FILE_PATH_PREFIX
                + newSubmissionFileName + NEW_SUBMISSION_PAPERS_FILE_PATH_POSFIX;
    }
    
    @Override
    public void extract() throws IOException
    {
        ArrayList<Paper> newSubmissionPapers = DocumentParser.parseNewSubmissionPapers(this.doc);
        PaperIO.savePapers(newSubmissionPapers, paperFilePath, false);
    }
}
