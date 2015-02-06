/* 
 * MessageParametersGenerator.java        0.1.6 07/02/12
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import android.os.Build;
import android.util.Log;
import de.esukom.decoit.android.ifmapclient.activities.MainActivity;
import de.esukom.decoit.android.ifmapclient.device.DeviceProperties;
import de.esukom.decoit.android.ifmapclient.device.application.ApplicationListEntry;
import de.esukom.decoit.android.ifmapclient.device.application.Permission;
import de.esukom.decoit.android.ifmapclient.device.system.SystemProperties;
import de.esukom.decoit.android.ifmapclient.observer.battery.BatteryReceiver;
import de.esukom.decoit.android.ifmapclient.observer.camera.CameraReceiver;
import de.esukom.decoit.android.ifmapclient.observer.sms.SMSObserver;
import de.esukom.decoit.android.ifmapclient.observer.sms.SMSObserver.SmsInfos;
import de.esukom.decoit.android.ifmapclient.preferences.PreferencesValues;
import de.esukom.decoit.android.ifmapclient.util.CryptoUtil;
import de.esukom.decoit.android.ifmapclient.util.DateUtil;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;
import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.binding.IfmapStrings;
import de.fhhannover.inform.trust.ifmapj.identifier.Device;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifier;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj.identifier.Identity;
import de.fhhannover.inform.trust.ifmapj.identifier.IdentityType;
import de.fhhannover.inform.trust.ifmapj.identifier.IpAddress;
import de.fhhannover.inform.trust.ifmapj.messages.MetadataLifetime;
import de.fhhannover.inform.trust.ifmapj.messages.PublishDelete;
import de.fhhannover.inform.trust.ifmapj.messages.PublishRequest;
import de.fhhannover.inform.trust.ifmapj.messages.PublishUpdate;
import de.fhhannover.inform.trust.ifmapj.messages.Requests;
import de.fhhannover.inform.trust.ifmapj.metadata.Cardinality;
import de.fhhannover.inform.trust.ifmapj.metadata.LocationInformation;
import de.fhhannover.inform.trust.ifmapj.metadata.StandardIfmapMetadataFactory;

/**
 * A generic class that generates message-parameters for the IfmapJ-lib
 * 
 * @author Marcel Jahnke, DECOIT GmbH
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Markus Schölzel, Decoit GmbH
 * @version 0.1.6
 * @param <T>
 */
public class MessageParametersGenerator<T> {

    // some predefined values
    final static String OTHER_TYPE_DEFINITION = "32939:category";
    final static String NAMESPACE = "http://www.esukom.de/2012/ifmap-metadata/1";
    final static String NAMESPACE_PREFIX = "esukom";
    final static String QUANT = "quantitive";
    final static String ARBIT = "arbitrary";
    final static String QUALI = "qualified";

    // flag detecting that a previous publish-request has been send
    // (to handle deletion of previous send data)
    public static boolean sInitialLocationWasSend = false;
    public static boolean sInitialDevCharWasSend = false;

    // create device identifier
    Device deviceIdentifier;

    // location information
    private String mLastLatitude;
    private String mLastLongitude;

    // request-type
    private T mRequest = null;

    // document-builder
    private DocumentBuilderFactory mDocumentBuilderFactory;
    private DocumentBuilder mDocumentBuilder;

    // category-identities
    private Identity mPhoneCat;
    private Identity mPhoneSystemCat;
    private Identity mPhoneDeviceCat;
    private Identity mPhoneAndroidCat;
    private Identity mPhoneOsCat;
    private Identity mPhoneSensorCat;
    private Identity mPhoneCommunicationCat;
    private Identity mPhoneSMSCat;
    private Identity mPhoneIpCat;
    private Identity mPhoneBatteryCat;
    private Identity mPhoneMemoryCat;

    // category-metadata
    private Document mDeviceCategory;
    private Document mSubCategoryOf;

    // values for features that need to be republished because they can change at runtime
    private String mLastBatStat;
    private String mLastCpuLoad;
    private String mLastFreeRam;
    private String mLastProcCount;
    private String mLastSmsRecCount;
    private String mLastSmsSentCount;
    private String mLastSmsSentDate;
    private String mLastRxBytesTotal;
    private String mLastTxBytesTotal;
    private ArrayList<ApplicationListEntry> mLastAppList;
    private String mLastIpAddress;
    private boolean mLastCameraIsUsed;

