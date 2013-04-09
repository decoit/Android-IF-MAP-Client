/*
 * PreferencesValues.java       0.1.6 07/02/12 
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

package de.esukom.decoit.android.ifmapclient.preferences;

/**
 * Object for holding Preferences
 * 
 * @author  Dennis Dunekacke, Decoit GmbH
 * @version 0.1.6
 */
public class PreferencesValues {

    // "lock" parts of the preferences menu after a new session
    // has been established and "unlock" it when a session has ended.
    // declared static so it can be passed to SetupActivity.
    // connection type is a special case: it can only be changed
    // once. after a session has been established it cannot be
    // changed until the client is restarted!
    public static boolean sLockConnectionPreferences = false;
    public static boolean sLockPreferences = false;
    public static boolean sLockLocationTrackingOptions = false;

    // location tracking
    public static String sLocationTrackingType = null;
    public static boolean sEnableLocationTracking = false;
    
    // auto update
    public static boolean sAutoUpdate =  false;
    private long mUpdateInterval = 60000;
    

    // application-settings
    private boolean mUseNonConformMetadata;
    private boolean mDontSendApplicationsInfos;
    private boolean mDontSendGoogleApps;
    private boolean mAutostart;
    private boolean mAutoconnect;

    // server-settings
    private String mServerIpPreference;
    private String mServerPortPreference;

    // user-settings
    private boolean mUseBasicAuth;
    private String mUsernamePreference;
    private String mPasswordPreference;

    // connection-settings
    private boolean mAllowUnsafeSSLPreference;
    private boolean mIsPermantConnection;
    private Long mRenewIntervalPreference;
    private long mRenewRequestMinInterval = 10000; // default minimum

    // logging-settings
    public static boolean sApplicationFileLogging = false;
    private boolean mEnableNewAndEndSessionLog;
    private boolean mEnableSubscribe;
    private boolean mEnablePollLog;
    private boolean mEnableLocationTrackingLog;
    private boolean mEnablePublishCharacteristicsLog;
    private boolean mEnableErrorMessageLog;
    private boolean mEnableInvalideResponseLog;
    private boolean mEnableRenewRequestLog;

    /**
     * @return the mUseNonConformMetadata
     */
    public boolean isUseNonConformMetadata() {
        return mUseNonConformMetadata;
    }

    /**
     * @param mUseNonConformMetadata
     *            the mUseNonConformMetadata to set
     */
    public void setUseNonConformMetadata(boolean mUseNonConformMetadata) {
        this.mUseNonConformMetadata = mUseNonConformMetadata;
    }

    /**
     * @return the mAutostart
     */
    public boolean ismAutostart() {
        return mAutostart;
    }

    /**
     * @param mAutostart
     *            the mAutostart to set
     */
    public void setmAutostart(boolean mAutostart) {
        this.mAutostart = mAutostart;
    }

    /**
     * @return the mServerIpPreference
     */
    public String getServerIpPreference() {
        return mServerIpPreference;
    }

    /**
     * @param mServerIpPreference
     *            the mServerIpPreference to set
     */
    public void setServerIpPreference(String mServerIpPreference) {
        this.mServerIpPreference = mServerIpPreference;
    }

    /**
     * @return the mServerPortPreference
     */
    public String getServerPortPreference() {
        return mServerPortPreference;
    }

    /**
     * @param mServerPortPreference
     *            the mServerPortPreference to set
     */
    public void setServerPortPreference(String mServerPortPreference) {
        this.mServerPortPreference = mServerPortPreference;
    }

    /**
     * @return the mUsernamePreference
     */
    public String getUsernamePreference() {
        return mUsernamePreference;
    }

    /**
     * @param mUsernamePreference
     *            the mUsernamePreference to set
     */
    public void setUsernamePreference(String mUsernamePreference) {
        this.mUsernamePreference = mUsernamePreference;
    }

    /**
     * @return the mPasswordPreference
     */
    public String getPasswordPreference() {
        return mPasswordPreference;
    }

    /**
     * @param mPasswordPreference
     *            the mPasswordPreference to set
     */
    public void setPasswordPreference(String mPasswordPreference) {
        this.mPasswordPreference = mPasswordPreference;
    }

    /**
     * @return the mAllowUnsafeSSLPreference
     */
    public boolean isAllowUnsafeSSLPreference() {
        return mAllowUnsafeSSLPreference;
    }

    /**
     * @param mAllowUnsafeSSLPreference
     *            the mAllowUnsafeSSLPreference to set
     */
    public void setAllowUnsafeSSLPreference(boolean mAllowUnsafeSSLPreference) {
        this.mAllowUnsafeSSLPreference = mAllowUnsafeSSLPreference;
    }

    /**
     * @return the mIsPermantConnection
     */
    public boolean isIsPermantConnection() {
        return mIsPermantConnection;
    }

    /**
     * @param mIsPermantConnection
     *            the mIsPermantConnection to set
     */
    public void setIsPermantConnection(boolean mIsPermantConnection) {
        this.mIsPermantConnection = mIsPermantConnection;
    }

    /**
     * @return the mRenewIntervalPreference
     */
    public Long getRenewIntervalPreference() {
        return mRenewIntervalPreference;
    }

    /**
     * @param mRenewIntervalPreference
     *            the mRenewIntervalPreference to set
     */
    public void setRenewIntervalPreference(Long mRenewIntervalPreference) {
        this.mRenewIntervalPreference = mRenewIntervalPreference;
    }

