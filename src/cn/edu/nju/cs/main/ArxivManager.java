// /////////////////////////////////////////////////////////////////////////////
// @author
// ------ Wang Kun
// ------ Email: nju.wangkun@gmail.com
// @date Aug 14, 2015 11:08:52 AM
// @file arxiv-extractor/cn.edu.nju.cs/ArxivExtractor.java
// @brief
// ArxivExtractor supports following commands
// -help "help documentation"
// -month "yymm" -dir "directory to store the extracted papers"
// -year "yy" -dir "directory to store the extracted papers"
// -e extract command
// -f filter comman.
// /////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.main;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cn.edu.nju.cs.extractor.*;
import cn.edu.nju.cs.filter.Filter;
import cn.edu.nju.cs.filter.PapersFilter;
import cn.edu.nju.cs.utility.FileOpener;
import cn.edu.nju.cs.utility.ManagerIO;

@SuppressWarnings("deprecation")
public class ArxivManager
{
    public static void main(String[] args) throws ParseException, IOException
    {
        // Step0. setup arxiv configuration file //////////////////////////////////////////////////
        ManagerIO.saveArxivConfigFile(ManagerIO.DEFAULT_ARXIV_MIRROR_SITE,
                ManagerIO.DEFAULT_ARXIV_SUBJECT, ManagerIO.DEFAULT_STORAGE_DIRECTORY);
        // Step1. setup options ///////////////////////////////////////////////////////////////////
        Options options = setupOptions();
        // Step2. setup parser ////////////////////////////////////////////////////////////////////
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        // Step3. querying the command line ///////////////////////////////////////////////////////
        setupQueries(options, cmd);
    }
    
