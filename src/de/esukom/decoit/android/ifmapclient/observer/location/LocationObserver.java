/* 
 * LocationObserver.java .java        0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.observer.location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import de.esukom.decoit.android.ifmapclient.activities.MainActivity;
import de.esukom.decoit.android.ifmapclient.activities.R;

/**
 * Location Listener Class for getting current physical Location of the Device/User
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Marcel Jahnke, DECOIT GmbH
 * @version 0.1.6
 * 
 */
public class LocationObserver implements LocationListener {

    private MainActivity mCurrentAppContext;

    /**
     * set current application context (for callback)
     * 
     * @param mainActivity
     *            current application context
     */
    public void setAppllicationContext(MainActivity mainActivity) {
        this.mCurrentAppContext = mainActivity;
    }

    @Override
    public void onLocationChanged(Location loc) {
        loc.getLatitude();
        loc.getLongitude();
        loc.getAltitude();
        mCurrentAppContext.setCurrentLocation(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());

    }

    @Override
    public void onProviderDisabled(String provider) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				mCurrentAppContext);
		builder.setMessage(
				mCurrentAppContext.getResources().getString(
						R.string.location_disabled))
				.setCancelable(false)
				.setPositiveButton(
						mCurrentAppContext.getResources().getString(
								R.string.enable),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent gpsOptionsIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								mCurrentAppContext
										.startActivity(gpsOptionsIntent);
							}
						});
		builder.setNegativeButton(
				mCurrentAppContext.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        // implement me!
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // implement me!
    }
}