    /**
     * constructor
     */
    public MessageParametersGenerator() {
        // create document-builder from factory
        mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * generate request-parameters for use with ssrc-connection
     * 
     * @param messageType
     *            type of message to generate
     * @param deviceProperties
     *            DeviceProperties object that contains the properties of the device
     * 
     * @return A PublishRequest or SubscribeRequest object message
     */
    @SuppressWarnings("unchecked")
    public T generateSRCRequestParamteres(byte messageType, DeviceProperties deviceProperties, boolean useNonConformMetadata,
            boolean dontSendAppInfos, boolean dontSendGoogleApps) {
        
    	// this hack is needed in order to match this data to our training data
        // initialize device identifier, no real salt useful for us
    	//String imei = deviceProperties.getPhoneProperties().getIMEI();
        //deviceIdentifier = Identifiers.createDev(anonymize(imei));
        
        // changed value of device-identifier to mac-address as value
        // (changes due to demonstrator - usecase3)
        deviceIdentifier = Identifiers.createDev(deviceProperties.getSystemProperties().getMAC());

        // create metadata-factory
        StandardIfmapMetadataFactory metadataFactory = IfmapJ.createStandardMetadataFactory();

        // Current time in specified format according to ifmap-spec
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssZ");
        Date nowTime = new java.util.Date();

        // create ip-address identifier
        IpAddress ipAddress = Identifiers.createIp4(deviceProperties.getSystemProperties().getLocalIpAddress());

        switch (messageType) {

        /* no parameters needed for new session request */
        case MessageHandler.MSG_TYPE_REQUEST_NEWSESSION:
            break;

        /* no parameters needed for renew session */
        case MessageHandler.MSG_TYPE_REQUEST_RENEWSESSION:
            break;

        /* end session request */
        case MessageHandler.MSG_TYPE_REQUEST_ENDSESSION:
            sInitialDevCharWasSend = false;
            sInitialLocationWasSend = false;
            restLastValues();
            break;

        /* send location data request */
        case MessageHandler.MSG_TYPE_METADATA_UPDATE:
            PublishRequest publishRequest = Requests.createPublishReq();
            PublishUpdate publishLocationUpdate = Requests.createPublishUpdate();

            
            // append device-characteristics-metadata
            createDeviceCharacteristicsMetadataGraph(publishRequest, deviceIdentifier, deviceProperties, simpledateformat, nowTime,
                   ipAddress);

            // also append esukom-specific data if related option is set
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (useNonConformMetadata) {
                    createEsukomSpecificDeviceCharacteristicsMetadataGraph(publishRequest, deviceIdentifier, simpledateformat, nowTime,
                            deviceProperties, dontSendAppInfos, dontSendGoogleApps);
                }
            }

            if (PreferencesValues.sEnableLocationTracking &&(MainActivity.sLatitude != null && MainActivity.sLongitude != null)) {
                // if a previous location has been send, add publish-delete
                if (sInitialLocationWasSend) {
                    PublishDelete deleteLastLocation = Requests.createPublishDelete();
                    deleteLastLocation.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX, IfmapStrings.STD_METADATA_NS_URI);
                    String string_filter = "meta:location[" + "@ifmap-publisher-id=\'" + MainActivity.sCurrentPublisherId + "\']";
                    deleteLastLocation.setFilter(string_filter);
                    deleteLastLocation.setIdentifier1(ipAddress);
                    publishRequest.addPublishElement(deleteLastLocation);
                }
    
                // build publish-location-update-request
               publishLocationUpdate.setIdentifier1(ipAddress);
                List<LocationInformation> locationInfoList = new ArrayList<LocationInformation>();
                LocationInformation locationInformation = new LocationInformation(PreferencesValues.sLocationTrackingType,
                        MainActivity.sLatitude + " " + MainActivity.sLongitude);
                locationInfoList.add(locationInformation);
                Document location = metadataFactory.createLocation(locationInfoList, simpledateformat.format(nowTime),
                        MainActivity.sCurrentPublisherId);
                publishLocationUpdate.addMetadata(location);
                publishRequest.addPublishElement(publishLocationUpdate);
            }
            
            sInitialDevCharWasSend = true;
            sInitialLocationWasSend = true;
            mRequest = (T) publishRequest;
            break;

        /* send device characteristics data request */
        case MessageHandler.MSG_TYPE_PUBLISH_CHARACTERISTICS:
            publishRequest = Requests.createPublishReq();

            // append device-characteristics-metadata
            createDeviceCharacteristicsMetadataGraph(publishRequest, deviceIdentifier, deviceProperties, simpledateformat, nowTime,
                   ipAddress);

            // also append esukom-specific data if related option is set
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (useNonConformMetadata) {
                    createEsukomSpecificDeviceCharacteristicsMetadataGraph(publishRequest, deviceIdentifier, simpledateformat, nowTime,
                            deviceProperties, dontSendAppInfos, dontSendGoogleApps);
                }
            }