    public static Options setupOptions()
    {
        // Boolean Options
        Option helpOption = new Option("h", "Help documentation on the arXiv manager tool");
        Option newOption = new Option("n", "Extract new submission papers. " +
                "In the quant-ph case, will extract papers from " +
                "http://arxiv.org/list/quant-ph/new");
        Option extractOption = new Option("e", "Extract papers from arXiv. " +
                "Only with this option enabled, extraction commands -n/-m/-y " +
                "will work.");
        // -month "yymm"
        OptionBuilder.withArgName("month-num");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Extract all papers of the given month. " +
                "The value must be given as four-digit number, for example " +
                "0403 means March, 2004." +
                "In the quant-ph case, will extract papers from " +
                "http://arxiv.org/list/quant-ph/0403");
        Option monthOption = OptionBuilder.create("m");
        // -year "yy"
        OptionBuilder.withArgName("year-num");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Extract all papers of the given year. " +
                "The value must be given as two-digit number, for example " +
                "04 for 2004." +
                "In the quant-ph case, will extract papers from " +
                "http://arxiv.org/list/quant-ph/04xx, where xx are specific month number.");
        Option yearOption = OptionBuilder.create("y");
        // -f "file-name"
        OptionBuilder.withArgName("file-paths");
        OptionBuilder.hasOptionalArg();
        OptionBuilder
                .withDescription("Filter the given file or files. " +
                        "Files must be given with absolute paths. " +
                        "The file-paths should be embraced with double quotation marks, " +
                        "and seperated by comma: \"filepath1, filepath2, ...\". " +
                        "If worked with -e option, will filter the extracted papers. " +
                        "In this case, \"file-paths\" is not needed.");
        Option filterOption = OptionBuilder.create("f");
        
        Options options = new Options();
        options.addOption(helpOption);
        options.addOption(extractOption);
        options.addOption(filterOption);
        options.addOption(newOption);
        options.addOption(monthOption);
        options.addOption(yearOption);
        
        return options;
    }
    
    private static void setupQueries(Options options, CommandLine cmd) throws IOException
    {
        // help command
        if (cmd.hasOption("h"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(100);
            formatter.printHelp("arxiv-manager", options, true);
        }
        // extract command. -n/-m/-y commands are within the extraction command.
        // That means if there is no '-e' command, then the above sub-commands are useless.
        if (cmd.hasOption("e"))
        {
            if (cmd.hasOption("n"))
            {
                System.out.println("arxiv-manager: extracting new submissions now...");
                Extractor newSubmissionExtractor = new NewSubmissionExtractor();
                newSubmissionExtractor.extract();
                System.out.println("arxiv-manager: extracting new submissions done!");
                // if there is filter command when extracting papers,
                // then after extraction, filter these papers.
                if (cmd.hasOption("f"))
                {
                    String filePath = ((NewSubmissionExtractor) newSubmissionExtractor)
                            .getPaperFilePath();
                    System.out.println("arxiv-manager: filtering new submissions now...");
                    Filter filter = new Filter(filePath);
                    filter.filter();
                    System.out.println("arxiv-manager: filtering new submissions done!");
                    
                    // open the filtered file for me to read
                    int index = filePath.indexOf('.');
                    StringBuffer filteredFilePath = new StringBuffer();
                    filteredFilePath.append(filePath.substring(0, index));
                    filteredFilePath.append(PapersFilter.FILTERED_PAPERS_FILE_MARK);
                    filteredFilePath.append(filePath.substring(index));
                    FileOpener.open(filteredFilePath.toString());
                }
            }
            if (cmd.hasOption("m"))
            {
                String monthString = cmd.getOptionValue("month");
                System.out.println("arxiv-manager: extracting submissions of month "
                        + monthString + " now...");
                Extractor monthExtractor = new MonthExtractor(monthString);
                monthExtractor.extract();
                System.out.println("arxiv-manager: extracting submissions of month "
                        + monthString + " done!");
                // if there is filter command when extracting papers,
                // then after extraction, filter these papers.
                if (cmd.hasOption("f"))
                {
                    String filePath = ((MonthExtractor) monthExtractor).getPaperFilePath();
                    System.out.println("arxiv-manager: filtering submissions of month "
                            + monthString + " now...");
                    Filter filter = new Filter(filePath);
                    filter.filter();
                    System.out.println("arxiv-manager: filtering submissions of month "
                            + monthString + " done!");
                }
            }
            if (cmd.hasOption("y"))
            {
                String yearString = cmd.getOptionValue("year");
                System.out.println("arxiv-manager: extracting submissions of year "
                        + yearString + " now...");
                Extractor yearExtractor = new YearExtractor(yearString);
                yearExtractor.extract();
                System.out.println("arxiv-manager: extracting submissions of year "
                        + yearString + " done!");
                // if there is filter command when extracting papers,
                // then after extraction, filter these papers.
                if (cmd.hasOption("f"))
                {
                    ArrayList<String> filePathList =
                            ((YearExtractor) yearExtractor).getPaperFilePathList();
                    System.out.println("arxiv-manager: filtering submissions of year "
                            + yearString + " now...");
                    Filter filter = new Filter(filePathList);
                    filter.filter();
                    System.out.println("arxiv-manager: filtering submissions of year "
                            + yearString + " done!");
                }
            }
        }
        // filter command. In this case, papers to be filtered are stored in a file,
        // or in files. The files' names are passed by the parameter attached to -f option.
        // Example:
        // 1. arxiv-manager -f "file1"
        // 2. arxiv-manager -f "file1, file2, ..."
        // Do not forger the double quotation marks, otherwise the parameter cannot
        // be parsed correctly.
        if (cmd.hasOption("f"))
        {
            String value = cmd.getOptionValue("f");
            if (null == value)
                return;
            
            System.out.println("arxiv-manager: filtering files now...");
            String[] filePathsList = value.split(",");
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < filePathsList.length; i++)
                list.add(filePathsList[i].trim());
            Filter filter = new Filter(list);
            filter.filter();
            System.out.println("arxiv-manager: filtering files done!");
        }
    }
}
