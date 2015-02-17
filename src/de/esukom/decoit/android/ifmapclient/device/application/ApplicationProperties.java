/* 
 * ApplicationProperties.java        0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.device.application;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

/**
 * class for reading out several properties from applications which are installed on the device
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 */
public class ApplicationProperties {

    /* application context object */
    private Context mAppContext;

    /* separator for lines in formatted result */
    private final String lineSeperator = "----------------------------------------------------------------------------------------";

    /**
     * constructor
     */
    public ApplicationProperties(Context appContext) {
        this.mAppContext = appContext;
    }

    /**
     * get list of applications that are currently present on the device
     * 
     * @param excludeNativeApplications
     *            don�t add native applications (com.android or com.google) to list
     * @param includeVersionNumber
     *            add version number to each list-entry
     * @param includePermissions
     *            add permissions for each list-entry
     * @param includeCurrentRunStatus
     *            add isCurrentRunning Flag
     * 
     * @return list of application-list-entry object
     */
    public ArrayList<ApplicationListEntry> getApplicationList(boolean excludeNativeApplications, boolean includeVersionNumber,
            boolean includePermissions, boolean includeCurrentRunStatus) {
        ArrayList<ApplicationListEntry> returnList = new ArrayList<ApplicationListEntry>();
        final PackageManager pm = mAppContext.getPackageManager();
        final List<ResolveInfo> resolves = pm
                .queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        for (ResolveInfo resolveInfo : resolves) {
            ApplicationListEntry curEntry = null;
            PackageInfo packageInfo = null;

            /* initialize package info if necessary */
            if (includeVersionNumber || includePermissions) {
                try {
                    packageInfo = pm.getPackageInfo(resolveInfo.activityInfo.applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            /* exclude "native" application */
            if (excludeNativeApplications) {
                if (!resolveInfo.activityInfo.name.startsWith("com.google") && !resolveInfo.activityInfo.name.startsWith("com.android")) {
                    curEntry = new ApplicationListEntry(resolveInfo.activityInfo.name);
                    curEntry.setInstallerPackageName(pm.getInstallerPackageName(resolveInfo.activityInfo.applicationInfo.packageName));
                }
            } else {
                curEntry = new ApplicationListEntry(resolveInfo.activityInfo.name);
                curEntry.setInstallerPackageName(pm.getInstallerPackageName(resolveInfo.activityInfo.applicationInfo.packageName));
            }

            if (curEntry != null) {

                /* include version name and code */
                if (includeVersionNumber) {
                    curEntry.setVersionName(packageInfo.versionName);
                    curEntry.setVersionCode(packageInfo.versionCode);
                }

                /* include is currently running flag */
                if (includeCurrentRunStatus) {
                    curEntry.setCurrentlyRunning(runningProcessNamesListContainsStartWith(resolveInfo.activityInfo.name.toString()));
                }

                /* include permissions */
                if (includePermissions) {
                    if (packageInfo.requestedPermissions != null) {
                        for (String permissionInfo : packageInfo.requestedPermissions) {
                            curEntry.addPermission(permissionInfo);
                        }
                    }
                }

                /* add current application entry to list */
                returnList.add(curEntry);
            }

        }
        return returnList;
    }

    /**
     * get formatted application list used for display in StatusActivity
     * 
     * @param excludeNativeApplications
     *            don�t add native applications (com.android or com.google) to list
     * @param includeVersionNumber
     *            add version number to each list-entry
     * @param includePermissions
     *            add permissions for each list-entry
     * @param includeCurrentRunStatus
     *            add isCurrentRunning Flag
     * 
     * @return string list of application-informations
     */
    public ArrayList<String> getFormattedApplicationList(boolean excludeNativeApplications, boolean includeVersionNumber,
            boolean includePermissions, boolean includeCurrentRunStatus) {
        ArrayList<String> returnList = new ArrayList<String>();

        ArrayList<ApplicationListEntry> applicationList = getApplicationList(excludeNativeApplications, includeVersionNumber,
                includePermissions, includeCurrentRunStatus);

        for (ApplicationListEntry currentEntry : applicationList) {
            /* application-name */
            returnList.add(currentEntry.getName());
            
            /* installer package name */
            returnList.add("Installer Package: " + currentEntry.getInstallerPackageName());

            /* version number and code */
            if (includeVersionNumber) {
                returnList.add("Version Name: " + currentEntry.getVersionName());
                returnList.add("Version Code: " + currentEntry.getVersionCode());
            }

            /* current running flag */
            if (includeCurrentRunStatus) {
                returnList.add("is running: " + currentEntry.isCurrentlyRunning());
            }

            returnList.add(lineSeperator);

            /* permissions */
            if (includePermissions) {
                for (Permission currentPermissionEntry : currentEntry.getPermissions()) {
                    if (currentPermissionEntry.getPermissionType() == Permission.PERMISSIONTYPE_GRANTED) {
                        returnList.add("GRANTED:  " + currentPermissionEntry.getPermissionName());
                    } else {
                        returnList.add("REQUIRED: " + currentPermissionEntry.getPermissionName());
                    }
                }
                returnList.add(lineSeperator);
            }
        }
        return returnList;
    }

    /**
     * get permissions and their relating applications
     * 
     * @param permisissionType
     *            byte describing the type of permission to read out
     * @return List of installed permissions and their related applications
     */
    public ArrayList<PermissionListEntry> getPermissionsList() {
        // gather all permissions and the applications that uses them
        ArrayList<PermissionListEntry> permissionAppList = new ArrayList<PermissionListEntry>();

        final PackageManager pm = mAppContext.getPackageManager();
        final List<ResolveInfo> resolves = pm
                .queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        for (ResolveInfo resolveInfo : resolves) {
            String appName = resolveInfo.activityInfo.name;
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            PackageInfo packageInfo = null;
            try {
                packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            if (packageInfo.requestedPermissions != null) {
                for (String permissionInfo : packageInfo.requestedPermissions) {

                    // add current permission to permission-list
                    PermissionListEntry newEntry = new PermissionListEntry(permissionInfo);
                    if (permissionAppList.contains(newEntry)) {
                        for (int i = 0; i < permissionAppList.size(); i++) {
                            if (permissionAppList.get(i).getPermissionName().equals(newEntry.getPermissionName())) {
                                permissionAppList.get(i).addApplication(appName);
                            }
                        }
                    } else {
                        newEntry.addApplication(appName);
                        permissionAppList.add(newEntry);
                    }
                }
            }
        }

        return permissionAppList;
    }

    /**
     * get formatted permission list used for display in StatusActivity
     * 
     * @return string list of permission-informations
     */
    public ArrayList<String> getFormattedPermissionsList() {
        ArrayList<String> returnList = new ArrayList<String>();
        ArrayList<PermissionListEntry> permissionList = getPermissionsList();
        for (PermissionListEntry currentEntry : permissionList) {
            returnList.add(currentEntry.getPermissionName());
            returnList.add(lineSeperator);
            for (String currentAppEntry : currentEntry.getPermissionApplications()) {
                returnList.add(currentAppEntry);
            }
            returnList.add(lineSeperator);
        }

        return returnList;
    }

    /**
     * get number of currently running processes
     * 
     * @return current number of running processes
     */
    public int getRuningProcCount() {
        ActivityManager servMng = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = servMng.getRunningAppProcesses();
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    /**
     * get list of all running processes
     * 
     * @return list of running processes
     */
    private List<ActivityManager.RunningAppProcessInfo> getRunningProcNames() {
        ActivityManager servMng = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
        return servMng.getRunningAppProcesses();
    }

    /**
     * get String-list of all running process-names
     * 
     * @return string-list of containing all running process names
     */
    private ArrayList<String> getRunningAppProcessNamesAsStrings() {
        ArrayList<String> returnList = new ArrayList<String>();
        for (ActivityManager.RunningAppProcessInfo currentEntry : getRunningProcNames()) {
            returnList.add(currentEntry.processName);
        }
        return returnList;
    }

    /**
     * search if running-process-names-list contains an entry that starts with the passed in search string
     * 
     * @param searchString
     *            string to search for
     * @return true, if list contains an entry that start with passed in search-striung
     */
    public boolean runningProcessNamesListContainsStartWith(String searchString) {
        for (String currentEntry : getRunningAppProcessNamesAsStrings()) {
            if (searchString.startsWith(currentEntry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get formated list of all running processes
     * 
     * @return formatted list containing all runnning processes
     */
    public ArrayList<String> getFormattedRunningAppProcessNamesList() {
        List<ActivityManager.RunningAppProcessInfo> processList = getRunningProcNames();
        ArrayList<String> returnList = new ArrayList<String>();
        if (processList != null) {
            for (int i = 0; i < processList.size(); ++i) {
                returnList.add(processList.get(i).processName);
                returnList.add(lineSeperator);
            }
        } else {
            returnList.add("none detected!");
        }

        return returnList;
    }

	/**
	 * get percentage of used memory by a specific pid based on /proc/statm
	 *
	 * @param pid process id
	 * @return memory usage of process
	 */
	private String getMemOfPid(int pid) {
		String pidMem = null, totalMem = null;
		String percentageMem;
		RandomAccessFile statm = null, meminfo = null;

		try {
			statm = new RandomAccessFile("/proc/" + pid + "/statm", "r");
			meminfo = new RandomAccessFile("/proc/meminfo", "r");

			// resident set size of process
			pidMem = statm.readLine().split(" ")[1];

			// total memory (MemTotal: ...)
			String[] splitMem = meminfo.readLine().split(" ");
			totalMem = splitMem[splitMem.length -  2];
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (statm != null) {
				try {
					statm.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (meminfo != null) {
				try {
					meminfo.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		// used pages * page size / byte
		int usedMem = Integer.parseInt(pidMem) * 4096 / 1024;

		percentageMem = ((int) (usedMem / Float.parseFloat(totalMem) * 100))
				+ "%";

		return percentageMem;
	}

    /**
     * get list of all running processes as {@link Process}
     *
     * @return list containing all runnning processes as Process
     */
	public List<Process> getProcesses() {
		List<ActivityManager.RunningAppProcessInfo> processList = getRunningProcNames();
		List<Process> returnList = new ArrayList<Process>();
		
		for(ActivityManager.RunningAppProcessInfo pInfo: processList) {
			returnList.add(new Process(pInfo.pid, pInfo.processName, pInfo.uid, getMemOfPid(pInfo.pid)));
		}

		return returnList;
	}
}
