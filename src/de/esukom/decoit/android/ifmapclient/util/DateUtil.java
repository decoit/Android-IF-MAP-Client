/**
 * File: DateUtil.java
 * 
 * Copyright (C) 2012 Hochschule Hannover
 * 
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package de.esukom.decoit.android.ifmapclient.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Simple util class for date transformations
 * 
 */
public class DateUtil {

	private final static SimpleDateFormat mDateFormatHumanReadable = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");
	private final static Calendar mCalendar = Calendar.getInstance();

	private final static SimpleDateFormat DATEFORMAT_YYYYMMDD = new SimpleDateFormat(
			"yyyyMMdd");

	private static SimpleDateFormat mDateFormatXsd = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	public static String getFormattedDateFromTimestamp(final long timestamp) {
		synchronized (mCalendar) {
			mCalendar.setTimeInMillis(timestamp);
			return mDateFormatHumanReadable.format(mCalendar.getTime());
		}
	}

	public static String xsd2HumanReadble(final String xsdDate) {
		return xsdDate.replace('T', ' ').substring(0, xsdDate.lastIndexOf('+'));
	}

	public static String getCurrentTimestampFormatted(final SimpleDateFormat sdf) {
		synchronized (mCalendar) {
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			return sdf.format(mCalendar.getTime());
		}
	}

	public static String getCurrentTimestampFormatted() {
		synchronized (mCalendar) {
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			return mDateFormatHumanReadable.format(mCalendar.getTime());
		}
	}

	public static String getCurrentTimestampXsd() {
		synchronized (mCalendar) {
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			final String d = mDateFormatXsd.format(mCalendar.getTime());
			final StringBuilder sb = new StringBuilder(d);
			sb.insert(22, ':');
			return sb.toString();
		}
	}

	public static String getTimestampXsd(final long timestamp) {
		synchronized (mCalendar) {
			mCalendar.setTimeInMillis(timestamp);
			final String d = mDateFormatXsd.format(mCalendar.getTime());
			final StringBuilder sb = new StringBuilder(d);
			sb.insert(22, ':');
			return sb.toString();
		}
	}

	public static long getCurrentTimestamp() {
		return System.currentTimeMillis();
	}

	public static String getFormattedDateFromTimestamp(final String timestamp) {
		synchronized (mCalendar) {
			try {
				final long time = Long.parseLong(timestamp);
				return getFormattedDateFromTimestamp(time);
			} catch (final NumberFormatException e) {
				return timestamp;
			}
		}
	}
}
