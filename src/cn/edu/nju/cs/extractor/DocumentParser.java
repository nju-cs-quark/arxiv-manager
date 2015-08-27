// /////////////////////////////////////////////////////////////////////////////
// @author
// ------ Wang Kun
// ------ Email: nju.wangkun@gmail.com
// @date Aug 16, 2015 11:13:06 AM
// @file arxiv-extractor/cn.edu.nju.cs/PaperExtractor.java
// @brief PaperExtractor extracts a paper's detail information from a given
// Document structure.
// /////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.nju.cs.paper.Paper;

public class DocumentParser
{
    private static final String TITLE_KEYWORD             = "Title";
    private static final String AUTHORS_KEYWORD           = "Authors";
    private static final String COMMENTS_KEYWORD          = "Comments";
    private static final String SUBJECTS_KEYWORD          = "Subjects";
    private static final String JOURNAL_KEYWORD           = "Journal-ref";
    private static final String PAPER_ABSTRACT_URL_PREFIX = "http://www.arxiv.org/abs/";
    
    public DocumentParser()
    {
    }
    
    public static String parseArchiveNumberOfPapers(Document doc) throws IOException
    {
        Elements list = doc.getElementsByTag("small");
        String content = list.get(0).text();    // "[ total of 454 entries: 1-454 ]"
        String[] words = content.split(" ");
        return words[3];
    }
    
    public static ArrayList<String> parseArchiveIDs(Document doc) throws IOException
    {
        ArrayList<String> archiveIds = new ArrayList<String>();
        Elements dts = doc.getElementsByTag("dt");
        for (Element dt : dts)
        {
            Elements linkElements = dt.getElementsByTag("a");
            Element arxivIdElement = linkElements.get(1);
            String idString = arxivIdElement.text().trim();
            int index = idString.indexOf(':');
            idString = idString.substring(index + 1, idString.length());
            archiveIds.add(idString);
        }
        return archiveIds;
    }
    
    public static Paper parseArchivePaper(Document doc) throws IOException
    {
        Paper paper = new Paper();
        Elements lists = null;
        String id = null;
        // paper id
        lists = doc.getElementsByTag("title");
        id = parseIdFromElement(lists.first());
        paper.setPaperArXivId(id);
        // paper url
        paper.setPaperURL(PAPER_ABSTRACT_URL_PREFIX + id);
        // paper title
        lists = doc.select("h1.title.mathjax");
        paper.setPaperTitle(parseTitleFromElement(lists.first()));
        // paper authors
        lists = doc.select("div.authors");
        paper.setPaperAuthors(parseAuthorsFromElement(lists.first()));
        // paper comments
        lists = doc.select("td.tablecell.comments");
        if (lists.isEmpty())
            paper.setPaperComments(null);
        else
            paper.setPaperComments(parseCommentsFromElement(lists.first()));
        // paper journals
        lists = doc.select("td.tablecell.jref");
        if (lists.isEmpty())
            paper.setPaperJournal(null);
        else
            paper.setPaperJournal(parseJournalFromElement(lists.first()));
        // paper subjects
        lists = doc.select("td.tablecell.subjects");
        if (lists.isEmpty())
            paper.setPaperSubjects(null);
        else
            paper.setPaperSubjects(parseCommentsFromElement(lists.first()));
        // paper abstract
        lists = doc.select("blockquote.abstract.mathjax");
        if (lists.isEmpty())
            paper.setPaperAbstract(new String(" "));
        else
            paper.setPaperAbstract(parseAbstractFromElement(lists.first()));
        
        return paper;
    }
    