    /**
     * @return the mEnableNewAndEndSessionLog
     */
    public boolean isEnableNewAndEndSessionLog() {
        return mEnableNewAndEndSessionLog;
    }

    /**
     * @param mEnableNewAndEndSessionLog
     *            the mEnableNewAndEndSessionLog to set
     */
    public void setEnableNewAndEndSessionLog(boolean mEnableNewAndEndSessionLog) {
        this.mEnableNewAndEndSessionLog = mEnableNewAndEndSessionLog;
    }

    /**
     * @return the mEnableSubscribe
     */
    public boolean isEnableSubscribe() {
        return mEnableSubscribe;
    }

    /**
     * @param mEnableSubscribe
     *            the mEnableSubscribe to set
     */
    public void setEnableSubscribe(boolean mEnableSubscribe) {
        this.mEnableSubscribe = mEnableSubscribe;
    }

    /**
     * @return the mEnablePollLog
     */
    public boolean isEnablePollLog() {
        return mEnablePollLog;
    }

    /**
     * @param mEnablePollLog
     *            the mEnablePollLog to set
     */
    public void setEnablePollLog(boolean mEnablePollLog) {
        this.mEnablePollLog = mEnablePollLog;
    }

    /**
     * @return the mEnableLocationTrackingLog
     */
    public boolean isEnableLocationTrackingLog() {
        return mEnableLocationTrackingLog;
    }

    /**
     * @param mEnableLocationTrackingLog
     *            the mEnableLocationTrackingLog to set
     */
    public void setEnableLocationTrackingLog(boolean mEnableLocationTrackingLog) {
        this.mEnableLocationTrackingLog = mEnableLocationTrackingLog;
    }

    /**
     * @return the mEnablePublishCharacteristicsLog
     */
    public boolean isEnablePublishCharacteristicsLog() {
        return mEnablePublishCharacteristicsLog;
    }

    /**
     * @param mEnablePublishCharacteristicsLog
     *            the mEnablePublishCharacteristicsLog to set
     */
    public void setEnablePublishCharacteristicsLog(boolean mEnablePublishCharacteristicsLog) {
        this.mEnablePublishCharacteristicsLog = mEnablePublishCharacteristicsLog;
    }

    /**
     * @return the mEnableErrorMessageLog
     */
    public boolean isEnableErrorMessageLog() {
        return mEnableErrorMessageLog;
    }

    /**
     * @param mEnableErrorMessageLog
     *            the mEnableErrorMessageLog to set
     */
    public void setEnableErrorMessageLog(boolean mEnableErrorMessageLog) {
        this.mEnableErrorMessageLog = mEnableErrorMessageLog;
    }

    /**
     * @return the mEnableInvalideResponseLog
     */
    public boolean isEnableInvalideResponseLog() {
        return mEnableInvalideResponseLog;
    }

    /**
     * @param mEnableInvalideResponseLog
     *            the mEnableInvalideResponseLog to set
     */
    public void setEnableInvalideResponseLog(boolean mEnableInvalideResponseLog) {
        this.mEnableInvalideResponseLog = mEnableInvalideResponseLog;
    }

    /**
     * @return the mEnableRenewRequestLog
     */
    public boolean isEnableRenewRequestLog() {
        return mEnableRenewRequestLog;
    }

    /**
     * @param mEnableRenewRequestLog
     *            the mEnableRenewRequestLog to set
     */
    public void setEnableRenewRequestLog(boolean mEnableRenewRequestLog) {
        this.mEnableRenewRequestLog = mEnableRenewRequestLog;
    }

    /**
     * @return the mRenewRequestMinInterval
     */
    public long getRenewRequestMinInterval() {
        return mRenewRequestMinInterval;
    }

    /**
     * @return the mUpdateInterval
     */
    public long getmUpdateInterval() {
        return mUpdateInterval;
    }

    /**
     * @param mUpdateInterval
     *            the mUpdateInterval to set
     */
    public void setmUpdateInterval(long mLocationTrackingInterval) {
        this.mUpdateInterval = mLocationTrackingInterval;
    }

    /**
     * @return the mUseBasicAuth
     */
    public boolean isUseBasicAuth() {
        return mUseBasicAuth;
    }

    /**
     * @param mUseBasicAuth
     *            the mUseBasicAuth to set
     */
    public void setIsUseBasicAuth(boolean mUseBasicAuth) {
        this.mUseBasicAuth = mUseBasicAuth;
    }

    /**
     * @return the mAutoconnect
     */
    public boolean ismAutoconnect() {
        return mAutoconnect;
    }

    /**
     * @param mAutoconnect
     *            the mAutoconnect to set
     */
    public void setmAutoconnect(boolean mAutoconnect) {
        this.mAutoconnect = mAutoconnect;
    }

    /**
     * @return the mSendApplicationsInfos
     */
    public boolean ismSendApplicationsInfos() {
        return mDontSendApplicationsInfos;
    }

    /**
     * @param mSendApplicationsInfos
     *            the mSendApplicationsInfos to set
     */
    public void setmSendApplicationsInfos(boolean mSendApplicationsInfos) {
        this.mDontSendApplicationsInfos = mSendApplicationsInfos;
    }

    /**
     * @return the mDontSendGoogleApps
     */
    public boolean ismDontSendGoogleApps() {
        return mDontSendGoogleApps;
    }

    /**
     * @param mDontSendGoogleApps the mDontSendGoogleApps to set
     */
    public void setmDontSendGoogleApps(boolean mDontSendGoogleApps) {
        this.mDontSendGoogleApps = mDontSendGoogleApps;
    }
}
