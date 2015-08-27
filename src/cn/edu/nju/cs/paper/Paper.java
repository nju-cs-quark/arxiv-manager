/******************************************************************************
 * @author Wang Kun
 *         ID: DG1333031
 *         Email: nju.wangkun@gmail.com
 *         Date: 2015��3��28��
 * @file Papers.java
 * @brief Papers class is used to record the basic information of a paper:
 *        + paper arXiv ID
 *        + paper name
 *        + paper authors (may be more than one)
 *        + paper abstract
 ******************************************************************************/
package cn.edu.nju.cs.paper;

import java.util.ArrayList;
import java.util.Iterator;

public class Paper
{
    private String            paperArXivId  = null;
    private String            paperTitle    = null;
    private ArrayList<String> paperAuthors  = null;
    private String            paperComments = null;
    private String            paperJournal  = null;
    private String            paperSubjects = null;
    private StringBuffer      paperAbstract = null;
    private String            paperURL      = null;
    
    public Paper()
    {
        this.paperArXivId = new String();
        this.paperTitle = new String();
        this.paperAuthors = new ArrayList<String>();
        this.paperComments = new String();
        this.paperJournal = new String();
        this.paperSubjects = new String();
        this.paperAbstract = new StringBuffer();
        this.paperURL = new String();
    }
    
    public String getPaperArXivId()
    {
        return paperArXivId;
    }
    
    public void setPaperArXivId(String paperArXivId)
    {
        this.paperArXivId = paperArXivId;
    }
    
    public String getPaperTitle()
    {
        return paperTitle;
    }
    
    public void setPaperTitle(String paperTitle)
    {
        this.paperTitle = paperTitle;
    }
    
    public String getPaperAuthors()
    {
        Iterator<String> itAuthors = this.paperAuthors.iterator();
        StringBuffer buffer = new StringBuffer();
        while (itAuthors.hasNext())
        {
            String author = itAuthors.next().toString();
            buffer.append(author);
            if (itAuthors.hasNext())
                buffer.append(", ");
        }
        
        return buffer.toString();
    }
    
    public void setPaperAuthors(ArrayList<String> paperAuthors)
    {
        this.paperAuthors = paperAuthors;
    }
    
    public void addPaperAuthor(String paperAuthor)
    {
        this.paperAuthors.add(paperAuthor);
    }
    
    public String getPaperAbstract()
    {
        if (null == paperAbstract)
            return null;
        else
            return paperAbstract.toString();
    }
    
    public void setPaperAbstract(StringBuffer paperAbstract)
    {
        this.paperAbstract = paperAbstract;
    }
    
    public void setPaperAbstract(String paperAbstractString)
    {
        this.paperAbstract = new StringBuffer(paperAbstractString);
    }
    
    public String getPaperComments()
    {
        if (null == paperComments)
            return "";
        else
            return paperComments;
    }
    
    public void setPaperComments(String paperComments)
    {
        this.paperComments = paperComments;
    }
    
    public String getPaperSubjects()
    {
        if (null == paperSubjects)
            return "";
        else
            return paperSubjects;
    }
    
    public void setPaperSubjects(String paperSubjects)
    {
        this.paperSubjects = paperSubjects;
    }
    
    public String getPaperJournal()
    {
        if (null == paperJournal)
            return "";
        else
            return paperJournal;
    }
    
    public void setPaperJournal(String paperJournal)
    {
        this.paperJournal = paperJournal;
    }
    
    public String getPaperURL()
    {
        return paperURL;
    }
    
    public void setPaperURL(String paperURL)
    {
        this.paperURL = paperURL;
    }
    
}
