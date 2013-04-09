/* 
 * BatteryReceiver.java        0.1.6 07/02/12
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

package de.esukom.decoit.android.ifmapclient.observer.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Receiver-Objects, registers its own broadcast-receiver to get the current
 * battery-status
 * 
 * @version 0.1.4.2
 * @author Dennis Dunekacke, Decoit GmbH
 */
public class BatteryReceiver {

    public static String sCurrentBatteryLevel = "not detected";
    private BatteryLevelReceiver mBatteryLevelReceiver;

    /**
     * constructor
     * 
     * @param context
     *            application-context
     */
    public BatteryReceiver(Context context) {
        final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryLevelReceiver = new BatteryLevelReceiver();
        context.registerReceiver(mBatteryLevelReceiver, intentFilter);
    }

    /**
     * Broadcast-Receiver for fetching the current battery-status
     */
    private class BatteryLevelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            sCurrentBatteryLevel = level + "";
        }
    }
}