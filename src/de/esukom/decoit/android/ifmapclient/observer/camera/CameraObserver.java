/* 
 * CameraObserver.java .java        0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.observer.camera;

import java.util.Date;

import android.hardware.Camera;
import android.os.FileObserver;
import android.util.Log;

/**
 * class for detecting a photo that taken with the camera
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, GMBH
 */
public class CameraObserver {

    public static Date sLastPictureTakenDate = null;
    private FileObserver mPhotoFileObserver;

    /**
     * constructor
     * 
     * @param path
     *            path to photo-folder
     */
    public CameraObserver(String path) {
        initObserver(path);
    }

    /**
     * initialize FileObserver that is listening to changes
     * in passed in folder-path
     * 
     * @param path path to photo-folder
     */
    private void initObserver(String path) {
        mPhotoFileObserver = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString() + path) {
            @Override
            public void onEvent(int event, String file) {
                if (event == FileObserver.CREATE && !file.equals(".probe")) {
                    sLastPictureTakenDate = new Date();
                }
            }
        };
        mPhotoFileObserver.startWatching();
    }
}
