/* 
 * EventParameters.java
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

package de.esukom.decoit.android.ifmapclient.messaging;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import de.esukom.decoit.android.ifmapclient.device.DeviceProperties;
import de.esukom.decoit.android.ifmapclient.device.application.App;
import de.esukom.decoit.android.ifmapclient.device.application.ApplicationProperties;
import de.esukom.decoit.android.ifmapclient.device.system.SystemProperties;
import de.esukom.decoit.android.ifmapclient.event.AppData;
import de.esukom.decoit.android.ifmapclient.event.Event;
import de.esukom.decoit.android.ifmapclient.event.EventBuilder;
import de.esukom.decoit.android.ifmapclient.event.EventData;
import de.esukom.decoit.android.ifmapclient.event.InfoData;
import de.esukom.decoit.android.ifmapclient.event.MonitorData;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;

/**
 * Generate event messages for iMonitor
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

public class EventParameters {

	private DeviceProperties mDeviceProperties;
	private ApplicationProperties mApplicationProperties;

	public EventParameters(DeviceProperties deviceProperties) {
		this.mDeviceProperties = deviceProperties;
		this.mApplicationProperties = mDeviceProperties
				.getApplicationProperties();
	}

	/**
	 * generate InfoEvent
	 */
	public String genInfoEvent() {
		Event mEvent = null;
		String eventIP = mDeviceProperties.getSystemProperties()
				.getLocalIpAddress();

		mEvent = new EventBuilder().withHeader(eventIP).Class("info")
				.data(genInfoData()).create();

		return Toolbox.toJSON(mEvent);
	}

	/**
	 * generate MonitorEvent
	 */
	public String genMonitorEvent() {
		Event mEvent = null;
		String eventIP = mDeviceProperties.getSystemProperties()
				.getLocalIpAddress();

		mEvent = new EventBuilder().withHeader(eventIP).Class("monitor")
				.data(genMonitorData()).create();

		return Toolbox.toJSON(mEvent);
	}

	/**
	 * generate Liste of AppEvents
	 */
	public List<String> genAppEvents() {
		List<String> mEventList = new ArrayList<String>();
		String eventIP = mDeviceProperties.getSystemProperties()
				.getLocalIpAddress();

		for (EventData app : genAppData()) {
			Event mEvent = new EventBuilder().withHeader(eventIP).Class("apps")
					.data(app).create();
			mEventList.add(Toolbox.toJSON(mEvent));
		}
		return mEventList;
	}

	/**
	 * generate info data on connect
	 */
	private EventData genInfoData() {
		InfoData mInfoData = new InfoData();

		mInfoData.setMac(mDeviceProperties.getSystemProperties().getMAC());
		mInfoData.setImei(mDeviceProperties.getPhoneProperties().getIMEI());
		mInfoData.setImsi(mDeviceProperties.getPhoneProperties().getIMSI());
		mInfoData.setKernel(mDeviceProperties.getSystemProperties()
				.getKernelVersion());
		mInfoData.setFirmware(mDeviceProperties.getPhoneProperties()
				.getFirmwareVersion());
		mInfoData.setRoot(mDeviceProperties.getSystemProperties().isRooted());
		mInfoData.setSelinux(mDeviceProperties.getSystemProperties()
				.getSelinuxStatus());
		mInfoData.setBaseband(mDeviceProperties.getPhoneProperties()
				.getBasebandVersion());
		mInfoData.setBuild(mDeviceProperties.getPhoneProperties()
				.getBuildNumber());
		mInfoData
				.setBrand(mDeviceProperties.getPhoneProperties().getBranding());
		mInfoData.setManufacturer(mDeviceProperties.getPhoneProperties()
				.getManufacturer());
		mInfoData.setCellnumber(mDeviceProperties.getPhoneProperties()
				.getPhonenumber());

		return mInfoData;

	}

	/**
	 * generate monitor data on a regular basis ...
	 */
	private EventData genMonitorData() {
		MonitorData mMonitorData = new MonitorData();

		mDeviceProperties.getSystemProperties();
		mMonitorData.setTrafficin(SystemProperties.getTotalRxKBytes());
		mMonitorData.setTrafficout(SystemProperties.getTotalTxKBytes());
		mMonitorData.setCpuload(mDeviceProperties.getSystemProperties()
				.getCurCpuLoadPercent());
		mMonitorData.setMem(mDeviceProperties.getSystemProperties()
				.getFreeRamInPercent());
		mMonitorData.setProcesscount(mApplicationProperties
				.getRuningProcCount());

		mMonitorData.setProcessdetail(mApplicationProperties.getProcesses());

		return mMonitorData;

	}

	/**
	 * generate app data on install/remove
	 */
	private List<EventData> genAppData() {
		List<EventData> mAppDataList = new ArrayList<EventData>();

		for (App mApp : getInstalledApps()) {
			AppData mAppData = new AppData();
			mAppData.setApp(mApp);
			mAppDataList.add(mAppData);
		}

		return mAppDataList;

	}

	/**
	 * get all installed apps as list of App
	 */
	private List<App> getInstalledApps() {
		PackageManager mPackageManager = mDeviceProperties.getPhoneProperties().mAppContext
				.getPackageManager();

		// get a list of installed apps.
		List<ApplicationInfo> packages = mPackageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		List<App> appList = new ArrayList<App>();

		for (ApplicationInfo app : packages) {
			PackageInfo pInfo = null;
			try {
				pInfo = mPackageManager.getPackageInfo(app.packageName.toString(),
						PackageManager.GET_PERMISSIONS);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			appList.add(new App(pInfo.packageName, mPackageManager.getApplicationLabel(
					pInfo.applicationInfo).toString(), pInfo.versionName,
					mApplicationProperties.runningProcessNamesListContainsStartWith(pInfo.packageName),
					pInfo.firstInstallTime, pInfo.lastUpdateTime,
					pInfo.requestedPermissions));
		}
		return appList;
	}
}
