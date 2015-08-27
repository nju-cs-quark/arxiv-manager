///////////////////////////////////////////////////////////////////////////////
// Copyright 2014, Kun Wang, http://www.quantumman.me/.
// Department of Computer Science & Technology, Nanjing University, China.
// Distributed under the protect of GNU GPLv3.
// @date Aug 27, 2015-10:26:20 AM
// @file arxiv-manager/cn.edu.nju.cs.utility/FileOpener.java
// @brief 
///////////////////////////////////////////////////////////////////////////////
package cn.edu.nju.cs.utility;

import java.io.IOException;

public class FileOpener
{
    
    /* @param args
     * void
     */
    public static void open(String filePath)
    {
        String cmd = "cmd.exe /c start " + filePath;
        Runtime run = Runtime.getRuntime();
        try
        {
            run.exec(cmd);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            System.err.println("FileOpener: open file error.");
            e.printStackTrace();
        }
    }
}