    public static String parseNewSubmissionDate(Document doc) throws IOException
    {
        String date = doc.getElementsByClass("list-dateline").text();
        String pattern = "announced";
        int index = date.indexOf(pattern);
        String[] dateInfo = date.substring(index + pattern.length(), date.length()).trim()
                .split(" ");
        // dateInfo[0] is week
        String day = dateInfo[1];
        String month = dateInfo[2];
        String year = dateInfo[3];
        
        StringBuffer fileName = new StringBuffer();
        switch (month)
        {
        case "Jan":
            month = "01";
            break;
        case "Feb":
            month = "02";
            break;
        case "Mar":
            month = "03";
            break;
        case "Apr":
            month = "04";
            break;
        case "May":
            month = "05";
            break;
        case "Jun":
            month = "06";
            break;
        case "Jul":
            month = "07";
            break;
        case "Aug":
            month = "08";
            break;
        case "Sep":
            month = "09";
            break;
        case "Oct":
            month = "10";
            break;
        case "Nov":
            month = "11";
            break;
        case "Dec":
            month = "12";
            break;
        default:
            System.err.println("getFileName(): undefined month.");
            break;
        }
        
        int dayValue = Integer.parseInt(day);
        if (dayValue < 10)
            day = "0" + day;
        
        fileName.append("20" + year);
        fileName.append(month);
        fileName.append(day);
        
        return fileName.toString();
    }
    
    public static ArrayList<Paper> parseNewSubmissionPapers(Document doc) throws IOException
    {
        ArrayList<Paper> papers = new ArrayList<Paper>();
        // According to arXiv, the doc contains three lists, they are
        // 1. 'New submissions' list
        // 2. 'Cross-lists' list
        // 3. 'Replacements' list
        // Some of them may be empty. This should be taken into consideration.
        Elements lists = doc.getElementsByTag("dl");
        
        for (Element list : lists)
        {
            // For each list, their structure is designed as:
            // 1. dt: records the paper's arXiv id
            // 2. dd: records the paper's other informations:
            // title, authors, comments, journal-references, subjects, abstract.
            // Some of these domains may be missing.
            // As there are many papers in one list, we must extract them one by one.
            Elements paperIds = list.getElementsByTag("dt");
            Elements paperInfos = list.getElementsByTag("dd");
            // For each paper, there must be a paper id and its information.
            assert (paperIds.size() == paperInfos.size());
            int numOfPapers = paperIds.size();
            for (int i = 0; i < numOfPapers; i++)
            {
                Element paperId = paperIds.get(i);
                Element paperInfo = paperInfos.get(i);
                
                Paper paper = new Paper();
                
                setPaperArXivIdAndURL(paper, paperId);
                setPaperInfo(paper, paperInfo);
                
                papers.add(paper);
            }
        }
        
        return papers;
    }
    
    // A 'dt' list for paper's arXiv id has the form like:
    // <dt>
    // <a name="item1">[1]</a>&nbsp;
    // <span class="list-identifier"><a href="/abs/1503.07517" title="Abstract">arXiv:1503.07517</a>
    // [<a href="/pdf/1503.07517" title="Download PDF">pdf</a>,
    // <a href="/ps/1503.07517" title="Download PostScript">ps</a>,
    // <a href="/format/1503.07517" title="Other formats">other</a>]</span>
    // </dt>
    // So we can first get all elements by tag 'a', then fetch the second one.
    private static void setPaperArXivIdAndURL(Paper paper, Element paperId)
    {
        Elements links = paperId.getElementsByTag("a");
        String arXivId = links.get(1).text();
        int beginIndex = arXivId.indexOf(':');
        int endIndex = arXivId.length();
        arXivId = arXivId.substring(beginIndex + 1, endIndex);
        paper.setPaperArXivId(arXivId);
        
        String url = PAPER_ABSTRACT_URL_PREFIX + arXivId;
        paper.setPaperURL(url);
    }
    