            sInitialDevCharWasSend = true;
            mRequest = (T) publishRequest;
            break;

        /* if all fails */
        default:
            Toolbox.logTxt(this.getClass().getName(),
                    "Error while building publish request...Messagetype for publish-request could not be found!");
            break;
        }

        return mRequest;
    }

    /**
     * reset all mLast* values
     */
    private void restLastValues() {
    	mLastLatitude = null;
    	mLastAppList = null;
    	mLastBatStat = null;
    	mLastCpuLoad = null;
    	mLastFreeRam = null;
    	mLastIpAddress = null;
    	mLastLongitude = null;
    	mLastProcCount = null;
    	mLastRxBytesTotal = null;
    	mLastRxBytesTotal = null;
    	mLastSmsRecCount = null;
    	mLastSmsSentCount = null;
    	mLastSmsSentDate = null;
    	mLastTxBytesTotal = null;
	}

	/**
     * create device-characteristics-metadata and append it to passes in publish-request-object
     * 
     * @param publishRequest
     *            publish-request to send
     * @param deviceIdentifier
     *            root-identifier for the device
     * @param deviceProperties
     *            device-properties object containing device-related informations to be appended to the metadata-graph
     * @param simpledateformat
     *            date-formatter
     * @param nowTime
     *            current time
     * @param ipAddress
     *            IP-Identifier
     */
    public void createDeviceCharacteristicsMetadataGraph(PublishRequest publishRequest, Device deviceIdentifier,
            DeviceProperties deviceProperties, SimpleDateFormat simpledateformat, Date nowTime, IpAddress ipAddress) {
        Document document = createStdSingleElementDocument("device-characteristic", Cardinality.multiValue);
        Element root = (Element) document.getFirstChild();

        // append standard conform meta-data to device-characteristics-metadata
        // the order is important(!) and must match the order from the spec.
        appendTextElementIfNotNull(document, root, "manufacturer", deviceProperties.getPhoneProperties().getManufacturer());
        appendTextElementIfNotNull(document, root, "model", deviceProperties.getPhoneProperties().getManufacturer());
        appendTextElementIfNotNull(document, root, "os", "Android");
        appendTextElementIfNotNull(document, root, "os-version", deviceProperties.getPhoneProperties().getFirmwareVersion());
        appendTextElementIfNotNull(document, root, "device-type", "remote-access-device");
        appendTextElementIfNotNull(document, root, "discovered-time", simpledateformat.format(nowTime));
        appendTextElementIfNotNull(document, root, "discoverer-id", MainActivity.sCurrentPublisherId);
        appendTextElementIfNotNull(document, root, "discovery-method", "scan");

        // add device and ip-identifier to update, append meta-data
        addToUpdateRequest(publishRequest, ipAddress, deviceIdentifier, document, MetadataLifetime.session, true);
    }

    /**
     * create metadata-graph for esukom-specific metadata
     * 
     * @param publishRequest
     *            publish-request to send
     * @param deviceIdent
     *            root-identifier for the device
     * @param simpledateformat
     *            date-formatter
     * @param nowTime
     *            current time
     * @param devProps
     *            device-properties object containing device-related informations to be appended to the metadata-graph
     */
    public void createEsukomSpecificDeviceCharacteristicsMetadataGraph(PublishRequest publishRequest, Device deviceIdent,
            SimpleDateFormat simpledateformat, Date nowTime, DeviceProperties devProps, boolean dontSendAppInfos, boolean dontSendGoogleApps) {

        // feature-document and current time
        Document fe = null;
        String time = simpledateformat.format(nowTime);
        // hack to include colon in timezone
        time = DateUtil.getTimestampXsd(nowTime.getTime());

        // republish informations-flag
        boolean locationChanged = false;
        if (PreferencesValues.sEnableLocationTracking && (MainActivity.sLatitude != null && MainActivity.sLongitude != null)) {
            if (mLastLatitude != MainActivity.sLatitude || mLastLongitude != MainActivity.sLongitude) {
                mLastLatitude = MainActivity.sLatitude;
                mLastLongitude = MainActivity.sLongitude;
                locationChanged = true;
            }
        }

        // only send in initial publish
        if (!sInitialDevCharWasSend) {
            // categories
            mPhoneCat = createCategory("smartphone", deviceIdentifier.getName());
            mPhoneSystemCat = createCategory("smartphone.system", deviceIdentifier.getName());
            mPhoneDeviceCat = createCategory("smartphone.device", deviceIdentifier.getName());
            mPhoneAndroidCat = createCategory("smartphone.android", deviceIdentifier.getName());
            mPhoneOsCat = createCategory("smartphone.android.os", deviceIdentifier.getName());
            mPhoneSensorCat = createCategory("smartphone.sensor", deviceIdentifier.getName());
            mPhoneCommunicationCat = createCategory("smartphone.communication", deviceIdentifier.getName());
            mPhoneSMSCat = createCategory("smartphone.communication.sms", deviceIdentifier.getName());
            mPhoneIpCat = createCategory("smartphone.communication.ip", deviceIdentifier.getName());
            mPhoneBatteryCat = createCategory("smartphone.system.battery", deviceIdentifier.getName());
            mPhoneMemoryCat = createCategory("smartphone.system.memory", deviceIdentifier.getName());
            mDeviceCategory = createCategoryLink("device-category");
            mSubCategoryOf = createCategoryLink("subcategory-of");

            addToUpdateRequest(publishRequest, deviceIdent, mPhoneCat, mDeviceCategory, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCat, mPhoneSystemCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCat, mPhoneDeviceCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCat, mPhoneAndroidCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneAndroidCat, mPhoneOsCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCat, mPhoneSensorCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCat, mPhoneCommunicationCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCommunicationCat, mPhoneSMSCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneCommunicationCat, mPhoneIpCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneSystemCat, mPhoneBatteryCat, mSubCategoryOf, MetadataLifetime.session, false);
            addToUpdateRequest(publishRequest, mPhoneSystemCat, mPhoneMemoryCat, mSubCategoryOf, MetadataLifetime.session, false);
        }

        // the following features are static and will not change during runtime
        // only republish them if the current location has been changed
        if (!sInitialDevCharWasSend || locationChanged) {
            fe = createFeature("MAC", time, devProps.getSystemProperties().getMAC(), QUALI);
            addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("Manufacturer", time, devProps.getPhoneProperties().getManufacturer(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("Branding", time, devProps.getPhoneProperties().getBranding(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("IMEI", time, anonymize(devProps.getPhoneProperties().getIMEI()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("IMSI", time, anonymize(devProps.getPhoneProperties().getIMSI()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("KernelVersion", time, devProps.getSystemProperties().getKernelVersion(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneOsCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("FirmwareVersion", time, devProps.getPhoneProperties().getFirmwareVersion(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneOsCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("BasebandVersion", time, devProps.getPhoneProperties().getBasebandVersion(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneOsCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("BuildNumber", time, devProps.getPhoneProperties().getBuildNumber(), ARBIT);
            addToUpdateRequest(publishRequest, mPhoneOsCat, null, fe, MetadataLifetime.session, true);
        }

        // features that can change during runtime
        
        // Battery
        String mCurBatStat = BatteryReceiver.sCurrentBatteryLevel;
        if (!mCurBatStat.equals(mLastBatStat) || locationChanged) {
            fe = createFeature("Level", time, mCurBatStat, QUANT);
            addToUpdateRequest(publishRequest, mPhoneBatteryCat, null, fe, MetadataLifetime.session, true);
            mLastBatStat = mCurBatStat;
        }
        
        // IP
        String currentIpAddress = devProps.getSystemProperties().getLocalIpAddress();
        if (!currentIpAddress.equals(mLastIpAddress) || locationChanged) {
        	fe = createFeature("IpAddress", time, currentIpAddress, QUALI);
        	addToUpdateRequest(publishRequest, mPhoneDeviceCat, null, fe, MetadataLifetime.session, true);
        	mLastIpAddress = currentIpAddress;
        } else {
        	Log.i("MessageParametersGenerator", "IpAddress unchanged.");
        }
        
        

        String mCurCpuLoad = String.valueOf(devProps.getSystemProperties().getCurCpuLoadPercent());
        if (!mCurCpuLoad.equals(mLastCpuLoad) || locationChanged) {
            fe = createFeature("CPULoad", time, mCurCpuLoad, QUANT);
            addToUpdateRequest(publishRequest, mPhoneSystemCat, null, fe, MetadataLifetime.session, true);
            mLastCpuLoad = mCurCpuLoad;
        }

        String mCurFreeRam = String.valueOf(devProps.getSystemProperties().getFreeRamInBytes());
        if (!mCurFreeRam.equals(mLastFreeRam) || locationChanged) {
            fe = createFeature("MemoryAvailable", time, mCurFreeRam, QUANT);
            addToUpdateRequest(publishRequest, mPhoneMemoryCat, null, fe, MetadataLifetime.session, true);
            mLastFreeRam = mCurFreeRam;
        }

        String mCurProcCount = String.valueOf(devProps.getApplicationProperties().getRuningProcCount());
        if (!mCurProcCount.equals(mLastProcCount) || locationChanged) {
            fe = createFeature("ProcessCount", time, mCurProcCount, QUANT);
            addToUpdateRequest(publishRequest, mPhoneSystemCat, null, fe, MetadataLifetime.session, true);
            mLastProcCount = mCurProcCount;
        }

        /* traffic stuff rx/tx 3g / other */
        String currentTxBytesTotal = String.valueOf(SystemProperties.getTotalTxBytes());
        String currentRxBytesTotal = String.valueOf(SystemProperties.getTotalRxBytes());
        if (!currentTxBytesTotal.equals(mLastTxBytesTotal) ||
        	!currentRxBytesTotal.equals(mLastRxBytesTotal) ||
        	locationChanged) {
        	// we need to sent new features
            fe = createFeature("Rx3g", time, String.valueOf(SystemProperties.getRxBytes3G()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneIpCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("Tx3g", time, String.valueOf(SystemProperties.getTxBytes3G()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneIpCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("RxOther", time, String.valueOf(SystemProperties.getRxBytesOther()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneIpCat, null, fe, MetadataLifetime.session, true);
            fe = createFeature("TxOther", time, String.valueOf(SystemProperties.getTxBytesOther()), QUANT);
            addToUpdateRequest(publishRequest, mPhoneIpCat, null, fe, MetadataLifetime.session, true);
            
            // remember last state
            mLastRxBytesTotal = currentRxBytesTotal;
            mLastTxBytesTotal = currentTxBytesTotal;
        }
        
        // added outgoing_sms and incoming_sms
        for (SmsInfos smsInfo : SMSObserver.incomingSms) {
        	fe = createFeature("IncomingSms", DateUtil.getTimestampXsd(smsInfo.getDate().getTime()), "Incoming SMS to " + smsInfo.getAddress(), ARBIT);
        	addToUpdateRequest(publishRequest, mPhoneSMSCat, null, fe, MetadataLifetime.session, true);			
		}
        for (SmsInfos smsInfo : SMSObserver.outgoingSms) {
        	fe = createFeature("OutgoingSms", DateUtil.getTimestampXsd(smsInfo.getDate().getTime()), "Outgoing SMS to " + smsInfo.getAddress(), ARBIT);
        	addToUpdateRequest(publishRequest, mPhoneSMSCat, null, fe, MetadataLifetime.session, true);			
        }        
        SMSObserver.resetSmsInfos();
        
        String mCurSmsRecCount = String.valueOf(SMSObserver.sSmsInCount);
        if (!mCurSmsRecCount.equals(mLastSmsRecCount) || locationChanged) {
            fe = createFeature("ReceivedCount", time, mCurSmsRecCount, QUANT);
            addToUpdateRequest(publishRequest, mPhoneSMSCat, null, fe, MetadataLifetime.session, true);
            mLastSmsRecCount = mCurSmsRecCount;
        }

        String mCurSmsSentCount = String.valueOf(SMSObserver.sSmsSentCount);
        if (!mCurSmsSentCount.equals(mLastSmsSentCount) || locationChanged) {
            fe = createFeature("SentCount", time, mCurSmsSentCount, QUANT);
            addToUpdateRequest(publishRequest, mPhoneSMSCat, null, fe, MetadataLifetime.session, true);
            mLastSmsSentCount = mCurSmsSentCount;
        }

        Date lastSendDate = SMSObserver.sLastSendDate;
        if (lastSendDate != null) {
            String mCurSmsSentDate = SMSObserver.sLastSendDate.toLocaleString();
            if (!mCurSmsSentDate.equals(mLastSmsSentDate) || locationChanged) {
                fe = createFeature("LastSent", time, mCurSmsSentDate, QUANT);
                addToUpdateRequest(publishRequest, mPhoneSMSCat, null, fe, MetadataLifetime.session, true);
                mLastSmsSentDate = mCurSmsSentDate;
            }
        }

        // last sensor that was used. currently only the camera sensor can be used so
        // we just use that...as soon as other sensors are observed, we need a method
        // to compare the dates of the sensors that are used to find out which of them
        // was the last one in use...
        Date lastCameraUsedDate = CameraReceiver.sLastPictureTakenDate;
        if (lastCameraUsedDate != null) {
            fe = createFeature("NewPicture", time, "true", QUALI);
            addToUpdateRequest(publishRequest, mPhoneSensorCat, null, fe, MetadataLifetime.session, true);
        }
        
        // back camera used?
        MainActivity.checkCameraActive();
        boolean isUsed = (MainActivity.mBackCamActive || MainActivity.mFrontCamActive);
        String isUsedStr = isUsed ? "true" : "false";
        Log.i("MessageParametersGenerator", "CameraIsUsed = " + isUsed);
        if (isUsed != mLastCameraIsUsed || !sInitialLocationWasSend) {
        	Log.i("###", "MUST PUBLISH HERE");
        	fe = createFeature("CameraIsUsed", time, isUsedStr, QUALI);
        	addToUpdateRequest(publishRequest, mPhoneSensorCat, null, fe, MetadataLifetime.session, true);
        	mLastCameraIsUsed = isUsed;
		}
        
        

        // send application information?
        if (!dontSendAppInfos) {
            ArrayList<ApplicationListEntry> currentAppList = devProps.getApplicationProperties().getApplicationList(dontSendGoogleApps,
                    true, true, true);

            for (int i = 0; i < currentAppList.size(); i++) {
                // create app-category-identifier
                Identity phoneAppCat = createCategory("smartphone.android.app:" + i, deviceIdent.getName());

                boolean appEntryExists = (mLastAppList != null && i < mLastAppList.size() && mLastAppList.get(i) != null);

                /*
                 * if the name of the application differs from last application-list, then the application seems to have changed and
                 * therefore we need to (re)publish all application-informations
                 */
                boolean entryNameChanged = false;

                // smartphone.android.app.Name
                if (!sInitialDevCharWasSend || locationChanged
                        || (appEntryExists && !mLastAppList.get(i).getName().equals(currentAppList.get(i).getName()))) {
                    addToUpdateRequest(publishRequest, mPhoneAndroidCat, phoneAppCat, mSubCategoryOf, MetadataLifetime.session, false);
                    entryNameChanged = true;
                    fe = createFeature("Name", time, currentAppList.get(i).getName(), ARBIT);
                    addToUpdateRequest(publishRequest, phoneAppCat, null, fe, MetadataLifetime.session, true);
                }
                
                // smartphone.android.app.Installer
                if (!sInitialDevCharWasSend || entryNameChanged || locationChanged
                        || (appEntryExists && !mLastAppList.get(i).getInstallerPackageName().equals(currentAppList.get(i).getInstallerPackageName()))) {
                    fe = createFeature("Installer", time, currentAppList.get(i).getInstallerPackageName(), ARBIT);
                    addToUpdateRequest(publishRequest, phoneAppCat, null, fe, MetadataLifetime.session, true);
                }
                
                // smartphone.android.app.VersionName and VersionCode
                if (!sInitialDevCharWasSend || entryNameChanged || locationChanged
                        || (appEntryExists && !mLastAppList.get(i).getVersionName().equals(currentAppList.get(i).getVersionName()))) {
                    fe = createFeature("VersionName", time, currentAppList.get(i).getVersionName(), ARBIT);
                    addToUpdateRequest(publishRequest, phoneAppCat, null, fe, MetadataLifetime.session, true);
                    fe = createFeature("VersionCode", time, currentAppList.get(i).getVersionCode() + "", QUANT);
                    addToUpdateRequest(publishRequest, phoneAppCat, null, fe, MetadataLifetime.session, true);
                }

                // smartphone.android.app.IsRunning
                if (!sInitialDevCharWasSend || entryNameChanged || locationChanged
                        || (appEntryExists && !mLastAppList.get(i).isCurrentlyRunning() == currentAppList.get(i).isCurrentlyRunning())) {
                    fe = createFeature("IsRunning", time,
                            String.valueOf(currentAppList.get(i).isCurrentlyRunning()), ARBIT);
                    addToUpdateRequest(publishRequest, phoneAppCat, null, fe, MetadataLifetime.session, true);
                }

                // compare current permissions with "old" permission
                ArrayList<Permission> oldPermissionList = null;
                if (appEntryExists) {
                    oldPermissionList = mLastAppList.get(i).getPermissions();
                }
                ArrayList<Permission> newPermissionList = currentAppList.get(i).getPermissions();

                for (int j = 0; j < newPermissionList.size(); j++) {
                    if (!sInitialDevCharWasSend || entryNameChanged || locationChanged
                            || (!appEntryExists || !oldPermissionList.contains(newPermissionList.get(j)))) {

                        // smartphone.android.app.permission
                        Identity phoneAppPermCat = createCategory("smartphone.android.app:" + i + ".permission:" + j,
                                deviceIdent.getName());
                        addToUpdateRequest(publishRequest, phoneAppCat, phoneAppPermCat, mSubCategoryOf, MetadataLifetime.session, false);

                        if (newPermissionList.get(j).getPermissionType() == Permission.PERMISSIONTYPE_GRANTED) {
                            // smartphone.app.permission.granted
                            fe = createFeature("Granted", time, newPermissionList.get(j)
                                    .getPermissionName(), ARBIT);
                            addToUpdateRequest(publishRequest, phoneAppPermCat, null, fe, MetadataLifetime.session, true);
                        } else {
                            // smartphone.app.permission.requries, currently the same as permissions.Granted
                            fe = createFeature("Required", time, newPermissionList.get(j)
                                    .getPermissionName(), ARBIT);
                            addToUpdateRequest(publishRequest, phoneAppPermCat, null, fe, MetadataLifetime.session, true);
                        }
                    }
                }
            }
            mLastAppList = currentAppList;
        }
    }

    /**
     * create new publish-update and add it to passed in publish-request if the data has already been send, append a publish-delete to erase
     * the previously published data
     * 
     * @param request
     *            publish-request-object to add updates to
     * @param ident1
     *            first identifier for update-request
     * @param ident2
     *            second identifier for update-request
     * @param metadata
     *            metadata to append to identifier(s)
     * @param metadataLifeTime
     *            lifetime of metadata
     * @param doDelete
     *            flag to decide if an automatic delete request is appended
     */
    public void addToUpdateRequest(PublishRequest request, Identifier ident1, Identifier ident2, Document metadata,
            MetadataLifetime metadataLifeTime, boolean doDelete) {
        // add publish-update to request
        PublishUpdate publishUpdate = Requests.createPublishUpdate();
        publishUpdate = Requests.createPublishUpdate();
        publishUpdate.setIdentifier1(ident1);
        if (ident2 != null) {
            publishUpdate.setIdentifier2(ident2);
        }
        publishUpdate.addMetadata(metadata);
        publishUpdate.setLifeTime(metadataLifeTime);

        // if a previous message has been send, add publish-delete
        if (sInitialDevCharWasSend && doDelete) {
            PublishDelete publishDelete = Requests.createPublishDelete();
            // standard-metadata
            if (metadata.getChildNodes().item(0).getPrefix().equals("meta")) {
                publishDelete.addNamespaceDeclaration(IfmapStrings.STD_METADATA_PREFIX, IfmapStrings.STD_METADATA_NS_URI);
                publishDelete.setFilter(metadata.getChildNodes().item(0).getPrefix() + ":"
                        + metadata.getChildNodes().item(0).getLocalName() + "[" + "@ifmap-publisher-id=\'"
                        + MainActivity.sCurrentPublisherId + "\']");
            }
            // esukom-specific-metadata
            else if (metadata.getElementsByTagName("id").item(0) != null) {
                publishDelete.addNamespaceDeclaration(NAMESPACE_PREFIX, NAMESPACE);
                publishDelete.setFilter(metadata.getChildNodes().item(0).getPrefix() + ":"
                        + metadata.getChildNodes().item(0).getLocalName() + "[" + "id='"
                        + metadata.getElementsByTagName("id").item(0).getTextContent() + "'" + " and " + "@ifmap-publisher-id=\'"
                        + MainActivity.sCurrentPublisherId + "\']");
            }
            publishDelete.setIdentifier1(ident1);
            if (ident2 != null) {
                publishDelete.setIdentifier2(ident2);
            }
            request.addPublishElement(publishDelete);
        }
        request.addPublishElement(publishUpdate);
    }

    /**
     * create a standard single element document
     * 
     * @param name
     *            name of the element
     * @param card
     *            cardinality of the element
     * 
     * @return single element document
     */
    private Document createStdSingleElementDocument(String name, Cardinality card) {
        return createSingleElementDocument(IfmapStrings.STD_METADATA_PREFIX + ":" + name, IfmapStrings.STD_METADATA_NS_URI, card);
    }

    /**
     * create a single element document
     * 
     * @param qualifiedName
     *            qualified name of element
     * @param uri
     *            namespace uri
     * @param cardinality
     *            cardinality of element
     * 
     * @return single element document
     */
    private Document createSingleElementDocument(String qualifiedName, String uri, Cardinality cardinality) {
        Document doc = mDocumentBuilder.newDocument();
        Element e = doc.createElementNS(uri, qualifiedName);
        e.setAttributeNS(null, "ifmap-cardinality", cardinality.toString());
        doc.appendChild(e);
        return doc;
    }

    /**
     * create and append a text element
     * 
     * @param doc
     *            document
     * @param parent
     *            parent element
     * @param elName
     *            name of element
     * @param value
     *            value for element
     * 
     * @return element
     */
    private Element createAndAppendTextElementCheckNull(Document doc, Element parent, String elName, Object value) {
        if (doc == null || parent == null || elName == null) {
            throw new NullPointerException("bad parameters given");
        }

        if (value == null) {
            throw new NullPointerException("null is not allowed for " + elName + " in " + doc.getFirstChild().getLocalName());
        }

        String valueStr = value.toString();
        if (valueStr == null) {
            throw new NullPointerException("null-string not allowed for " + elName + " in " + doc.getFirstChild().getLocalName());
        }

        Element child = createAndAppendElement(doc, parent, elName);
        Text txtCElement = doc.createTextNode(valueStr);
        child.appendChild(txtCElement);
        return child;
    }

    /**
     * Helper to create an {@link Element} without a namespace in {@link Document} doc and append it to the {@link Element} given by parent.
     * 
     * @param doc
     * @param elName
     * 
     * @return element
     */
    private Element createAndAppendElement(Document doc, Element parent, String elName) {
        Element el = doc.createElementNS(null, elName);
        parent.appendChild(el);
        return el;
    }

    /**
     * Helper to create a new element with name elName and append it to the {@link Element} given by parent if the given value is non-null.
     * The new {@link Element} will have {@link Text} node containing value.
     * 
     * @param doc
     *            {@link Document} where parent is located in
     * @param parent
     *            where to append the new element
     * @param elName
     *            the name of the new element.
     * @param value
     *            the value of the {@link Text} node appended to the new element, using toString() on the object.
     */
    private void appendTextElementIfNotNull(Document doc, Element parent, String elName, Object value) {
        if (value == null) {
            return;
        }
        createAndAppendTextElementCheckNull(doc, parent, elName, value);
    }

    /**
     * create feature-metadata
     * 
     * @param id
     *            content for id-element
     * @param timestamp
     *            timestamp-string
     * @param value
     *            content for value-element
     * @param contentType
     *            type of content
     * 
     * @return feature-metadata-document
     */
    private Document createFeature(String id, String timestamp, String value, String contentType) {
        Document doc = mDocumentBuilder.newDocument();
        Element feature = doc.createElementNS(NAMESPACE, NAMESPACE_PREFIX + ":feature");

        feature.setAttributeNS(null, "ifmap-cardinality", "multiValue");
        feature.setAttribute("ctxp-timestamp", timestamp);
        if (PreferencesValues.sEnableLocationTracking && (MainActivity.sLatitude != null && MainActivity.sLongitude != null)) {
            feature.setAttribute("ctxp-position", MainActivity.sLatitude + "-" + MainActivity.sLongitude);
        }

        Element idElement = doc.createElement("id");
        idElement.setTextContent(id);
        feature.appendChild(idElement);

        Element typeElement = doc.createElement("type");
        typeElement.setTextContent(contentType);
        feature.appendChild(typeElement);

        Element valueElement = doc.createElement("value");
        valueElement.setTextContent(value);
        feature.appendChild(valueElement);

        doc.appendChild(feature);
        return doc;
    }

    /**
     * create new category-link
     * 
     * @param name
     *            category-link-name
     * 
     * @return category-link-metadata-document
     */
    private Document createCategoryLink(String name) {
        Document doc = mDocumentBuilder.newDocument();
        Element e = doc.createElementNS(NAMESPACE, NAMESPACE_PREFIX + ":" + name);
        e.setAttributeNS(null, "ifmap-cardinality", "singleValue");

        doc.appendChild(e);
        return doc;
    }

    /**
     * create a new category
     * 
     * @param name
     *            category-name
     * @param admDomain
     *            administrative-domain-string
     * 
     * @return category-identifer
     */
    private Identity createCategory(String name, String admDomain) {
        return Identifiers.createIdentity(IdentityType.other, name, admDomain, OTHER_TYPE_DEFINITION);
    }
    
    /**
     * Anonymize the given input String by using a cryptographic hash.
     * 
     * @param input
     * @return
     */
    private String anonymize(String input){
		String salt = CryptoUtil.sha256(input);
		// 10-times sha-256 hashing
		for (int i = 0; i < 10; i++) {
			input = CryptoUtil.sha256(input + salt);
		}
		return input;
    }
}
