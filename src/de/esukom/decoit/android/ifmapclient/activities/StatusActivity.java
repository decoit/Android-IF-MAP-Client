/* 
 * StatusActivity.java        0.1.6. 12/03/07
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

package de.esukom.decoit.android.ifmapclient.activities;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.esukom.decoit.android.ifmapclient.device.DeviceProperties;
import de.esukom.decoit.android.ifmapclient.device.DevicePropertiesDialog;
import de.esukom.decoit.android.ifmapclient.device.ListEntry;
import de.esukom.decoit.android.ifmapclient.device.StatusMessageAdapter;
import de.esukom.decoit.android.ifmapclient.device.system.SystemProperties;
import de.esukom.decoit.android.ifmapclient.observer.battery.BatteryReceiver;
import de.esukom.decoit.android.ifmapclient.observer.camera.CameraReceiver;
import de.esukom.decoit.android.ifmapclient.observer.sms.SMSObserver;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;

/**
 * Activity for showing Device-Status, current location and Applications Permission
 * 
 * @version 0.1.5
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Marcel Jahnke, Decoit GmbH
 * @author Leonid Schwenke, Decoit GmbH
 */
public class StatusActivity extends Activity implements OnItemClickListener {

    // static values for onClick-handler, used to determine which
    // list item has been clicked by using item position in list
    public static final byte LIST_POSITION_IP = 0;
    public static final byte LIST_POSITION_MAC = 1;
    public static final byte LIST_POSITION_IMEI = 2;
    public static final byte LIST_POSITION_IMSI = 3;
    public static final byte LIST_POSITION_KERNEL_VERSION = 4;
    public static final byte LIST_POSITION_FIRMWARE_VERSION = 5;
    public static final byte LIST_POSITION_BUILD_NUMBER = 6;
    public static final byte LIST_POSITION_BASEBAND_VERSION = 7;
    public static final byte LIST_POSITION_DEVICE_BRANDING = 8;
    public static final byte LIST_POSITION_DEVICE_MANUFACTURER = 9;
    public static final byte LIST_POSITION_DEVICE_PHONENUMBER = 10;
    public static final byte LIST_POSITION_DEVICE_SMSCOUNT_IN = 11;
    public static final byte LIST_POSITION_DEVICE_SMSCOUNT_OUT = 12;
    public static final byte LIST_POSITION_DEVICE_SMSSENDDATE = 13;
    public static final byte LIST_POSITION_DEVICE_LASTCAMERAUSE = 14;
    public static final byte LIST_POSITION_BLUEATOOTH_ENABLED = 15;
    public static final byte LIST_POSITION_MICROPHONE_MUTED = 16;
    public static final byte LIST_POSITION_BATTERY_LEVEL = 17;
    public static final byte LIST_POSITION_RECEIVED_BYTES = 18;
    public static final byte LIST_POSITION_TRANSFERED_BYTES = 19;
    public static final byte LIST_POSITION_CPU_LOAD = 20;
    public static final byte LIST_POSITION_RAM_FREE = 21;
    public static final byte LIST_POSITION_PROCESS_COUNT = 22;
    public static final byte LIST_POSITION_RUNNING_PROCESSES = 23;
    public static final byte LIST_POSITION_INSTALLED_APPS = 24;
    public static final byte LIST_POSITION_INSTALLED_APPS_WITH_PERMS = 25;
    public static final byte LIST_POSITION_PERMISSIONS = 26;
    public static final byte LIST_POSITION_LONGITUDE = 27;
    public static final byte LIST_POSITION_LATITUDE = 28;
    public static final byte LIST_POSITION_ALTITUDE = 29;

    // required by location manager from MainActivity in order to detect
    // if this Activity is already initialized before sending location-data
    // for displaying the location-data in status-list
    public static boolean sIsActivityActive = false;

    // location properties
    private static TextView sLocationLongitude;
    private static TextView sLocationLatitude;
    private static TextView sLocationAltitude;;

    // location tracking default values
    private static String sLatitudeValue = "not detected";
    private static String sLongitudeValue = "not detected";
    private static String sAltitudeValue = "not detected";

    // device properties
    private DeviceProperties mDeviceProperties;

    // ListView Items
    private ListEntry mEntry;
    private ListView mList;
    private ArrayList<ListEntry> mListArray;
    private StatusMessageAdapter mStatusAdapter;

    // -------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE HANDLING
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbox.logTxt(this.getLocalClassName(), "onCreate(...) called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3);

        // initialize status-list
        initValues();
        initListAdapter();
    }

