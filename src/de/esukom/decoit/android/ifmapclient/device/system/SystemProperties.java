/* 
 * SystemProperties.java        0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.device.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;

/**
 * class for reading out several system-properties
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Markus Schölzel, Decoit GmbH
 */
public class SystemProperties {

	/* application context object */
	private Context mAppContext;

	/* initialize start traffic values */
	private static long mStartRxBytesTotal = TrafficStats.getTotalRxBytes();
	private static long mStartTxBytesTotal = TrafficStats.getTotalTxBytes();
	private static long mStartRxBytes3G = TrafficStats.getMobileRxBytes();
	private static long mStartTxBytes3G = TrafficStats.getMobileTxBytes();
	// private static long mStartRxBytesOther = mStartRxBytesTotal -
	// mStartRxBytes3G;
	// private static long mStartTxBytesOther = mStartTxBytesTotal -
	// mStartTxBytes3G;

	/* current traffic values */
	private static long mCurrentTotalRxBytes;
	private static long mCurrentTotalTxBytes;
	private static long mCurrentRxBytes3G;
	private static long mCurrentTxBytes3G;
	private static long mCurrentRxBytesOther;
	private static long mCurrentTxBytesOther;

	/**
	 * constructor
	 */
	public SystemProperties(Context appContext) {
		this.mAppContext = appContext;
	}

	/**
	 * return local IP Address of the device
	 * 
	 * @return String current device IP
	 */
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// get IPv4 address (exclude loopback address)
					Matcher ipMatcher = Toolbox.getIpPattern().matcher(
							inetAddress.getHostAddress().toString());
					if (ipMatcher.find() && !inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return null;
	}

	/**
	 * return MAC Address of the device
	 * 
	 * @return String current device MAC
	 */
	public String getMAC() {
		WifiManager wifiMan = (WifiManager) mAppContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		return wifiInf.getMacAddress();
	}

	/**
	 * get the current cpu-usage as formatted string for status activity
	 * 
	 * @return String current cpu-usage as formatted string
	 */
	public String getFormattedCurCpuLoadPercent() {
		int[] usage = getCpuUsageStatistic();
		int cpuUsage = 0;
		for (int i = 0; i < usage.length; i++) {
			cpuUsage += usage[i];
		}
		return cpuUsage + "% (user:" + usage[0] + "%, system:" + usage[1]
				+ "%, idle:" + usage[2] + "%, other:" + usage[3] + "%)";
	}

	/**
	 * get the current cpu-usage as string
	 * 
	 * @return String current cpu-usage
	 */
	public int getCurCpuLoadPercent() {
		int[] usage = getCpuUsageStatistic();
		int cpuUsage = 0;
		for (int i = 0; i < usage.length; i++) {
			cpuUsage += usage[i];
		}
		return cpuUsage;
	}

	/**
	 * get current cpu-usage statistics
	 * 
	 * http://stackoverflow.com/questions/2467579/how-to-get-cpu-usage-
	 * statistics-on-android
	 * 
	 * @return integer Array with 4 elements: user, system, idle and other cpu
	 *         usage in percentage.
	 */
	private int[] getCpuUsageStatistic() {
		String tempString = executeTop();
		tempString = tempString.replaceAll(",", "");
		tempString = tempString.replaceAll("User", "");
		tempString = tempString.replaceAll("System", "");
		tempString = tempString.replaceAll("IOW", "");
		tempString = tempString.replaceAll("IRQ", "");
		tempString = tempString.replaceAll("%", "");
		for (int i = 0; i < 10; i++) {
			tempString = tempString.replaceAll("  ", " ");
		}
		tempString = tempString.trim();
		String[] myString = tempString.split(" ");
		int[] cpuUsageAsInt = new int[myString.length];
		for (int i = 0; i < myString.length; i++) {
			myString[i] = myString[i].trim();
			cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
		}
		return cpuUsageAsInt;
	}

	/**
	 * execute top-command and read result for retrieving current cpu-usage
	 * 
	 * @return result of top-command
	 */
	private String executeTop() {
		java.lang.Process p = null;
		BufferedReader in = null;
		String returnString = null;
		try {
			p = Runtime.getRuntime().exec("top -n 1");
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while (returnString == null || returnString.contentEquals("")) {
				returnString = in.readLine();
			}
		} catch (IOException e) {
			Log.e("executeTop", "error in getting first line of top");
			e.printStackTrace();
		} finally {
			try {
				in.close();
				p.destroy();
			} catch (IOException e) {
				Log.e("executeTop",
						"error in closing and destroying top process");
				e.printStackTrace();
			}
		}
		return returnString;
	}

	/**
	 * get the currently unused ram as formatted string for output in status
	 * activity
	 * 
	 * @return current formated free ram as string for output
	 */
	public String getFormattedFreeRam() {
		return getFreeRamInMB() + " MB of " + getTotalMemoryAmountInMB()
				+ " MB (" + getFreeRamInPercent() + "%)";
	}

	/**
	 * calculate total memory amount in MB
	 * 
	 * @return total memory in MB
	 */
	private int getTotalMemoryAmountInMB() {
		int totalMemCalculated = 0;
		RandomAccessFile reader = null;
		try {
			reader = new RandomAccessFile("/proc/meminfo", "r");
			String totalMem = reader.readLine().replace("MemTotal:", "")
					.replace("kB", "").replace(" ", "");
			totalMemCalculated = Integer.parseInt(totalMem) / 1024;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return totalMemCalculated;
	}

	/**
	 * get the current amount if free ram in MB
	 * 
	 * @return current free ram in MB
	 */
	private int getFreeRamInMB() {
		// 1MB is 1024 kilobytes, or 1048576 (1024x1024) bytes
		long availableMegs = getFreeRamInBytes() / 1048576L;
		return (int) availableMegs;
	}

	/**
	 * get the current amount if free ram in Bytes
	 * 
	 * @return current free ram in Bytes
	 */
	public long getFreeRamInBytes() {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) mAppContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		// 1MB is 1024 kilobytes, or 1048576 (1024x1024) bytes
		return mi.availMem;
	}

