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

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * abstract base-class for all PhoneProperties-classes
 * due to differences in the android-sdk, the actual implementation of
 * this class is loaded at runtime via reflections
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, DECOIT GmbH
 */
public abstract class PhoneProperties {

    /* application context object */
    public Context mAppContext;
    
    /* telephony manager */
    public TelephonyManager mTelephonyMgr;
   
    
    /**
     * get device-manufacturer
     * 
     * @return device manufacturer as string
     */
    public abstract String getManufacturer(); 

    /**
     * get device-IMEI-number
     * 
     * @return device IMEI number as string
     */
    public abstract String getIMEI();

    /**
     * get device-IMSI-number
     * 
     * @return device IMSI number as string
     */
    public abstract String getIMSI();

    /**
     * get device-branding
     * 
     * @return device-branding-string
     */
    public abstract String getBranding();

    /**
     * get device-phone-number
     * 
     * @return device phone number as string
     */
    public abstract String getPhonenumber();

    /**
     * get device-model-string
     * 
     * @return device-model-string
     */
    public abstract String getModel();

    /**
     * get firmware-version
     * 
     * http://groups.google.com/group/android-beginners/browse_thread/thread/bafc511526a185c2?pli=1
     * 
     * @return firmware-version-string
     */
    public abstract String getFirmwareVersion();

    /**
     * get device-bluetooth state
     * 
     * @return current state of deivces bluetooth-interface
     */
    public abstract String getBluetoothActiveStatusString();

    /**
     * check current state of device-bluetooth-interface
     * 
     * @return true if bluetooth-interface is enabled, otherwise false
     */
    public abstract boolean isBluetoothActive();

    /**
     * check current microphone state
     * 
     * @return
     */
    public abstract String getMicrophoneActiveString();

    /**
     * check current state of device-microphone-interface
     * 
     * @return true if microphone is currently muted, otherwise false
     */
    public abstract boolean isMicrophoneMute();
    
    /**
     * get current baseband version
     * 
     * @return current baseband-version
     */
    public abstract String getBasebandVersion();
    
    /**
     * get current baseband version
     * 
     * @return current baseband-version
     */
    public abstract String getBuildNumber();
}
