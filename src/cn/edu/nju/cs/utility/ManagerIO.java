package cn.edu.nju.cs.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import cn.edu.nju.cs.paper.Paper;

/* PapersRecorder intents to record all papers' information to file. */
public class ManagerIO
{
    
    public static final String  ARXIV_CONFIG_FILE_PATH                  = "./arxiv-config.txt";
    public static final String  DEFAULT_ARXIV_MIRROR_SITE               = "http://arxiv.org";
    public static final String  DEFAULT_ARXIV_SUBJECT                   = "quant-ph";
    public static final String  DEFAULT_STORAGE_DIRECTORY               = "D:/workspaces/arxiv/";
    public static final String  KEYWORDS_STORAGE_DIRECTORY              = "keywords/";
    public static final int     MIRROR_SITE_VALUE_TYPE                  = 0;
    public static final int     SUBJECT_VALUE_TYPE                      = 1;
    public static final int     STORAGE_VALUE_TYPE                      = 2;
    private static final String PAPER_ID_LEADING_MARK                   = "id: ";
    private static final String PAPER_TITLE_LEADING_MARK                = "title: ";
    private static final String PAPER_AUTHORS_LEADING_MARK              = "authors: ";
    private static final String PAPER_COMMENTS_LEADING_MARK             = "comments: ";
    private static final String PAPER_JOURNAL_LEADING_MARK              = "journal: ";
    private static final String PAPER_SUBJECTS_LEADING_MARK             = "subjects: ";
    private static final String PAPER_URL_LEADING_MARK                  = "url: ";
    private static final String PAPER_ABSTRACT_LEADING_MARK             = "abstract: ";
    private static final int    PAPER_INFORMATION_OFFSET                = 2;
    
    private static final String ARCHIVE_PAPERS_FILE_DIRECTORY           = "archive-contents/";
    private static final String ARCHIVE_PAPERS_FILE_NAME_POSTFIX        = "-papers.txt";
    private static final String ARCHIVE_IDS_FILE_DIRECTORY              = "archive-contents/";
    private static final String ARCHIVE_IDS_FILE_NAME_POSTFIX           = "-ids.txt";
    
    private static final String NEW_SUBMISSION_PAPERS_FILE_DIRECTORY    = "new-submissions/";
    private static final String NEW_SUBMISSION_PAPERS_FILE_NAME_POSTFIX = "-papers.txt";
    