    @Override
    public void onStart() {
        Toolbox.logTxt(this.getLocalClassName(), "onStart() called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Toolbox.logTxt(this.getLocalClassName(), "onResume() called");
        super.onStart();
        try {
            if (super.getParent().getIntent().getExtras().size() > 2) {
                sLatitudeValue = (String) super.getParent().getIntent().getExtras().get("latitude");
                sLongitudeValue = (String) super.getParent().getIntent().getExtras().get("longitude");
            }
        } catch (NullPointerException e) {
            Toolbox.logTxt(this.getClass().getName(), "StatusActivity.onResume(...) getExtras() " + e.getMessage());
        }
        initValues();
        initListAdapter();
    }

    // -------------------------------------------------------------------------
    // ACTIVITY INITIALISATION HANDLING
    // -------------------------------------------------------------------------

    /**
     * initialize required application values
     */
    private void initValues() {

        // location properties
        sLocationLongitude = (TextView) findViewById(R.id.LongitudeLabel_TextView);
        sLocationLatitude = (TextView) findViewById(R.id.LatitudeLabel_TextView);
        sLocationAltitude = (TextView) findViewById(R.id.AltitudeLabel_TextView);

        // get device properties and initialize text-fields
        mDeviceProperties = new DeviceProperties(this);

        // Add Items to ListView
        mListArray = new ArrayList<ListEntry>();

        // add values
        addValueToListEntry(getString(R.string.info_label_value_ip), mDeviceProperties.getSystemProperties().getLocalIpAddress());
        addValueToListEntry(getString(R.string.info_label_value_mac), mDeviceProperties.getSystemProperties().getMAC());
        addValueToListEntry(getString(R.string.info_label_value_deviceimei), mDeviceProperties.getPhoneProperties().getIMEI());
        addValueToListEntry(getString(R.string.info_label_value_deviceimsi), mDeviceProperties.getPhoneProperties().getIMSI());
        addValueToListEntry(getString(R.string.info_label_value_kernelversion), mDeviceProperties.getSystemProperties().getKernelVersion());
        addValueToListEntry(getString(R.string.info_label_value_version), mDeviceProperties.getPhoneProperties().getFirmwareVersion());
        addValueToListEntry(getString(R.string.info_label_value_buildnumber), mDeviceProperties.getPhoneProperties().getBuildNumber());
        addValueToListEntry(getString(R.string.info_label_value_basebandversion), mDeviceProperties.getPhoneProperties()
                .getBasebandVersion());
        addValueToListEntry(getString(R.string.info_label_value_branding), mDeviceProperties.getPhoneProperties().getBranding());
        addValueToListEntry(getString(R.string.info_label_value_manufacturer), mDeviceProperties.getPhoneProperties().getManufacturer());
        addValueToListEntry(getString(R.string.info_label_value_phonenumber), mDeviceProperties.getPhoneProperties().getPhonenumber());
        addValueToListEntry(getString(R.string.info_label_value_smscount), Integer.valueOf(SMSObserver.sSmsInCount).toString());
        addValueToListEntry(getString(R.string.info_label_value_smscount_out), Integer.valueOf(SMSObserver.sSmsSentCount).toString());
        addValueToListEntry(getString(R.string.info_label_value_smscount_lastsend), convertLastSentDate(SMSObserver.sLastSendDate));
        addValueToListEntry(getString(R.string.info_label_value_camera_lastused), convertLastSentDate(CameraReceiver.sLastPictureTakenDate));
        addValueToListEntry(getString(R.string.info_label_value_bluetooth), mDeviceProperties.getPhoneProperties()
                .getBluetoothActiveStatusString());
        addValueToListEntry(getString(R.string.info_label_value_microphone), mDeviceProperties.getPhoneProperties()
                .getMicrophoneActiveString());
        addValueToListEntry(getString(R.string.info_label_value_battery), BatteryReceiver.sCurrentBatteryLevel + "%");
        addValueToListEntry(getString(R.string.info_label_value_received_total_bytes), SystemProperties.getTotalRxKBytes()
                + " kb");
        addValueToListEntry(getString(R.string.info_label_value_transferred_total_bytes), mDeviceProperties.getSystemProperties().getTotalTxKBytes()
                + " kb");
        addValueToListEntry(getString(R.string.info_label_value_cpu_load), "touch to update");
        addValueToListEntry(getString(R.string.info_label_value_ram_free), mDeviceProperties.getSystemProperties().getFormattedFreeRam());
        addValueToListEntry(getString(R.string.info_label_value_process_count),
                String.valueOf(mDeviceProperties.getApplicationProperties().getRuningProcCount()));
        addValueToListEntry(getString(R.string.info_label_value_running_process_names), "touch to show");
        addValueToListEntry(getString(R.string.info_label_apps), "touch to show");
        addValueToListEntry(getString(R.string.info_label_appswithperms), "touch to show");
        addValueToListEntry(getString(R.string.info_label_perms), "touch to show");
        addValueToListEntry(getString(R.string.info_label_latitude), sLatitudeValue);
        addValueToListEntry(getString(R.string.info_label_longitude), sLongitudeValue);
        addValueToListEntry(getString(R.string.info_label_altitude), sAltitudeValue);

        sIsActivityActive = true;
    }

    /**
     * initialize adapter for status-list
     */
    private void initListAdapter() {
        mList = (ListView) findViewById(R.id.status_ListView);
        mStatusAdapter = new StatusMessageAdapter(this, mListArray);
        mList.setAdapter(mStatusAdapter);
        mList.setOnItemClickListener(this);
    }

    // -------------------------------------------------------------------------
    // BUTTON HANDLING
    // -------------------------------------------------------------------------

    /**
     * Handler for update status information button
     * 
     * @param view
     *            element that originated the call
     */
    public void updateStatusButtonHandler(View view) {
        // update status information
        Toast.makeText(this, "updating status information...", Toast.LENGTH_SHORT).show();
        updateEntry(mDeviceProperties.getSystemProperties().getLocalIpAddress(), LIST_POSITION_IP);
        updateEntry(String.valueOf(SMSObserver.sSmsInCount), LIST_POSITION_DEVICE_SMSCOUNT_IN);
        updateEntry(String.valueOf(SMSObserver.sSmsSentCount), LIST_POSITION_DEVICE_SMSCOUNT_OUT);
        updateEntry(convertLastSentDate(SMSObserver.sLastSendDate), LIST_POSITION_DEVICE_SMSSENDDATE); 
        updateEntry(convertLastSentDate(CameraReceiver.sLastPictureTakenDate), LIST_POSITION_DEVICE_LASTCAMERAUSE);
        updateEntry(mDeviceProperties.getPhoneProperties().getBluetoothActiveStatusString(), LIST_POSITION_BLUEATOOTH_ENABLED);
        updateEntry(mDeviceProperties.getPhoneProperties().getMicrophoneActiveString(), LIST_POSITION_MICROPHONE_MUTED);
        updateEntry(BatteryReceiver.sCurrentBatteryLevel + "%", LIST_POSITION_BATTERY_LEVEL);
        updateEntry(SystemProperties.getTotalRxKBytes() + " kb", LIST_POSITION_RECEIVED_BYTES);
        updateEntry(mDeviceProperties.getSystemProperties().getTotalTxKBytes() + " kb", LIST_POSITION_TRANSFERED_BYTES);
        updateEntry(sLongitudeValue, LIST_POSITION_LONGITUDE);
        updateEntry(sLatitudeValue, LIST_POSITION_LATITUDE);
        updateEntry(sLongitudeValue, LIST_POSITION_LONGITUDE);
        updateEntry(sAltitudeValue, LIST_POSITION_ALTITUDE);
        updateEntry(mDeviceProperties.getSystemProperties().getFormattedCurCpuLoadPercent(), LIST_POSITION_CPU_LOAD);
        updateEntry(mDeviceProperties.getSystemProperties().getFormattedFreeRam(), LIST_POSITION_RAM_FREE);
        updateEntry(String.valueOf(mDeviceProperties.getApplicationProperties().getRuningProcCount()), LIST_POSITION_PROCESS_COUNT);
        mStatusAdapter = new StatusMessageAdapter(this, mListArray);
        mList.setAdapter(mStatusAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        mEntry = (ListEntry) mStatusAdapter.getItem(position);
        if (position != LIST_POSITION_INSTALLED_APPS && position != LIST_POSITION_INSTALLED_APPS_WITH_PERMS
                && position != LIST_POSITION_PERMISSIONS) {
            Toast.makeText(this, "updating " + mEntry.getTitle().replace(":", "") + " value...", Toast.LENGTH_SHORT).show();
        }

        // update list-entries
        switch (position) {
        case LIST_POSITION_IP:
            updateEntry(mDeviceProperties.getSystemProperties().getLocalIpAddress(), position);
            break;
        case LIST_POSITION_MAC:
            updateEntry(mDeviceProperties.getSystemProperties().getMAC(), position);
            break;
        case LIST_POSITION_IMSI:
            updateEntry(mDeviceProperties.getPhoneProperties().getIMSI(), position);
            break;
        case LIST_POSITION_IMEI:
            updateEntry(mDeviceProperties.getPhoneProperties().getIMEI(), position);
            break;
        case LIST_POSITION_BUILD_NUMBER:
            updateEntry(mDeviceProperties.getPhoneProperties().getBuildNumber(), position);
            break;
        case LIST_POSITION_DEVICE_BRANDING:
            updateEntry(mDeviceProperties.getPhoneProperties().getBranding(), position);
            break;
        case LIST_POSITION_DEVICE_MANUFACTURER:
            updateEntry(mDeviceProperties.getPhoneProperties().getManufacturer(), position);
            break;
        case LIST_POSITION_DEVICE_PHONENUMBER:
            updateEntry(mDeviceProperties.getPhoneProperties().getPhonenumber(), position);
            break;
        case LIST_POSITION_FIRMWARE_VERSION:
            updateEntry(mDeviceProperties.getPhoneProperties().getFirmwareVersion(), position);
            break;
        case LIST_POSITION_BASEBAND_VERSION:
            updateEntry(mDeviceProperties.getPhoneProperties().getBasebandVersion(), position);
            break;
        case LIST_POSITION_DEVICE_SMSCOUNT_IN:
            updateEntry(String.valueOf(SMSObserver.sSmsInCount), position);
            break;
        case LIST_POSITION_DEVICE_SMSCOUNT_OUT:
            updateEntry(String.valueOf(SMSObserver.sSmsSentCount), position);
            break;
        case LIST_POSITION_DEVICE_SMSSENDDATE:
            updateEntry(convertLastSentDate(SMSObserver.sLastSendDate), position);
            break;
        case LIST_POSITION_DEVICE_LASTCAMERAUSE:
            updateEntry(convertLastSentDate(CameraReceiver.sLastPictureTakenDate), position);
            break;
        case LIST_POSITION_BLUEATOOTH_ENABLED:
            String bluetoothEntryValue = mDeviceProperties.getPhoneProperties().getBluetoothActiveStatusString();
            updateEntry(bluetoothEntryValue, position);
            break;
        case LIST_POSITION_MICROPHONE_MUTED:
            String microphoneEntryValue = mDeviceProperties.getPhoneProperties().getMicrophoneActiveString();
            updateEntry(microphoneEntryValue, position);
            break;
        case LIST_POSITION_BATTERY_LEVEL:
            updateEntry(BatteryReceiver.sCurrentBatteryLevel + "%", position);
            break;
        case LIST_POSITION_RECEIVED_BYTES:
            updateEntry(SystemProperties.getTotalRxKBytes() + " kb", position);
            break;
        case LIST_POSITION_TRANSFERED_BYTES:
            updateEntry(mDeviceProperties.getSystemProperties().getTotalTxKBytes() + " kb", position);
            break;
        case LIST_POSITION_LATITUDE:
            updateEntry(sLongitudeValue, position);
            break;
        case LIST_POSITION_LONGITUDE:
            updateEntry(sLatitudeValue, position);
            break;
        case LIST_POSITION_ALTITUDE:
            updateEntry(sAltitudeValue, position);
            break;
        case LIST_POSITION_INSTALLED_APPS:
            showApplicationsInformations(LIST_POSITION_INSTALLED_APPS);
            break;
        case LIST_POSITION_INSTALLED_APPS_WITH_PERMS:
            showApplicationsInformations(LIST_POSITION_INSTALLED_APPS_WITH_PERMS);
            break;
        case LIST_POSITION_PERMISSIONS:
            showApplicationsInformations(LIST_POSITION_PERMISSIONS);
            break;
        case LIST_POSITION_CPU_LOAD:
            updateEntry(mDeviceProperties.getSystemProperties().getFormattedCurCpuLoadPercent(), position);
            break;
        case LIST_POSITION_RAM_FREE:
            updateEntry(mDeviceProperties.getSystemProperties().getFormattedFreeRam(), position);
            break;
        case LIST_POSITION_PROCESS_COUNT:
            updateEntry(String.valueOf(mDeviceProperties.getApplicationProperties().getRuningProcCount()), position);
            break;
        case LIST_POSITION_RUNNING_PROCESSES:
            showApplicationsInformations(LIST_POSITION_RUNNING_PROCESSES);
            break;
        }
        mListArray.set(position, mEntry);
        mStatusAdapter = new StatusMessageAdapter(this, mListArray);
        mList.setAdapter(mStatusAdapter);
    }

    /**
     * show applications-informations inside a dialog-box
     * 
     * @param infoType
     *            type of applications-informations
     */
    public void showApplicationsInformations(int infoType) {
        String title = null;
        String msgToShow = null;
        ArrayList<String> appsettings = null;
        switch (infoType) {
        case LIST_POSITION_INSTALLED_APPS:
            // gather installed applications informations
            // and show them in a simple dialog-box
            // getFormattedApplicationList(boolean excludeNativeApplications, boolean includeVersionNumber,
            // boolean includePermissions)
            appsettings = mDeviceProperties.getApplicationProperties().getFormattedApplicationList(true, true, false, true);
            title = "Installed Applications";
            break;
        case LIST_POSITION_INSTALLED_APPS_WITH_PERMS:
            // gather installed applications and show them in a simple
            // dialog-box
            appsettings = mDeviceProperties.getApplicationProperties().getFormattedApplicationList(false, true, true, true);
            title = "Installed Applications";
            break;
        case LIST_POSITION_PERMISSIONS:
            appsettings = mDeviceProperties.getApplicationProperties().getFormattedPermissionsList();
            title = "Permissions";
            break;
        case LIST_POSITION_RUNNING_PROCESSES:
            appsettings = mDeviceProperties.getApplicationProperties().getFormattedRunningAppProcessNamesList();
            title = "Running Processes";
            break;
        }

        if (appsettings != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < appsettings.size() - 1; i++) {
                sb.append(appsettings.get(i) + "\n");
            }
            msgToShow = sb.toString();
            DevicePropertiesDialog dialog = new DevicePropertiesDialog(this, title, msgToShow);
            dialog.show();
        }
    }

    /**
     * we override the behavior of the back-button so that the application runs in the background (instead of destroying it) when pressing
     * back (similar to the home button)
     */
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }

