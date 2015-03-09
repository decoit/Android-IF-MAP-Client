/*
 * PermanentlyConnectionManager.java        0.1.6. 12/03/07
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
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License. 
 */

package de.simu.decoit.android.decomap.connection;

import java.util.ArrayList;

/**
 * manager-class for providing a permanently connection
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Marcel Jahnke, Decoit GmbH
 */
public class PermanentlyConnectionManager {

    private ArrayList<Runnable> mActive = new ArrayList<Runnable>();
    private ArrayList<Runnable> mQueue = new ArrayList<Runnable>();

    private static PermanentlyConnectionManager instance;

    public static PermanentlyConnectionManager getInstance() {
        if (instance == null)
            instance = new PermanentlyConnectionManager();
        return instance;
    }

    public void push(Runnable runnable) {
        mQueue.add(runnable);
        if (mActive.size() < 1)
            startNext();
    }

    private void startNext() {
        if (!mQueue.isEmpty()) {
            Runnable next = mQueue.get(0);
            mQueue.remove(0);
            mActive.add(next);
            Thread thread = new Thread(next);
            thread.start();
        }
    }

    public void didComplete(Runnable runnable) {
        mActive.remove(runnable);
        startNext();
    }
}