    public static void loadPapers(ArrayList<Paper> papers, String filePath)
            throws IOException
    {
        File file = new File(filePath);
        if (!file.exists())
        {
            System.out.println("PapersIO.loadPapers(): file open error, program crashed.");
            System.exit(-1);
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> paperInformation = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith(PAPER_ID_LEADING_MARK))
            {
                if (!paperInformation.isEmpty())
                    papers.add(parsePaperInformation(paperInformation));
                paperInformation.clear();
            }
            paperInformation.add(line);
        }
        if (!paperInformation.isEmpty())
            papers.add(parsePaperInformation(paperInformation));
        reader.close();
    }
    
    private static Paper parsePaperInformation(ArrayList<String> paperInformation)
    {
        Paper paper = new Paper();
        String id = null;
        String title = null;
        ArrayList<String> authors = null;
        String comments = null;
        String journal = null;
        String subjects = null;
        String url = null;
        String abs = null;
        
        Iterator<String> it = paperInformation.iterator();
        while (it.hasNext())
        {
            String info = it.next();
            if (info.startsWith(PAPER_ID_LEADING_MARK))
            {
                id = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperArXivId(id);
            }
            if (info.startsWith(PAPER_TITLE_LEADING_MARK))
            {
                title = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperTitle(title);
            }
            if (info.startsWith(PAPER_AUTHORS_LEADING_MARK))
            {
                String[] authorsList = info.substring(info.indexOf(':')
                        + PAPER_INFORMATION_OFFSET).split(",");
                authors = new ArrayList<String>();
                for (String str : authorsList)
                    authors.add(str.trim());
                paper.setPaperAuthors(authors);
            }
            if (info.startsWith(PAPER_COMMENTS_LEADING_MARK))
            {
                comments = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperComments(comments);
            }
            if (info.startsWith(PAPER_JOURNAL_LEADING_MARK))
            {
                journal = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperJournal(journal);
            }
            if (info.startsWith(PAPER_SUBJECTS_LEADING_MARK))
            {
                subjects = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperSubjects(subjects);
            }
            if (info.startsWith(PAPER_URL_LEADING_MARK))
            {
                url = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperURL(url);
            }
            if (info.startsWith(PAPER_ABSTRACT_LEADING_MARK))
            {
                abs = info.substring(info.indexOf(':') + PAPER_INFORMATION_OFFSET);
                paper.setPaperAbstract(abs);
            }
        }
        
        return paper;
    }
    
    public static void savePapers(ArrayList<Paper> papers, String filePath,
            boolean append) throws IOException
    {
        File file = new File(filePath);
        if (!file.exists())
            file.createNewFile();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        
        Iterator<Paper> it = papers.iterator();
        while (it.hasNext())
        {
            Paper paper = it.next();
            writer.write(PAPER_ID_LEADING_MARK);
            writer.write(paper.getPaperArXivId());
            writer.newLine();
            
            writer.write(PAPER_TITLE_LEADING_MARK);
            writer.write(paper.getPaperTitle());
            writer.newLine();
            
            writer.write(PAPER_AUTHORS_LEADING_MARK);
            writer.write(paper.getPaperAuthors());
            writer.newLine();
            
            writer.write(PAPER_COMMENTS_LEADING_MARK);
            String comments = paper.getPaperComments();
            if (null == comments)
                writer.write(" ");
            else
                writer.write(comments);
            writer.newLine();
            
            writer.write(PAPER_JOURNAL_LEADING_MARK);
            String journal = paper.getPaperJournal();
            if (null == journal)
                writer.write(" ");
            else
                writer.write(journal);
            writer.newLine();
            
            writer.write(PAPER_SUBJECTS_LEADING_MARK);
            String subject = paper.getPaperSubjects();
            if (null == subject)
                writer.write(" ");
            else
                writer.write(subject);
            writer.newLine();
            
            writer.write(PAPER_URL_LEADING_MARK);
            String url = paper.getPaperURL();
            if (null == url)
                writer.write(" ");
            else
                writer.write(url);
            writer.newLine();
            
            writer.write(PAPER_ABSTRACT_LEADING_MARK);
            String abs = paper.getPaperAbstract();
            if (null == abs)
                writer.write(" ");
            else
                writer.write(abs);
            writer.newLine();
            
            writer.newLine();
            writer.flush();
        }
        writer.flush();
        writer.close();
    }
    
    public static void savePapersInMarkdown(ArrayList<Paper> papers, String filePath,
            boolean append) throws IOException
    {
        File file = new File(filePath);
        if (!file.exists())
            file.createNewFile();
        String fileName = file.getName();
        fileName = new String("20" + fileName.substring(0, 4) + " Filtered arXiv Papers");
        // keep the markdown file in UTF-8 format
        BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        // For each markdown file, there is a front matter
        writer.write("---");
        writer.newLine();
        writer.write("layout: arxiv");
        writer.newLine();
        writer.write("title: " + fileName);
        writer.newLine();
        writer.write("---");
        writer.newLine();
        writer.newLine();
        // write papers' information
        Iterator<Paper> it = papers.iterator();
        int cnt = 0;
        while (it.hasNext())
        {
            Paper paper = it.next();
            cnt ++;
            writer.write("**" + cnt + ".    " + paper.getPaperTitle() + "**" + "  ");
            writer.newLine();
            
            writer.write(paper.getPaperAuthors() + "  ");
            writer.newLine();
            
            String journal = paper.getPaperJournal();
            if (null != journal && journal.length() > 2)
            {
                writer.write(journal + "  ");
                writer.newLine();
            }
            
            String url = paper.getPaperURL();
            if (null != url)
            {
                writer.write(url + "  ");
                writer.newLine();
            }
            
            String abs = paper.getPaperAbstract();
           // remove YAML tags
            abs = abs.replaceAll("\\{\\{", " ");
            abs = abs.replaceAll("\\{\\%", " ");
            if (null != abs)
            {
                writer.write("<blockquote>");
                writer.newLine();
                writer.write("<p>");
                writer.newLine();
                writer.write(abs);
                writer.newLine();
                writer.write("</p>");
                writer.newLine();
                writer.write("</blockquote>");
                writer.newLine();
            }
            
            writer.newLine();
            writer.write("------");
            writer.newLine();
            writer.newLine();
            
            writer.flush();
        }
        
        writer.flush();
        writer.close();
    } 
    
    public static void savePaperIDs(ArrayList<String> idList,
            String idFilePath, boolean append) throws IOException
    {
        File file = new File(idFilePath);
        if (!file.exists() || append == false)
            file.createNewFile();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        Iterator<String> it = idList.iterator();
        while (it.hasNext())
        {
            writer.write(it.next());
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
    
    public static void loadPaperIDs(ArrayList<String> idList, String idFilePath)
            throws IOException
    {
        File file = new File(idFilePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String arxivId = null;
        while ((arxivId = reader.readLine()) != null)
            idList.add(arxivId);
        
        reader.close();
    }
    
    public static void saveArxivConfigFile(String mirrorSite, String subject,
            String storageDir) throws IOException
    {
        File file = new File(ARXIV_CONFIG_FILE_PATH);
        // if configuration file exists, do nothing
        if (file.exists())
            return;
        else
            file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        writer.write("ARXIV MIRROR SITE: ");
        writer.write(mirrorSite);
        writer.newLine();
        writer.write("ARXIV SUBJECT: ");
        writer.write(subject);
        writer.newLine();
        writer.write("ARXIV STORAGE DIRECTORY: ");
        writer.write(storageDir);
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    private static String readArxivConfigFile(int valueType) throws IOException
    {
        File file = new File(ARXIV_CONFIG_FILE_PATH);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String mirrorSite = parseValue(reader.readLine());
        String subject = parseValue(reader.readLine());
        String storageDir = parseValue(reader.readLine());
        String value = null;
        if (MIRROR_SITE_VALUE_TYPE == valueType)
            value = mirrorSite;
        else if (SUBJECT_VALUE_TYPE == valueType)
            value = subject;
        else if (STORAGE_VALUE_TYPE == valueType)
            value = storageDir;
        else
            System.err.println("+++ readArxivConfigFile(): error value type!");
        
        reader.close();
        return value;
    }
    
    private static String getStorageDirectory()
    {
        String storageDir = null;
        try
        {
            storageDir = readArxivConfigFile(STORAGE_VALUE_TYPE);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return storageDir;
    }
    
    private static String getMirrorSite()
    {
        String mirrorSite = null;
        try
        {
            mirrorSite = readArxivConfigFile(MIRROR_SITE_VALUE_TYPE);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mirrorSite;
    }
    
    private static String getSubject()
    {
        String subject = null;
        try
        {
            subject = readArxivConfigFile(SUBJECT_VALUE_TYPE);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subject;
    }
    
    private static String parseValue(String line)
    {
        int index = line.indexOf(':');
        return line.substring(index + 1).trim();
    }
    
    public static String createNewSubmissionsURL()
    {
        String mirrorSite = getMirrorSite();
        String subject = getSubject();
        
        String tmp = mirrorSite + "/list/" + subject + "/new";
        System.out.println(tmp);
        
        return mirrorSite + "/list/" + subject + "/new";
    }
    
    public static String createMonthArchiveURL(String month) throws IOException
    {
        String mirrorSite = getMirrorSite();
        String subject = getSubject();
        return mirrorSite + "/list/" + subject + "/" + month;
    }
    
    public static String createArchivePaperURL(String paperID) throws IOException
    {
        String mirrorSite = getMirrorSite();
        return mirrorSite + "/abs/" + paperID;
    }
    
    public static String createMonthIdFilePath(String month)
    {
        return getStorageDirectory() + ARCHIVE_IDS_FILE_DIRECTORY
                + month + ARCHIVE_IDS_FILE_NAME_POSTFIX;
    }
    
    public static String createMonthPaperFilePath(String month)
    {
        return getStorageDirectory() + ARCHIVE_PAPERS_FILE_DIRECTORY
                + month + ARCHIVE_PAPERS_FILE_NAME_POSTFIX;
    }
    
    public static String createNewSubmissionPaperFilePath(String fileName)
    {
        return getStorageDirectory() + NEW_SUBMISSION_PAPERS_FILE_DIRECTORY
                + fileName + NEW_SUBMISSION_PAPERS_FILE_NAME_POSTFIX;
    }
    
    public static String createTitleKeywordsFilePath()
    {
        return getStorageDirectory() + KEYWORDS_STORAGE_DIRECTORY + "title-keywords.txt";
    }
    
    public static String createAuthorKeywordsFilePath()
    {
        return getStorageDirectory() + KEYWORDS_STORAGE_DIRECTORY + "author-keywords.txt";
    }
    
    public static String createAbstractKeywordsFilePath()
    {
        return getStorageDirectory() + KEYWORDS_STORAGE_DIRECTORY + "abstract-keywords.txt";
    }
}