    private static void setPaperInfo(Paper paper, Element paperInfo)
    {
        Element level1 = paperInfo.children().get(0);
        Elements level2 = level1.children();
        int endIndex = 0;
        // If there is 'p' tag in level 1, then it marks the abstract part,
        // otherwise there is no abstract for this paper.
        Elements pTag = level1.getElementsByTag("p");
        // there is no abstract part, so elements in level 2 are title,
        // authors, comments, journal-refs and subjects
        if (0 == pTag.size())
            endIndex = level2.size();
        else
        {
            // Extract paper abstract
            endIndex = level2.size() - 1;
            Element abstractElement = level2.get(endIndex);
            assert (abstractElement.tagName() == "p");
            paper.setPaperAbstract(new StringBuffer(abstractElement.text()
                    .trim()));
        }
        for (int i = 0; i < endIndex; i++)
        {
            Element child = level2.get(i);
            String key = getKeywords(child.text()).trim();
            String value = removeLeadingTags(child.text()).trim();
            switch (key)
            {
            case TITLE_KEYWORD:        // Extract paper title
                paper.setPaperTitle(value);
                break;
            case AUTHORS_KEYWORD:       // Extract paper authors
                Elements authors = child.getElementsByTag("a");
                for (Element author : authors)
                {
                    String authorString = author.text();
                    paper.addPaperAuthor(authorString);
                }
                break;
            case COMMENTS_KEYWORD:      // Extract paper comments
                paper.setPaperComments(value);
                break;
            case JOURNAL_KEYWORD:      // Extract paper journals
                paper.setPaperJournal(value);
                break;
            case SUBJECTS_KEYWORD:      // Extract paper subjects
                paper.setPaperSubjects(value);
                break;
            default:
                System.err.println("setPaperInfo(): undefined keywords.");
                break;
            }
        }
    }
    
    // For 'Title', 'Author', 'Comments', 'Journal-ref' and 'Subject' strings, there are leading
    // 'XXX: ' in the string, we can identify it out to know which part of the paper it belongs.
    private static String getKeywords(String string)
    {
        int index = string.indexOf(':');
        return string.substring(0, index);
    }
    
    // For 'Title', 'Author', 'Comments', 'Journal-ref' and 'Subject' strings, there are leading
    // 'XXX: ' in the string, it must be removed to keep elegance.
    private static String removeLeadingTags(String string)
    {
        int index = string.indexOf(':');
        String newString = null;
        if (-1 == index)
            newString = string.trim();
        else
            newString = string.substring(index + 1).trim();
        return newString;
    }
    
    /* The idElement has a form like
     * <title>[quant-ph/0405001] Is Quantum Search Practical?</title>
     * We have to pick out the 'quant-ph/0405001' part. */
    private static String parseIdFromElement(Element idElement)
    {
        String id = idElement.text();
        int beginIndex = id.indexOf('[');
        int endIndex = id.indexOf(']');
        return id.substring(beginIndex + 1, endIndex);
    }
    
    /* The titleElement has a form like
     * <h1 class="title mathjax"><span class="descriptor">Title:</span>
     * Is Quantum Search Practical?</h1>
     * We must pick out the 'Is Quantum Search Practical?' part */
    private static String parseTitleFromElement(Element titleElement)
    {
        String title = titleElement.text();
        int beginIndex = title.indexOf(':');
        int endIndex = title.length();
        return title.substring(beginIndex + 1, endIndex).trim();
    }
    
    /* The authorsElement has a rather complicated form
     * <div class="authors"><span class="descriptor">Authors:</span>
     * ** <a href="/find/quant-ph/1/au:+Viamontes_G/0/1/0/all/0/1">George F. Viamontes</a>,
     * ** <a href="/find/quant-ph/1/au:+Markov_I/0/1/0/all/0/1">Igor L. Markov</a>,
     * ** <a href="/find/quant-ph/1/au:+Hayes_J/0/1/0/all/0/1">John P. Hayes</a>
     * </div>
     * We must pick all authors out. */
    private static ArrayList<String> parseAuthorsFromElement(
            Element authorsElement)
    {
        ArrayList<String> authorsList = new ArrayList<String>();
        Elements authors = authorsElement.getElementsByTag("a");
        for (Element author : authors)
        {
            String authorString = author.text().trim();
            authorsList.add(authorString);
        }
        return authorsList;
    }
    
    private static String parseCommentsFromElement(Element commentElement)
    {
        return commentElement.text().trim();
    }
    
    private static String parseJournalFromElement(Element journalElement)
    {
        return journalElement.text().trim();
    }
    
    private static StringBuffer parseAbstractFromElement(Element abstractElement)
    {
        String abs = abstractElement.text();
        int beginIndex = abs.indexOf(':');
        int endIndex = abs.length();
        return new StringBuffer(abs.substring(beginIndex + 1, endIndex).trim());
    }
}
