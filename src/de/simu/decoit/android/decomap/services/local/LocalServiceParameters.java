/*
 * LocalServiceParameters.java       0.1.6 07/02/12
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

package de.simu.decoit.android.decomap.services.local;

import android.os.Handler;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.simu.decoit.android.decomap.activities.MainActivity;
import de.simu.decoit.android.decomap.messaging.MessageHandler;
import de.simu.decoit.android.decomap.preferences.PreferencesValues;
import de.simu.decoit.android.decomap.services.PermanentConnectionService;
import de.simu.decoit.android.decomap.services.RenewConnectionService;

/**
 * Container-Class that holds several parameters that are required for creating
 * a new local-service-object
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Marcel Jahnke, DECOIT GmbH
 * @author Markus Schölzel, Decoit GmbH
 * @version 0.1.6
 */
public class LocalServiceParameters {

	// local-service-type
	public static final int SERVICE_BINDER_TYPE_PERMANENT_CONNECTION_SERVICE = 0;
	public static final int SERVICE_BINDER_TYPE_RENEW_CONNECTION_SERVICE = 1;
	public static final int SERVICE_BINDER_TYPE_TIME_COUNTING_SERVICE = 2;

	private boolean mSSLConnectionType = false;
	private byte mMessageType = 0;
	private String mServerPort = "0";
	private String mServerIpPreference = null;
	private String mUsername = null;
	private String mPassword = null;
	private String mIpAddress = null;
	private String mCurrentSessionID = null;
	private String mCurrentPublisherID = null;
	private Long mRenewIntervall = 0L;
	private PublishRequest mReguestParamsPublish = null;
	private Handler mMsgHandler = null;
	private PermanentConnectionService.LocalBinder permServiceBinder;
	private RenewConnectionService.LocalBinder renewServiceBinder;

	// private TimeCountingService.LocalBinder timeCountingServiceBinder;

	/**
	 * constructor
	 * 
	 * @param serviceType
	 *            type of local-service for which the object holds the
	 *            parameters
	 * @param prefs
	 *            preferences-object containing application-settings
	 * @param ipAddress
	 *            ip-address of the client
	 * @param messageType
	 *            type of message {@link MessageHandler}
	 * @param reguestParams
	 * 
	 * @param msgHandler
	 *            callback-handler for local-service
	 */
	public LocalServiceParameters(int serviceType, PreferencesValues prefs, String ipAddress, byte messageType,
			PublishRequest reguestParams, Handler msgHandler) {
		this.mSSLConnectionType = prefs.isAllowUnsafeSSLPreference();
		this.mServerIpPreference = prefs.getServerIpPreference();
		//this.mServerPort = new Integer(prefs.getServerPortPreference()).intValue();
		this.mServerPort = prefs.getServerPortPreference();
		this.mUsername = prefs.getUsernamePreference();
		this.mPassword = prefs.getPasswordPreference();
		this.mIpAddress = ipAddress;
		this.mMessageType = messageType;
		this.mRenewIntervall = Long.valueOf(prefs.getRenewIntervalPreference()).longValue();
		this.mReguestParamsPublish = reguestParams;
		this.mMsgHandler = msgHandler;

		switch (serviceType) {
		case SERVICE_BINDER_TYPE_PERMANENT_CONNECTION_SERVICE:
			this.permServiceBinder = MainActivity.sBoundPermConnService;
			break;
		case SERVICE_BINDER_TYPE_RENEW_CONNECTION_SERVICE:
			this.renewServiceBinder = MainActivity.sBoundRenewConnService;
			break;
		}
	}

	/**
	 * @return the mSSLConnectionType
	 */
	public boolean ismSSLConnectionType() {
		return mSSLConnectionType;
	}

	/**
	 * @param sslConnectionType
	 *            the mSSLConnectionType to set
	 */
	public void setmSSLConnectionType(boolean sslConnectionType) {
		this.mSSLConnectionType = sslConnectionType;
	}

	/**
	 * @return the mMessageType
	 */
	public byte getmMessageType() {
		return mMessageType;
	}

	/**
	 * @param messageType
	 *            the mMessageType to set
	 */
	public void setmMessageType(byte messageType) {
		this.mMessageType = messageType;
	}

	/**
	 * @return the mRenewIntervalPreference
	 */
	public Long getmRenewIntervalPreference() {
		return mRenewIntervall;
	}

