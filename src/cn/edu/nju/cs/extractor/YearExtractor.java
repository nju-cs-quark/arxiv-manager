package cn.edu.nju.cs.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class YearExtractor implements Extractor
{
    private static final int  MONTHS_OF_YEAR = 12;
    private String            year           = null;
    private ArrayList<String> paperFilePathList  = null;
    
    public YearExtractor(String year)
    {
        this.year = year;
        this.paperFilePathList = new ArrayList<String>();
    }
    
    @Override
    public void extract() throws IOException
    {
        // Step1. generate the months of the year in the form yymm
        ArrayList<String> monthsList = createMonthsList(year);
        
        Iterator<String> it = monthsList.iterator();
        while (it.hasNext())
        {
            String month = it.next();
            Extractor monthExtractor = new MonthExtractor(month);
            monthExtractor.extract();
            this.paperFilePathList.add(((MonthExtractor)monthExtractor).getPaperFilePath());
        }
    }
    
    public ArrayList<String> getPaperFilePathList()
    {
        return this.paperFilePathList;
    }
    
    private ArrayList<String> createMonthsList(String year)
    {
        ArrayList<String> monthsList = new ArrayList<String>();
        // add links yy01 ~ yy12
        for (int month = 1; month <= MONTHS_OF_YEAR; month++)
        {
            StringBuffer monthString = new StringBuffer();
            monthString.append(year);
            if (isOneDigit(month))
                monthString.append("0" + month);
            else
                monthString.append(month);
            monthsList.add(monthString.toString());
        }
        
        return monthsList;
    }
    
    private boolean isOneDigit(int num)
    {
        if (0 <= num && num < 10)
            return true;
        else
            return false;
    }
    
}
