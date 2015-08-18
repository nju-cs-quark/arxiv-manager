// /////////////////////////////////////////////////////////////////////////////
// @author
// ------ Wang Kun
// ------ Email: nju.wangkun@gmail.com
// @date Aug 14, 2015 11:08:52 AM
// @file arxiv-extractor/cn.edu.nju.cs/ArxivExtractor.java
// @brief
// ArxivExtractor supports following commands
// -help "help documentation"
// -mirror "mirror site"
// -month "yymm" -dir "directory to store the extracted papers"
// -year "yy" -dir "directory to store the extracted papers"
// /////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.extractor;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cn.edu.nju.cs.paper.PaperIO;

@SuppressWarnings("deprecation")
public class ArxivExtractor
{
    public static void main(String[] args) throws ParseException, IOException
    {
        // Step0. setup arxiv configuration file //////////////////////////////////////////////////
        PaperIO.saveArxivConfigFile(PaperIO.DEFAULT_ARXIV_MIRROR_SITE,
                PaperIO.DEFAULT_ARXIV_SUBJECT);
        // Step1. setup options ///////////////////////////////////////////////////////////////////
        Options options = setupOptions();
        // Step2. setup parser ////////////////////////////////////////////////////////////////////
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        // Step3. querying the command line ///////////////////////////////////////////////////////
        if (cmd.hasOption("help"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("arxiv extractor", options, true);
        }
        if (cmd.hasOption("mirror"))
        {
            String mirror = cmd.getOptionValue("mirror");
            PaperIO.updateArxivConfigFile(PaperIO.MIRROR_SITE_VALUE_TYPE, mirror);
        }
        if (cmd.hasOption("subject"))
        {
            String subject = cmd.getOptionValue("subject");
            PaperIO.updateArxivConfigFile(PaperIO.SUBJECT_VALUE_TYPE, subject);
        }
        if (cmd.hasOption("new"))
        {
            // if (cmd.hasOption("dir"))
            // {
            // dirString = cmd.getOptionValue("dir");
            // }
            //
            System.out.println("arXiv Extractor: extracting new submissions now...");
            Extractor newSubmissionExtractor = new NewSubmissionExtractor();
            newSubmissionExtractor.extract();
            System.out.println("arXiv Extractor: extracting new submissions done!");
        }
        if (cmd.hasOption("month"))
        {
            String monthString = cmd.getOptionValue("month");
            // String dirString = "./";
            // if (cmd.hasOption("dir"))
            // {
            // dirString = cmd.getOptionValue("dir");
            // }
            System.out.println("arXiv Extractor: extracting submissions of month "
                    + monthString + " now...");
            Extractor monthExtractor = new MonthExtractor(monthString);
            monthExtractor.extract();
            System.out.println("arXiv Extractor: extracting submissions of month "
                    + monthString + " done!");
        }
        if (cmd.hasOption("year"))
        {
            String yearString = cmd.getOptionValue("year");
            // String dirString = "./";
            // if (cmd.hasOption("dir"))
            // {
            // dirString = cmd.getOptionValue("dir");
            // }
            System.out.println("arXiv Extractor: extracting submissions of year "
                    + yearString + " now...");
            Extractor yearExtractor = new YearExtractor(yearString);
            yearExtractor.extract();
            System.out.println("arXiv Extractor: extracting submissions of year "
                    + yearString + " done!");
        }
    }
    
    public static Options setupOptions()
    {
        // Boolean Options
        Option helpOption = new Option("help",
                "help documentation on the extractor tool");
        Option newOption = new Option("new",
                "extracts all papers of latest published");
        // -mirror option
        OptionBuilder.withArgName("mirror");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("set arviv mirror site (default is 'http://www.arxiv.org')");
        Option mirrorOption = OptionBuilder.create("mirror");
        // -subject option
        OptionBuilder.withArgName("subject");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("set arviv subject (default is 'quant-ph')");
        Option subjectOption = OptionBuilder.create("subject");
        // -month "yymm"
        OptionBuilder.withArgName("month");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("extracts all papers of the given month");
        Option monthOption = OptionBuilder.create("month");
        // -year "yy"
        OptionBuilder.withArgName("year");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("extracts all papers of the given year");
        Option yearOption = OptionBuilder.create("year");
        // -directory "directory"
        OptionBuilder.withArgName("dir");
        OptionBuilder.hasArg();
        OptionBuilder
                .withDescription("specify the directory to store the extracted papers");
        Option dirOption = OptionBuilder.create("dir");
        
        Options options = new Options();
        options.addOption(helpOption);
        options.addOption(mirrorOption);
        options.addOption(subjectOption);
        options.addOption(newOption);
        options.addOption(monthOption);
        options.addOption(yearOption);
        options.addOption(dirOption);
        
        return options;
    }
}
