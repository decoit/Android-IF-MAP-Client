/* 
 * PhoneProperties.java        0.1.6 07/02/12
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
package de.simu.decoit.android.decomap.device.phone;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * class for reading out several phone-properties on
 * devices which runs on sdk-version prior to android
 * ice cream sandwich 
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Markus Schölzel, Decoit GmbH
 */
public class PhonePropertiesLegacy extends PhoneProperties{

    /**
     * constructor
     */
    public PhonePropertiesLegacy(Context appContext) {
        mAppContext = appContext;
        mTelephonyMgr = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
    }
    
    /**
     * get device-manufacturer
     * 
     * @return device manufacturer as string
     */
    @Override
    public String getManufacturer() {
        return Build.MANUFACTURER.toString();
    }

    /**
     * get device-IMEI-number
     * 
     * @return device IMEI number as string
     */
    @Override
    public String getIMEI() {
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * get device-IMSI-number
     * 
     * @return device IMSI number as string
     */
    @Override
    public String getIMSI() {
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * get device-branding
     * 
     * @return device-branding-string
     */
    @Override
    public String getBranding() {
        return Build.BRAND.toString();
    }

    /**
     * get device-phone-number
     * 
     * @return device phone number as string
     */
    @Override
    public String getPhonenumber() {
        if (mTelephonyMgr.getLine1Number() != null && mTelephonyMgr.getLine1Number().length() > 0) {
            return mTelephonyMgr.getLine1Number().toString();
        }
        return null;
    }

    /**
     * get device-model-string
     * 
     * @return device-model-string
     */
    @Override
    public String getModel() {
        return Build.MODEL.toString();
    }

    /**
     * get firmware-version
     * 
     * http://groups.google.com/group/android-beginners/browse_thread/thread/bafc511526a185c2?pli=1
     * 
     * @return firmware-version-string
     */
    @Override
    public String getFirmwareVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * get device-bluetooth state
     * 
     * @return current state of deivces bluetooth-interface
     */
    @Override
    public String getBluetoothActiveStatusString() {
        if (isBluetoothActive()) {
            return "enabled";
        } else {
            return "deactivated";
        }
    }

    /**
     * check current state of device-bluetooth-interface
     * 
     * @return true if bluetooth-interface is enabled, otherwise false
     */
    @Override
    public boolean isBluetoothActive() {
        BluetoothAdapter bt = null;
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            bt = BluetoothAdapter.getDefaultAdapter();
            if (bt.isEnabled()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * check current microphone state
     * 
     * @return
     */
    @Override
    public String getMicrophoneActiveString() {
        if (isMicrophoneMute()) {
            return "muted";
        } else {
            return "not muted";
        }
    }

    /**
     * check current state of device-microphone-interface
     * 
     * @return true if microphone is currently muted, otherwise false
     */
    @Override
    public boolean isMicrophoneMute() {
        AudioManager audioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMicrophoneMute()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * get current baseband version
     * 
     * @return current baseband-version
     */
    @SuppressWarnings("deprecation")
    @Override
    public String getBasebandVersion(){
        return Build.RADIO;
    }
    
    /**
     * get current baseband version
     * 
     * @return current baseband-version
     */
    @Override
    public String getBuildNumber(){
        return Build.VERSION.INCREMENTAL;
    }

}