	/**
	 * @param renewIntervalPreference
	 *            the mRenewIntervalPreference to set
	 */
	public void setmRenewIntervalPreference(Long renewIntervalPreference) {
		this.mRenewIntervall = renewIntervalPreference;
	}

	/**
	 * @return the mServerPort
	 */
	public String getmServerPort() {
		return mServerPort;
	}

	/**
	 * @param serverPort
	 *            the mServerPort to set
	 */
	public void setmServerPort(String serverPort) {
		this.mServerPort = serverPort;
	}

	/**
	 * @return the mServerIpPreference
	 */
	public String getmServerIpPreference() {
		return mServerIpPreference;
	}

	/**
	 * @param serverIpPreference
	 *            the mServerIpPreference to set
	 */
	public void setmServerIpPreference(String serverIpPreference) {
		this.mServerIpPreference = serverIpPreference;
	}

	/**
	 * @return the mUsername
	 */
	public String getmUsername() {
		return mUsername;
	}

	/**
	 * @param username
	 *            the mUsername to set
	 */
	public void setmUsername(String username) {
		this.mUsername = username;
	}

	/**
	 * @return the mPassword
	 */
	public String getmPassword() {
		return mPassword;
	}

	/**
	 * @param password
	 *            the mPassword to set
	 */
	public void setmPassword(String password) {
		this.mPassword = password;
	}

	/**
	 * @return the mIpAddress
	 */
	public String getmIpAddress() {
		return mIpAddress;
	}

	/**
	 * @param ipAddress
	 *            the mIpAddress to set
	 */
	public void setmIpAddress(String ipAddress) {
		this.mIpAddress = ipAddress;
	}

	/**
	 * @return the mCurrentSessionID
	 */
	public String getmCurrentSessionID() {
		return mCurrentSessionID;
	}

	/**
	 * @param currentSessionID
	 *            the mCurrentSessionID to set
	 */
	public void setmCurrentSessionID(String currentSessionID) {
		this.mCurrentSessionID = currentSessionID;
	}

	/**
	 * @return the mCurrentPublisherID
	 */
	public String getmCurrentPublisherID() {
		return mCurrentPublisherID;
	}

	/**
	 * @param currentPublisherID
	 *            the mCurrentPublisherID to set
	 */
	public void setmCurrentPublisherID(String currentPublisherID) {
		this.mCurrentPublisherID = currentPublisherID;
	}

	/**
	 * @return the mReguestParamsPublish
	 */
	public PublishRequest getmReguestParamsPublish() {
		return mReguestParamsPublish;
	}

	/**
	 * @param reguestParamsPublish
	 *            the mReguestParamsPublish to set
	 */
	public void setmReguestParamsPublish(PublishRequest reguestParamsPublish) {
		this.mReguestParamsPublish = reguestParamsPublish;
	}

	/**
	 * @return the mMsgHandler
	 */
	public Handler getmMsgHandler() {
		return mMsgHandler;
	}

	/**
	 * @param msgHandler
	 *            the mMsgHandler to set
	 */
	public void setmMsgHandler(Handler msgHandler) {
		this.mMsgHandler = msgHandler;
	}

	/**
	 * @return the mRenewIntervall
	 */
	public Long getmRenewIntervall() {
		return mRenewIntervall;
	}

	/**
	 * @param renewIntervall
	 *            the mRenewIntervall to set
	 */
	public void setmRenewIntervall(Long renewIntervall) {
		this.mRenewIntervall = renewIntervall;
	}

	/**
	 * @return the permServiceBinder
	 */
	public PermanentConnectionService.LocalBinder getPermServiceBinder() {
		return permServiceBinder;
	}

	/**
	 * @param permServiceBinder
	 *            the permServiceBinder to set
	 */
	public void setPermServiceBinder(PermanentConnectionService.LocalBinder permServiceBinder) {
		this.permServiceBinder = permServiceBinder;
	}

	/**
	 * @return the renewServiceBinder
	 */
	public RenewConnectionService.LocalBinder getRenewServiceBinder() {
		return renewServiceBinder;
	}

	/**
	 * @param renewServiceBinder
	 *            the renewServiceBinder to set
	 */
	public void setRenewServiceBinder(RenewConnectionService.LocalBinder renewServiceBinder) {
		this.renewServiceBinder = renewServiceBinder;
	}
}
