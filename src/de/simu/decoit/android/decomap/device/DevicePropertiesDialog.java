/* 
 * DeviceProperties.java         0.2 2015-03-08
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
package de.simu.decoit.android.decomap.device;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import de.simu.decoit.android.decomap.activities.R;

/**
 * class representing a dialog-view-element for displaying the installed applications and its permissions
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Markus Schölzel, DECOIT GmbH
 * @version 0.1
 */
public class DevicePropertiesDialog extends Dialog {
    private String title;
    private String message;

    /**
     * constructor
     * 
     * @param context
     *            current context
     * @param msg
     *            message to display
     */
    public DevicePropertiesDialog(Context context, String ttl, String msg) {
        super(context);
        title = ttl;
        message = msg;
    }

    /**
     * Called when the activity is first created
     * 
     * @param savedInstanceState
     *            state-bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_status_dialog);
        setTitle(title);
        TextView twMsg = (TextView) findViewById(R.id.msgView);
        twMsg.setHorizontallyScrolling(true);
        twMsg.setVerticalScrollBarEnabled(true);
        twMsg.setVerticalFadingEdgeEnabled(true);
        twMsg.setText(message);
    }
}