    // -------------------------------------------------------------------------
    // STATUS-LIST OPERATIONS
    // -------------------------------------------------------------------------

    /**
     * add a new value to device properties list
     * 
     * @param label
     *            label of device properties list-entry
     * @param entry
     *            value of properties-list-entry
     */
    private void addValueToListEntry(String label, String entry) {
        if (entry == null || entry.length() == 0) {
            entry = "not detected, touch to update";
        }
        mListArray.add(new ListEntry(label, entry));
    }

    /**
     * set updated value to device properties list
     * 
     * @param value
     *            new value for entry
     * @param position
     *            position for list entry
     */
    private void updateEntry(String value, int position) {
        ListEntry current = (ListEntry) mStatusAdapter.getItem(position);
        if (value == null || value.length() == 0) {
            value = "not detected, touch to update";
        }
        current.setValue(value);

        mListArray.set(position, current);
    }

    /**
     * show the current location of the user
     * 
     * @param latitude
     *            current latitude value
     * @param longitude
     *            current longitude value
     * @param altitude
     *            current altitude value
     */
    public static void setCurrentLocation(double latitude, double longitude, double altitude) {
        sLocationLatitude.setText("Latitude: " + latitude);
        sLocationLongitude.setText("Longitude: " + longitude);
        sLocationAltitude.setText("Altitude: " + altitude);
        sLatitudeValue = Double.valueOf(latitude).toString();
        sLongitudeValue = Double.valueOf(longitude).toString();
        sAltitudeValue = Double.valueOf(altitude).toString();
    }

    /**
     * get current longitude
     * 
     * @return longitude-textview
     */
    public TextView getmLocationLongitude() {
        return sLocationLongitude;
    }

    /**
     * get current latitude
     * 
     * @return latitude-textview
     */
    public TextView getmLocationLatitude() {
        return sLocationLatitude;
    }

    /**
     * get current altitude
     * 
     * @return altitude-textview
     */
    public TextView getmLocationAltitude() {
        return sLocationAltitude;
    }

    /**
     * helper function to convert the passed in date to timestamp-string
     * 
     * @param date
     *            date to convert
     * @return date as timestamp-string
     */
    private String convertLastSentDate(Date date) {
        if (date == null) {
            return "not available";
        } else {
            return date.toLocaleString();
        }
    }
}
