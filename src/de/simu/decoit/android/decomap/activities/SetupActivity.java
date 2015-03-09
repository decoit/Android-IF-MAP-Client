/* 
 * SetupActivity.java        0.1.6. 12/03/07
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

package de.simu.decoit.android.decomap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import de.simu.decoit.android.decomap.preferences.PreferencesValues;
import de.simu.decoit.android.decomap.util.Toolbox;

/**
 * Activity for setting Preferences
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Markus Schölzel, Decoit GmbH
 */
public class SetupActivity extends PreferenceActivity {

    // -------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE HANDLING
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbox.logTxt(this.getClass().getName(), "SetupActivity.OnCreate(...) called");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceCategory serverSettings = (PreferenceCategory) findPreference("serverSettings");
        PreferenceCategory imonitorSettings = (PreferenceCategory) findPreference("imonitorSettings");
        PreferenceCategory userSettings = (PreferenceCategory) findPreference("userSettings");
        PreferenceCategory connectionSettings = (PreferenceCategory) findPreference("connectionSettings");
        PreferenceCategory applicationSettings = (PreferenceCategory) findPreference("applicationSettings");
        PreferenceCategory locationTrackingSettings = (PreferenceCategory) findPreference("locationSettings");
        
        // lock/unlock user and server settings
        if (PreferencesValues.sLockPreferences) {
            serverSettings.setEnabled(false);
            userSettings.setEnabled(false);
            applicationSettings.setEnabled(false);
        } else {
            serverSettings.setEnabled(true);
            userSettings.setEnabled(true);
            applicationSettings.setEnabled(true);
        }

        // lock/unlock connection settings
        if (PreferencesValues.sLockConnectionPreferences) {
            connectionSettings.setEnabled(false);
        } else {
            connectionSettings.setEnabled(true);
        }

        // lock/unlock iMonitor settings
		if (PreferencesValues.sLockIMonitorPreferences) {
			imonitorSettings.setEnabled(false);
		} else {
			imonitorSettings.setEnabled(true);
		}

        // lock/unlock location tracking system
        if (PreferencesValues.sLockLocationTrackingOptions){
            locationTrackingSettings.setEnabled(false);
        }
        else{
            locationTrackingSettings.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        Toolbox.logTxt(this.getClass().getName(), "SetupActivity.OnResume(...) called");
        super.onResume();
        PreferenceCategory serverSettings = (PreferenceCategory) findPreference("serverSettings");
        PreferenceCategory imonitorSettings = (PreferenceCategory) findPreference("imonitorSettings");
        PreferenceCategory userSettings = (PreferenceCategory) findPreference("userSettings");
        PreferenceCategory connectionSettings = (PreferenceCategory) findPreference("connectionSettings");
        PreferenceCategory applicationSettings = (PreferenceCategory) findPreference("applicationSettings");
        PreferenceCategory locationTrackingSettings = (PreferenceCategory) findPreference("locationSettings");

        // lock/unlock user and server settings
        if (PreferencesValues.sLockPreferences) {
            serverSettings.setEnabled(false);
            userSettings.setEnabled(false);
            applicationSettings.setEnabled(false);
        } else {
            serverSettings.setEnabled(true);
            userSettings.setEnabled(true);
            applicationSettings.setEnabled(true);
        }

        // lock/unlock user and server settings
        if (PreferencesValues.sLockConnectionPreferences) {
            connectionSettings.setEnabled(false);
        } else {
            connectionSettings.setEnabled(true);
        }

        // lock/unlock iMonitor settings
		if (PreferencesValues.sLockIMonitorPreferences) {
			imonitorSettings.setEnabled(false);
		} else {
			imonitorSettings.setEnabled(true);
		}

        // lock/unlock location tracking system
        if (PreferencesValues.sLockLocationTrackingOptions){
            locationTrackingSettings.setEnabled(false);
        }
        else{
            locationTrackingSettings.setEnabled(true);
        }
    }

    // -------------------------------------------------------------------------
    // BUTTON HANDLING
    // -------------------------------------------------------------------------
    
    /**
     * we override the behavior of the back-button so that the application runs
     * in the background (instead of destroying it) when pressing back (similar
     * to the home button)
     */
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}
