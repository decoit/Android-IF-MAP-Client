/* 
 * CustomLogger.java        0.2 2015-03-08
 * 
 * Licensed to the Apache Software Foundation (ASF) under one 
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information 
 * regarding copyright ownership.  The ASF licenses this file 
 * to you under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance 
 * with the License.  You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * d
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License. 
 */

package de.simu.decoit.android.decomap.util;

import java.io.BufferedWriter;
import java.io.FileWriter;

import android.os.Environment;

/**
 * class for logging messages to file (and outputting 
 * log-messages on console)
 * 
 * @author Dennis Dunekacke, Decoit GmbH
 * @version 0.2
 */
public class CustomLogger {

    // log-file-name
    private static String sLogFile = "log.txt";

    /**
     * output log message to log-file and use the android-logger for showing message on debug-console
     * 
     * @param tag
     *            tag of log message
     * @param message
     *            message to output
     */
    public static void logTxt(String tag, String message) {
        if (Toolbox.sLogFolderExists) {
            try {
                // write log-message to file
                BufferedWriter bw = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + Toolbox.sLogPath + sLogFile, true));
                bw.write("[" + Toolbox.now(Toolbox.DATE_FORMAT_NOW_EXPORT) + "] " + "(" + tag + ") " + message);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toolbox.sLogFolderExists = Toolbox.createDirIfNotExists(Toolbox.sLogPath);
        }
    }
}