/* 
 * MyStartupIntentReceiver.java        0.1.6. 12/03/07
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

package de.esukom.decoit.android.ifmapclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
 
/**
 * broadcast-receiver, automatically starts the application
 * 
 * @version 0.1.6
 * @author  Dennis Dunekacke, DECOIT GmbH
 */
public class MyStartupIntentReceiver extends BroadcastReceiver {
  
        @Override
        public void onReceive(Context context, Intent intent) {
 
            // get the preferences.xml preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            // get autostart-preferences
            boolean sApplicationFileLogging = prefs.getBoolean("autostartPreferences", false);
            
            if (sApplicationFileLogging){
                /* Create intent which will finally start the Main-Activity. */
                Intent myStarterIntent = new Intent(context, TabLayout.class);
 
                /* Set the Launch-Flag to the Intent. */
                myStarterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 
                /* Send the Intent to the OS. */
                context.startActivity(myStarterIntent);
            }
 
        }
 
}