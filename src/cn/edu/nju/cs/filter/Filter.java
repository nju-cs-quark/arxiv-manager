// /////////////////////////////////////////////////////////////////////////////
// Copyright 2014, Kun Wang, http://www.quantumman.me/.
// Department of Computer Science & Technology, Nanjing University, China.
// Distributed under the protect of GNU GPLv3.
// @date Aug 26, 2015-10:15:36 AM
// @file arxiv-manager/cn.edu.nju.cs.filter/Filter.java
// @brief Filter can filter a given file, or filter a given list which contains
// many files. Papers' information is stored in the file.
// The Filter will call PapersFilter to filter a specific file.
// /////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Filter
{
    private ArrayList<String> originFilePathList = null;
    
    public Filter(String originFilePath) throws IOException
    {
        this.originFilePathList = new ArrayList<String>();
        this.originFilePathList.add(originFilePath);
    }
    
    public Filter(ArrayList<String> originFilePathList) throws IOException
    {
        this.originFilePathList = new ArrayList<String>();
        this.originFilePathList = originFilePathList;
    }
    
    public void filter() throws IOException
    {
        Iterator<String> it = this.originFilePathList.iterator();
        while (it.hasNext())
        {
            String filePath = it.next();
            PapersFilter papersFilter = new PapersFilter(filePath);
            papersFilter.filter();
        }
    }
}