	/**
	 * get the current amount of free ram in percent
	 * 
	 * @return current free ram in percent
	 */
	public int getFreeRamInPercent() {
		int total = getTotalMemoryAmountInMB();
		if (total > 0) {
			return 100 * getFreeRamInMB() / total;
		}
		return 0;
	}

	/**
	 * get version of used linux kernel
	 * 
	 * @return version-string of linux kernel
	 */
	public String getKernelVersion() {
		return System.getProperty("os.version");
	}

	/**
	 * recalculates all traffic values (tx, rx, 3g, other, total)
	 * 
	 * FIXME this is called way to often at the moment (on each get* call). Add
	 * an anonymous thread that updates traffic values on a fixed intervall
	 * (like 60 seconds).
	 */
	private static void recalculateTraffic() {
		mCurrentTotalRxBytes = TrafficStats.getTotalRxBytes()
				- mStartRxBytesTotal;
		mCurrentTotalTxBytes = TrafficStats.getTotalTxBytes()
				- mStartTxBytesTotal;
		mCurrentRxBytes3G = TrafficStats.getMobileRxBytes() - mStartRxBytes3G;
		mCurrentTxBytes3G = TrafficStats.getMobileTxBytes() - mStartTxBytes3G;
		mCurrentRxBytesOther = mCurrentTotalRxBytes - mCurrentRxBytes3G;
		mCurrentTxBytesOther = mCurrentTotalTxBytes - mCurrentTxBytes3G;
	}

	/**
	 * get current received bytes amount
	 * 
	 * @return received data-amount in KB
	 */
	public static long getTotalRxKBytes() {
		return getTotalRxBytes() / 1024;
	}

	/**
	 * get current transfered bytes amount
	 * 
	 * @return transfered data-amount in KB
	 */
	public static long getTotalTxKBytes() {
		return getTotalTxBytes() / 1024;
	}

	/**
	 * get current received bytes amount
	 * 
	 * @return received data-amount in bytes
	 */
	public static long getTotalRxBytes() {
		recalculateTraffic();
		return mCurrentTotalRxBytes;
	}

	/**
	 * get current transfered bytes amount
	 * 
	 * @return transfered data-amount in bytes
	 */
	public static long getTotalTxBytes() {
		recalculateTraffic();
		return mCurrentTotalTxBytes;
	}

	/**
	 * get current received bytes amount for 3G
	 * 
	 * @return received data-amount in bytes
	 */
	public static long getRxBytes3G() {
		recalculateTraffic();
		return mCurrentRxBytes3G;
	}

	/**
	 * get current transfered bytes amount for 3G
	 * 
	 * @return transfered data-amount in bytes
	 */
	public static long getTxBytes3G() {
		recalculateTraffic();
		return mCurrentTxBytes3G;
	}

	/**
	 * get current received bytes amount for other interfaces than 3G
	 * 
	 * @return received data-amount in bytes
	 */
	public static long getRxBytesOther() {
		recalculateTraffic();
		return mCurrentRxBytesOther;
	}

	/**
	 * get current transfered bytes amount for other interfaces than 3G
	 * 
	 * @return transfered data-amount in bytes
	 */
	public static long getTxBytesOther() {
		recalculateTraffic();
		return mCurrentTxBytesOther;
	}

	/**
	 * check current state of root
	 *
	 * @return true if rooted, otherwise false
	 */
	public boolean isRooted() {
		boolean isRooted = false;

		if (getPathOfBin("su") != null) {
			isRooted = true;
		}

		return isRooted;
	}

	/**
	 * check current state of selinux
	 *
	 * @return mode as string
	 */
	public String getSelinuxStatus() {
		String cmd = "getenforce";
		String execPath = getPathOfBin(cmd);
		String selinuxStatus = null;

		if (execPath != null) {
			Process p = null;
			BufferedReader in = null;

			try {
				p = Runtime.getRuntime().exec(execPath + File.separator + cmd);
				in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				while ((selinuxStatus = in.readLine()) == null) {
				}
			} catch (Exception e) {
				Log.e("getenforce", "error in getting getenforce");
			} finally {
				try {
					in.close();
					p.destroy();
				} catch (Exception e) {
					Log.e("getenforce",
							"error in closing and destroying getenforce process");
				}
			}
		} else {
			selinuxStatus = "unknown";
		}

		return selinuxStatus;
	}
	
	/**
	 * get path to executable
	 *
	 * @return path to executable
	 */
	private String getPathOfBin(String file) {
		String path = null;
		List<String> paths = new ArrayList<String>(Arrays.asList(System.getenv("PATH").split(
				System.getProperty("path.separator"))));
		
		// add some more paths always worth looking at
		String[] morePaths = { "/data/local", "/data/local/xbin", "/sbin",
				"/system/bin", "/system/xbin", "/vendor/bin" };
		paths.addAll(Arrays.asList(morePaths));
		
		for (String p : paths) {
			if (new File(p + File.separator + file).canExecute()) {
				path = p;
				break;
			}
		}
		return path;
	}
}
