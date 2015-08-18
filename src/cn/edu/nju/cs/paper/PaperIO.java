package cn.edu.nju.cs.paper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import cn.edu.nju.cs.paper.Paper;

/* PapersRecorder intents to record all papers' information to file. */
public class PaperIO
{
    public static final String ARXIV_CONFIG_FILE_PATH    = "./arxiv-config.txt";
    public static final String DEFAULT_ARXIV_MIRROR_SITE = "http://arxiv.org";
    public static final String DEFAULT_ARXIV_SUBJECT     = "quant-ph";
    public static final int    MIRROR_SITE_VALUE_TYPE    = 0;
    public static final int    SUBJECT_VALUE_TYPE        = 1;
    
    // public static void loadPapers(ArrayList<Paper> papers, String filePath)
    // throws IOException
    // {
    // File file = new File(filePath);
    // if (!file.exists())
    // {
    // System.out
    // .println("PapersIO.loadPapers(): file open error, program crashed.");
    // System.exit(-1);
    // }
    //
    // BufferedReader reader = new BufferedReader(new FileReader(file));
    // String id = null;
    // String title = null;
    // ArrayList<String> authors = null;
    // String comments = null;
    // String journal = null;
    // String subjects = null;
    // String url = null;
    // StringBuffer abs = null;
    //
    // while ((id = reader.readLine()) != null)
    // {
    // id = getStringAfterColon(id);
    // title = reader.readLine();
    // title = getStringAfterColon(title);
    // authors = getAuthorsFromString(reader.readLine());
    //
    // }
    // reader.close();
    // }
    //
    // private static String getStringAfterColon(String str)
    // {
    // int beginIndex = str.indexOf(':') + 1;
    // int endIndex = str.length();
    // return str.substring(beginIndex, endIndex);
    // }
    //
    // private static ArrayList<String> getAuthorsFromString(String str)
    // {
    // int beginIndex = str.indexOf(':') + 1;
    // int endIndex = str.length();
    // String authorsStr = str.substring(beginIndex, endIndex);
    // String[] authorsList = authorsStr.split(",");
    // ArrayList<String> authors = new ArrayList<String>();
    // for (String str2 : authorsList)
    // authors.add(str2.trim());
    // return authors;
    // }
    public static void saveArxivConfigFile(String mirrorSite, String subject) throws IOException
    {
        File file = new File(ARXIV_CONFIG_FILE_PATH);
        if (!file.exists())
            file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        writer.write("ARXIV MIRROR SITE: ");
        writer.write(mirrorSite);
        writer.newLine();
        writer.write("ARXIV SUBJECT: ");
        writer.write(subject);
        writer.newLine();
        writer.flush();
        writer.close();
    }
    
    public static void updateArxivConfigFile(int valueType, String value) throws IOException
    {
        File file = new File(ARXIV_CONFIG_FILE_PATH);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String mirrorSite = parseValue(reader.readLine());
        String subject = parseValue(reader.readLine());
        reader.close();
        
        if (MIRROR_SITE_VALUE_TYPE == valueType)
            mirrorSite = value;
        else if (SUBJECT_VALUE_TYPE == valueType)
            subject = value;
        else
            System.err.println("+++ updateArxivConfigFile(): error value type!");
        
        saveArxivConfigFile(mirrorSite, subject);
    }
    
    public static String readArxivConfigFile(int valueType) throws IOException
    {
        File file = new File(ARXIV_CONFIG_FILE_PATH);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String value = null;
        if (MIRROR_SITE_VALUE_TYPE == valueType)
            value = parseValue(reader.readLine());
        else if (SUBJECT_VALUE_TYPE == valueType)
        {
            reader.readLine();
            value = parseValue(reader.readLine());
        }
        else
            System.err.println("+++ readArxivConfigFile(): error value type!");
        
        reader.close();
        return value;
    }
    
    private static String parseValue(String line)
    {
        int index = line.indexOf(':');
        return line.substring(index + 1).trim();
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
            writer.write("id: ");
            writer.write(paper.getPaperArXivId());
            writer.newLine();
            
            writer.write("title: ");
            writer.write(paper.getPaperTitle());
            writer.newLine();
            
            writer.write("authors: ");
            writer.write(paper.getPaperAuthors());
            writer.newLine();
            
            writer.write("comments: ");
            String comments = paper.getPaperComments();
            if (null == comments)
                writer.write(" ");
            else
                writer.write(comments);
            writer.newLine();
            
            writer.write("journal: ");
            String journal = paper.getPaperJournal();
            if (null == journal)
                writer.write(" ");
            else
                writer.write(journal);
            writer.newLine();
            
            writer.write("subjects: ");
            String subject = paper.getPaperSubjects();
            if (null == subject)
                writer.write(" ");
            else
                writer.write(subject);
            writer.newLine();
            
            writer.write("url: ");
            String url = paper.getPaperURL();
            if (null == url)
                writer.write(" ");
            else
                writer.write(url);
            writer.newLine();
            
            writer.write("abstract: ");
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
    
}
