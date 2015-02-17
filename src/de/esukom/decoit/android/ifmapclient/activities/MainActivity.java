/* 
 * MainActivity.java        0.1.6 07/02/12
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
 * d
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License. 
 */

package de.esukom.decoit.android.ifmapclient.activities;

import java.util.regex.Matcher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.googlecode.jsendnsca.encryption.Encryption;

import de.esukom.decoit.android.ifmapclient.connection.ConnectionObjects;
import de.esukom.decoit.android.ifmapclient.database.LoggingDatabase;
import de.esukom.decoit.android.ifmapclient.device.DeviceProperties;
import de.esukom.decoit.android.ifmapclient.logging.LogMessage;
import de.esukom.decoit.android.ifmapclient.logging.LogMessageHelper;
import de.esukom.decoit.android.ifmapclient.messaging.EventParameters;
import de.esukom.decoit.android.ifmapclient.messaging.MessageHandler;
import de.esukom.decoit.android.ifmapclient.messaging.MessageParametersGenerator;
import de.esukom.decoit.android.ifmapclient.messaging.ResponseParameters;
import de.esukom.decoit.android.ifmapclient.observer.battery.BatteryReceiver;
import de.esukom.decoit.android.ifmapclient.observer.camera.CameraReceiver;
import de.esukom.decoit.android.ifmapclient.observer.location.LocationObserver;
import de.esukom.decoit.android.ifmapclient.observer.sms.SMSObserver;
import de.esukom.decoit.android.ifmapclient.preferences.PreferencesValues;
import de.esukom.decoit.android.ifmapclient.services.NscaService;
import de.esukom.decoit.android.ifmapclient.services.NscaService.LocalBinder;
import de.esukom.decoit.android.ifmapclient.services.PermanentConnectionService;
import de.esukom.decoit.android.ifmapclient.services.RenewConnectionService;
import de.esukom.decoit.android.ifmapclient.services.binder.BinderClass;
import de.esukom.decoit.android.ifmapclient.services.binder.UnbinderClass;
import de.esukom.decoit.android.ifmapclient.services.local.LocalServiceParameters;
import de.esukom.decoit.android.ifmapclient.services.local.LocalServicePermanent;
import de.esukom.decoit.android.ifmapclient.services.local.LocalServiceSynchronous;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;
import de.fhhannover.inform.trust.ifmapj.IfmapJ;
import de.fhhannover.inform.trust.ifmapj.IfmapJHelper;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;
import de.fhhannover.inform.trust.ifmapj.messages.PublishRequest;

/**
 * Main Activity wich handles the communication with the MAP-Server
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 * @author Marcel Jahnke, Decoit GmbH
 * @author Markus Schölzel, Decoit GmbH
 */
public class MainActivity extends Activity {

	public static boolean mBackCamActive = false, mFrontCamActive = false;

	public static void checkCameraActive() {
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
				int cameraCount = 0;
				Camera cam = null;
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				cameraCount = Camera.getNumberOfCameras();
				for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
					Camera.getCameraInfo(camIdx, cameraInfo);
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
						try {
							cam = Camera.open(camIdx);
							MainActivity.mBackCamActive = false;
							cam.release();
						} catch (RuntimeException e) {
							Log.e("SystemProperties",
									"Camera failed to open: "
											+ e.getLocalizedMessage());
							MainActivity.mBackCamActive = true;
						}
					}
					Camera.getCameraInfo(camIdx, cameraInfo);
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						try {
							cam = Camera.open(camIdx);
							MainActivity.mFrontCamActive = false;
							cam.release();
						} catch (RuntimeException e) {
							Log.e("SystemProperties",
									"Camera failed to open: "
											+ e.getLocalizedMessage());
							MainActivity.mFrontCamActive = true;
						}
					}
				}
				Log.i("CameraWatcher", "Front: " + mFrontCamActive + " Back: " + mBackCamActive);
				
//			}
//		}).start();
	}

	// local services definitions
	public static RenewConnectionService.LocalBinder sBoundRenewConnService;
	public static PermanentConnectionService.LocalBinder sBoundPermConnService;

	// publisher id assigned from map-server
	public static String sCurrentPublisherId;

	// fields for location-tracking-values, declared static so that
	// the status-activity can access them
	public static String sLatitude = null;
	public static String sLongitude = null;
	public static String sAltitude = null;

	// ssrc-connection-object
	private static SSRC sSsrcConnection;

	// preferences
	private PreferencesValues mPreferences;

	// device properties
	private DeviceProperties mDeviceProperties;

	// manage nsca connection for iMonitor
	private NscaService mNscaServiceBind;

	// buttons
	private Button mConnectButton;
	private Button mDisconnectButton;
	private Button mPublishDeviceCharacteristicsButton;

	// status message field
	private EditText mStatusMessageField;

	// progress dialog and notifications
	private ProgressDialog myProgressDialog = null;

	// current if-map session and publisher id
	@SuppressWarnings("unused")
	private String mCurrentSessionId;

	// application/connection states
	private boolean mIsConnected = false;

	// local services
	private ServiceConnection mConnection;
	private ServiceConnection mPermConnection;

	// local services states
	private boolean mIsBound;

	// current messaging type (as defined in Message-Handler-Class)
	private byte mMessageType;

	// callback-handler for local-services
	private final Handler mMsgHandler = new Handler();

	// database-manager
	private LoggingDatabase mLogDB = null;

	// parameters for local service classes
	private LocalServiceParameters mLocalServicePreferences;

	// type of server-response
	private byte mResponseType;

	// location tracking objects
	private LocationManager mLocManager;
	private LocationObserver mLocListener;

	// message-parameter-generator
	MessageParametersGenerator<PublishRequest> parameters;

	@SuppressWarnings("unused")
	private BatteryReceiver mBatteryReciever = null;

	// observer for incoming and outgoing sms-messages
	private SMSObserver mSmsObserver = null;

	// receiver for pictures taken with the camera
	@SuppressWarnings("unused")
	private CameraReceiver mCameraReceiver = null;

	// -------------------------------------------------------------------------
	// ACTIVITY LIFECYCLE HANDLING
	// -------------------------------------------------------------------------

	/**
	 * Called when the activity is first created
	 * 
	 * @param savedInstanceState
	 *            state-bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Toolbox.logTxt(this.getLocalClassName(), "onCreate(...) called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1);

		// initialize preferences-object
		mPreferences = new PreferencesValues();

		// initialize application
		initViews();
		initValues();
		initPreferences();

		// generator for if-map-messages to be published
		parameters = new MessageParametersGenerator<PublishRequest>();

		// initialize sms-observing
		mSmsObserver = new SMSObserver(getApplicationContext());
		mSmsObserver.registerReceivedSmsBroadcastReceiver();
		mSmsObserver.registerSentSmsContentObserver();

		// initialize camera-receiver
		mCameraReceiver = new CameraReceiver();

		// autoconnect at Startup
		if (mPreferences.ismAutoconnect()) {
			// start connection service if all required preferences are set
			if (!validatePreferences()) {
				mStatusMessageField.append("\n"
						+ getResources().getString(
								R.string.main_status_message_errorprefix)
						+ " "
						+ getResources().getString(
								R.string.main_default_wrongconfig_message));
			} else if (PreferencesValues.sMonitoringPreference
							.equalsIgnoreCase("IF-MAP")) {
				// set status message to-text-output-field
				mStatusMessageField.append("\n"
						+ getResources().getString(
								R.string.main_status_message_prefix) + " "
						+ "Sending Request to "
						+ mPreferences.getServerIpPreference() + ":"
						+ mPreferences.getServerPortPreference());

				// start new session
				mMessageType = MessageHandler.MSG_TYPE_REQUEST_NEWSESSION;
				initConnection();
				startConnectionService();
			} else {
					mConnectButton.performClick();
			}
		}

		// enable/disable buttons depending on connection state
		changeButtonStates(mIsConnected);

		// show initial notification
		Toolbox.showNotification(
				getResources().getString(R.string.notification_initial_label),
				getResources().getString(R.string.notification_initial_label),
				getResources().getString(R.string.notification_initial_messgae),
				getApplicationContext());
	}

	/**
	 * Called when the activity is started
	 */
	@Override
	public void onStart() {
		Toolbox.logTxt(this.getLocalClassName(), "onStart() called");
		super.onStart();
	}

	/**
	 * Called when the activity is brought back to foreground
	 */
	@Override
	public void onResume() {
		Toolbox.logTxt(this.getLocalClassName(), "onResume() called");
		super.onResume();

		// reload preferences
		initPreferences();

		// re-initialize location tracking
		if (PreferencesValues.sEnableLocationTracking) {
			initLocation();
		}
	}

	/**
	 * Called when the activity is on pause
	 */
	@Override
	protected void onPause() {
		Toolbox.logTxt(this.getLocalClassName(), "onPause() called");
		super.onPause();

		// get the intent of the upper activity (tablayout) and fill it
		if (sLatitude != null) {
			super.getParent().getIntent().putExtra("latitude", sLatitude);
			super.getParent().getIntent().putExtra("longitude", sLongitude);
		}
	}

	/**
	 * Called when the activity is shut down
	 */
	@Override
	protected void onDestroy() {
		Toolbox.logTxt(this.getLocalClassName(), "onDestroy() called");
		super.onDestroy();

		// "unlock locked" preferences
		PreferencesValues.sLockPreferences = false;
		PreferencesValues.sLockConnectionPreferences = false;
		PreferencesValues.sLockLocationTrackingOptions = false;

		// remove updates from location manager
		if (mLocManager != null) {
			try {
				mLocManager.removeUpdates(mLocListener);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		// reset connection-objects
		ConnectionObjects.setSsrcConnection(null);

		// delete last notification message when application is shut down
		Toolbox.cancelNotification();

		// check and unbind all local services and handlers
		if (sBoundRenewConnService != null) {
			mIsBound = UnbinderClass.doUnbindConnectionService(
					getApplicationContext(), mConnection, myProgressDialog,
					mIsBound);
			sBoundRenewConnService = null;
		}
		if (sBoundPermConnService != null) {
			mIsBound = UnbinderClass.doUnbindConnectionService(
					getApplicationContext(), mPermConnection, myProgressDialog,
					mIsBound);
			sBoundPermConnService = null;
		}
		if (mRenewHandler != null) {
			mRenewHandler.removeCallbacksAndMessages(null);
			mRenewHandler = null;
		}
		if (mUpdateHandler != null) {
			mUpdateHandler.removeCallbacksAndMessages(null);
			mUpdateHandler = null;
		}
	}

	// -------------------------------------------------------------------------
	// ACTIVITY INITIALISATION HANDLING
	// -------------------------------------------------------------------------

	/**
	 * find all view-elements required by activity
	 */
	private void initViews() {
		Toolbox.logTxt(this.getLocalClassName(), "initViews...) called");

		mStatusMessageField = (EditText) findViewById(R.id.RequestStatus_EditText);
		mConnectButton = (Button) findViewById(R.id.Connect_Button);
		mDisconnectButton = (Button) findViewById(R.id.Disconnect_Button);
		mPublishDeviceCharacteristicsButton = (Button) findViewById(R.id.PublishDeviceCharacteristics_Button);
	}

	/**
	 * initialize required application values
	 */
	private void initValues() {
		Toolbox.logTxt(this.getLocalClassName(), "initValues(...) called");

		// create new database connection
		mLogDB = new LoggingDatabase(this);

		// get device-properties-object
		mDeviceProperties = new DeviceProperties(this);

		// create receivers
		mBatteryReciever = new BatteryReceiver(this.getApplicationContext());

		// default status message on startup
		mStatusMessageField.append(getResources().getString(
				R.string.main_status_message_prefix)
				+ " "
				+ getResources()
						.getString(R.string.main_default_status_message));
	}

	/**
	 * get application preferences
	 */
	private void initPreferences() {
		Toolbox.logTxt(this.getLocalClassName(), "onPreferences(...) called");

		// object for holding preferences-values
		mPreferences = new PreferencesValues();

		// get the preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		// set preferences
		PreferencesValues.sApplicationFileLogging = prefs.getBoolean(
				"applicatiologging", false);
		PreferencesValues.sMonitoringPreference = prefs.getString(
				"serverPref", "IF-MAP");
		PreferencesValues.sLocationTrackingType = prefs.getString(
				"locationPref", "GPS");
		PreferencesValues.sEnableLocationTracking = prefs.getBoolean(
				"enableLocationTracking", false);
		PreferencesValues.sAutoUpdate = prefs.getBoolean("autoUpdate", false);
		mPreferences.setUseNonConformMetadata(prefs.getBoolean(
				"nonConformDataPreferences", true));
		mPreferences.setmSendApplicationsInfos(prefs.getBoolean(
				"sendNoAppsPreferences", false));
		mPreferences.setmAutostart(prefs.getBoolean("autostartPreferences",
				false));
		mPreferences.setmAutoconnect(prefs.getBoolean("autoconnectPreferences",
				false));
		mPreferences.setmDontSendGoogleApps(prefs.getBoolean(
				"sendNoGoogleAppsPreferences", true));
		mPreferences.setAllowUnsafeSSLPreference(prefs.getBoolean(
				"allowUnsafeSSLPreference", true));
		mPreferences.setEnableNewAndEndSessionLog(prefs.getBoolean(
				"logNewsessionRequest", false));
		mPreferences
				.setEnablePollLog(prefs.getBoolean("logPollRequest", false));
		mPreferences.setEnableSubscribe(prefs.getBoolean("logSubscripeRequest",
				false));
		mPreferences.setEnableLocationTrackingLog(prefs.getBoolean(
				"logLocationTracking", false));
		mPreferences.setEnablePublishCharacteristicsLog(prefs.getBoolean(
				"logPublishCharacteristics", false));
		mPreferences.setEnableErrorMessageLog(prefs.getBoolean(
				"logErrorMessage", false));
		mPreferences.setEnableInvalideResponseLog(prefs.getBoolean(
				"logInvalideResponse", false));
		mPreferences.setEnableRenewRequestLog(prefs.getBoolean(
				"logRenewRequest", false));
		mPreferences.setUsernamePreference(prefs.getString(
				"usernamePreference", "user"));
		mPreferences.setPasswordPreference(prefs.getString(
				"passwordPreference", "password"));
		mPreferences.setServerIpPreference(prefs.getString(
				"serveripPreference", ""));
		mPreferences.setServerPortPreference(prefs.getString(
				"serverportPreference", "8443"));
		mPreferences.setNscaEncPreference(prefs.getString(
				"nscaEncPref", "1"));
		mPreferences.setNscaPassPreference(prefs.getString(
				"imonitorPassPreference", "icinga"));
		mPreferences.setIsPermantConnection(prefs.getBoolean(
				"permanantlyConectionPreferences", true));
		mPreferences.setIsUseBasicAuth(prefs.getBoolean("basicAuth", true));

		// set update interval
		try {
			mPreferences.setmUpdateInterval(Long.parseLong(prefs.getString(
					"updateInterval", "60000l")));
		} catch (NumberFormatException e) {
			// should not happen! just in case of...
			Toolbox.logTxt(
					this.getLocalClassName(),
					"initializing of update interval from preferences failed...using default (60000)");
			mPreferences.setRenewIntervalPreference(30000L);
		}
		// check if update interval is above minimum, of not set it to
		// default minimum value
		if (mPreferences.getmUpdateInterval() < 60000L) {
			mPreferences.setRenewIntervalPreference(60000L);
			Toolbox.logTxt(this.getLocalClassName(),
					"configured update interval is to short...using default (60000)");
		}

		// set renew session interval
		try {
			mPreferences.setRenewIntervalPreference(Long.parseLong(prefs
					.getString("renewInterval", "10000l")));
		} catch (NumberFormatException e) {
			// should not happen! just in case of...
			Toolbox.logTxt(
					this.getLocalClassName(),
					"initializing of renew session interval from preferences failed...using default (10000)");
			mPreferences.setRenewIntervalPreference(10000L);
		}

		// check if renew-session interval is above minimum, of not set it to
		// default minimum value
		if (mPreferences.getRenewIntervalPreference() < 10000L) {
			mPreferences.setRenewIntervalPreference(10000L);
			Toolbox.logTxt(this.getLocalClassName(),
					"configured renew session interval is to short...using default (10000)");
		}
	}

	/**
	 * Initialize the connection object if not already initialized, else assign
	 * already existing connection object
	 */
	private void initConnection() {
		Toolbox.logTxt(this.getLocalClassName(), "initConnection(...) called");
		if (ConnectionObjects.getSsrcConnection() == null
				|| (mResponseType == MessageHandler.MSG_TYPE_ERRORMSG)) {
			try {

				// if unsafe ssl is activated, set related properties for ifmapj
				if (mPreferences.isAllowUnsafeSSLPreference()) {
					Toolbox.logTxt(this.getLocalClassName(),
							"using unsafe ssl - verifypeercert and host set to false");
					System.setProperty("ifmapj.communication.verifypeercert",
							"false");
					System.setProperty("ifmapj.communication.verifypeerhost",
							"false");
				} else {
					Toolbox.logTxt(this.getLocalClassName(), "using safe ssl");
					System.setProperty("ifmapj.communication.verifypeercert",
							"true");
					System.setProperty("ifmapj.communication.verifypeerhost",
							"");
				}

				if (mPreferences.isUseBasicAuth()) {
					// create ssrc-connection using basic-authentication
					Toolbox.logTxt(this.getLocalClassName(),
							"initializing ssrc-connecion using basic-auth");
					sSsrcConnection = IfmapJ.createSSRC("https://"
							+ mPreferences.getServerIpPreference() + ":"
							+ mPreferences.getServerPortPreference(),
							mPreferences.getUsernamePreference(), mPreferences
									.getPasswordPreference(), IfmapJHelper
									.getTrustManagers(getResources()
											.openRawResource(R.raw.keystore),
											"androidmap"));
				} else {
					// create ssrc-connection using certificates
					Toolbox.logTxt(this.getLocalClassName(),
							"initializing ssrc-connecion using certificate-based-auth");
					sSsrcConnection = IfmapJ.createSSRC("https://"
							+ mPreferences.getServerIpPreference() + ":"
							+ mPreferences.getServerPortPreference(),
							IfmapJHelper.getKeyManagers(getResources()
									.openRawResource(R.raw.keystore),
									"androidmap"), IfmapJHelper
									.getTrustManagers(getResources()
											.openRawResource(R.raw.keystore),
											"androidmap"));
				}

				mResponseType = 0;
			} catch (InitializationException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
			ConnectionObjects.setSsrcConnection(sSsrcConnection);
		} else {
			sSsrcConnection = ConnectionObjects.getSsrcConnection();
		}
	}

	/**
	 * initialize the LocationManager class to obtain GPS locations
	 */
	private void initLocation() {
		Toolbox.logTxt(this.getLocalClassName(), "initLocation(...) called");

		// delete previous location-tracking-data
		sLongitude = null;
		sLatitude = null;
		sAltitude = null;

		// initialize location manager
		mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocListener = new LocationObserver();
		mLocListener.setAppllicationContext(this);

		// gps
		if (PreferencesValues.sLocationTrackingType.equalsIgnoreCase("gps")) {
			mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					mPreferences.getmUpdateInterval(), 0, mLocListener);
		}

		// cell based
		else {
			mLocManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					mPreferences.getmUpdateInterval(), 0, mLocListener);
		}
	}

	// -------------------------------------------------------------------------
	// BUTTON HANDLING
	// -------------------------------------------------------------------------

	/**
	 * enable/disable buttons depending on current application state (detected
	 * by mIsConnected-Flag)
	 * 
	 * @param isConnected
	 *            boolean indicating current connection state
	 * @param isSubscribed
	 *            flag indicating if client is currently subscribed to searches
	 */
	private void changeButtonStates(boolean isConnected) {
		// connected to if map server
		if (isConnected) {
			mConnectButton.setEnabled(false);
			mDisconnectButton.setEnabled(true);
			if (PreferencesValues.sAutoUpdate) {
				mPublishDeviceCharacteristicsButton.setEnabled(false);
			} else {
				mPublishDeviceCharacteristicsButton.setEnabled(true);
			}
		}
		// not connected
		else {
			mConnectButton.setEnabled(true);
			mDisconnectButton.setEnabled(false);
			mPublishDeviceCharacteristicsButton.setEnabled(false);
		}
	}

	/**
	 * Handler for Main-Tab Buttons
	 * 
	 * @param view
	 *            element that originated the call
	 */
	public void mainTabButtonHandler(View view) {
		if(PreferencesValues.sMonitoringPreference.equalsIgnoreCase("IF-MAP")) {
			mainTabButtonHandlerIfmap(view);
		}
		else {
			mainTabButtonHandlerIMonitor(view);
		}
	}

	private void mainTabButtonHandlerIMonitor(View view) {
		if (mIsConnected) {
			switch (view.getId()) {
			case R.id.Disconnect_Button:
				mNscaServiceBind.stopMonitor();
				getApplicationContext().unbindService(mNscaConnection);

				mConnectButton.setEnabled(true);
				mDisconnectButton.setEnabled(false);
				mIsConnected = false;
				mStatusMessageField.append("\n"
						+ getResources().getString(
								R.string.main_status_message_prefix) + " "
						+ "disconnected.");
				
				PreferencesValues.sLockPreferences = false;
				PreferencesValues.sLockConnectionPreferences = false;
				PreferencesValues.sLockIMonitorPreferences = false;

				break;
			}
		}

		// not connected to imonitor
		else {
			switch (view.getId()) {
			case R.id.Connect_Button:
				// start connection service if all required preferences are set
				if (!validatePreferences()) {
					mStatusMessageField.append("\n"
							+ getResources().getString(
									R.string.main_status_message_errorprefix)
							+ " "
							+ getResources().getString(
									R.string.main_default_wrongconfig_message));
				} else {
					// set status message to-text-output-field
					mStatusMessageField.append("\n"
							+ getResources().getString(
									R.string.main_status_message_prefix) + " "
							+ "Sending data to "
							+ mPreferences.getServerIpPreference() + ":"
							+ mPreferences.getServerPortPreference());
					getApplicationContext().bindService(
							new Intent(this, NscaService.class),
							mNscaConnection, Context.BIND_AUTO_CREATE);
					
					PreferencesValues.sLockPreferences = true;
					PreferencesValues.sLockConnectionPreferences = true;
					PreferencesValues.sLockIMonitorPreferences = true;
					
					mIsConnected = true;
					mConnectButton.setEnabled(false);
					mDisconnectButton.setEnabled(true);
				}

				break;
			}
		}
	}

	private void mainTabButtonHandlerIfmap(View view) {
		if (mIsConnected) {
			switch (view.getId()) {
			// close session button
			case R.id.Disconnect_Button:
				mMessageType = MessageHandler.MSG_TYPE_REQUEST_ENDSESSION;
				break;
			// publish device characteristics-button
			case R.id.PublishDeviceCharacteristics_Button:

				// disable buttons before starting request-generation
				mConnectButton.setEnabled(false);
				mDisconnectButton.setEnabled(false);
				mPublishDeviceCharacteristicsButton.setEnabled(false);

				mMessageType = MessageHandler.MSG_TYPE_PUBLISH_CHARACTERISTICS;
				break;
			}
		}

		// not connected to map server
		else {
			switch (view.getId()) {
			// start new session button
			case R.id.Connect_Button:
				mMessageType = MessageHandler.MSG_TYPE_REQUEST_NEWSESSION;
				break;
			}
		}

		// start connection service if all required preferences are set
		if (!validatePreferences()) {
			mStatusMessageField.append("\n"
					+ getResources().getString(
							R.string.main_status_message_errorprefix)
					+ " "
					+ getResources().getString(
							R.string.main_default_wrongconfig_message));
		} else {
			// set status message to-text-output-field
			mStatusMessageField.append("\n"
					+ getResources().getString(
							R.string.main_status_message_prefix) + " "
					+ "Sending Request to "
					+ mPreferences.getServerIpPreference() + ":"
					+ mPreferences.getServerPortPreference());
			initConnection();
			startConnectionService();
		}
	}

	/**
	 * override the behavior of the back-button so that the application runs in
	 * the background
	 */
	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}

	// -------------------------------------------------------------------------
	// CONNECTION/SERVICE-START HANDLING
	// -------------------------------------------------------------------------

	/**
	 * check if the preference values are valid
	 */
	public boolean validatePreferences() {
		boolean ipValid = false;
		boolean portValid = false;
		boolean authValid = false;
		boolean userValid = false;
		boolean passValid = false;

		// validate ip-setting from preferences
		if (mPreferences.getServerIpPreference() != null
				&& mPreferences.getServerIpPreference().length() > 0) {
			Matcher ipMatcher = Toolbox.getIpPattern().matcher(
					mPreferences.getServerIpPreference());
			if (ipMatcher.find()) {
				ipValid = true;
			}
		}
		// validate portnumber
		if (mPreferences.getServerPortPreference() != null
				&& mPreferences.getServerPortPreference().length() > 0) {
			portValid = true;
		}

		if (mPreferences.isUseBasicAuth()) {
			// validate username
			if (mPreferences.getUsernamePreference() != null
					&& mPreferences.getUsernamePreference().length() > 0) {
				userValid = true;
			}
			// validate password
			if (mPreferences.getPasswordPreference() != null
					&& mPreferences.getPasswordPreference().length() > 0) {
				passValid = true;
			}
		} else {
			authValid = true;
		}

		if (PreferencesValues.sMonitoringPreference.equalsIgnoreCase("iMonitor")) {
			// validate password (defaults always to "icinga")
			if (mPreferences.getNscaPassPreference() != null
					&& mPreferences.getNscaPassPreference().length() > 0) {
				passValid = true;
			}
		}

		if (ipValid && portValid && (authValid || (userValid && passValid))) {
			return true;
		}

		return false;
	}

	/**
	 * start the connection-service to connect to the Map-Server
	 */
	public void startConnectionService() {
		Toolbox.logTxt(this.getLocalClassName(),
				"startConnectionService(...) called");

		// disable buttons before starting request-generation
		mConnectButton.setEnabled(false);
		mDisconnectButton.setEnabled(false);
		mPublishDeviceCharacteristicsButton.setEnabled(false);

		mStatusMessageField.append("\n"
				+ getResources().getString(R.string.main_status_message_prefix)
				+ " " + "preparing data for request");

		PublishRequest publishReq = parameters.generateSRCRequestParamteres(
				mMessageType, mDeviceProperties,
				mPreferences.isUseNonConformMetadata(),
				mPreferences.ismSendApplicationsInfos(),
				mPreferences.ismDontSendGoogleApps());

		if (!mPreferences.isIsPermantConnection()) {
			// gather parameters for local service
			mLocalServicePreferences = new LocalServiceParameters(
					LocalServiceParameters.SERVICE_BINDER_TYPE_RENEW_CONNECTION_SERVICE,
					mPreferences, mDeviceProperties.getSystemProperties()
							.getLocalIpAddress(), mMessageType, publishReq,
					mMsgHandler);

			// initialize and bind local service
			mConnection = LocalServiceSynchronous.getConnection(
					getApplicationContext(), mLocalServicePreferences,
					new SynchronousRunnable(), Toolbox
							.generateRequestLogMessageFromPublishRequest(
									mMessageType, publishReq));
			mIsBound = BinderClass.doBindRenewConnectionService(
					getApplicationContext(), mConnection);

		} else {
			// gather parameters for permanent connection
			mLocalServicePreferences = new LocalServiceParameters(
					LocalServiceParameters.SERVICE_BINDER_TYPE_PERMANENT_CONNECTION_SERVICE,
					mPreferences, mDeviceProperties.getSystemProperties()
							.getLocalIpAddress(), mMessageType, publishReq,
					mMsgHandler);

			// initialize and bind local service
			mPermConnection = LocalServicePermanent.getPermConnection(
					getApplicationContext(), mLocalServicePreferences,
					new PermanentRunnable(), Toolbox
							.generateRequestLogMessageFromPublishRequest(
									mMessageType, publishReq));

			mIsBound = BinderClass.doBindPermConnectionService(
					getApplicationContext(), mPermConnection);
		}

		// show progress dialog
		myProgressDialog = ProgressDialog.show(
				MainActivity.this,
				getResources().getString(
						R.string.main_progressbar_message_srcrequest_1),
				getResources().getString(
						R.string.main_progressbar_message_srcrequest_2), true,
				false);
	}

	// -------------------------------------------------------------------------
	// SERVICE CALLBACK HANDLING
	// -------------------------------------------------------------------------

	/**
	 * Callback-Handler for Local Service that handles synchronous
	 * Server-Responses (renew-session-method)
	 */
	public class SynchronousRunnable implements Runnable {
		public ResponseParameters msg;
		public byte responseType;
		public LogMessage logRequestMsg;
		public LogMessage logResponseMsg;

		@Override
		public void run() {

			// process server-result
			processSRCResponseParameters(responseType, msg, logRequestMsg,
					logResponseMsg);

			// unbind service
			if (sBoundRenewConnService != null) {
				mIsBound = UnbinderClass.doUnbindConnectionService(
						getApplicationContext(), mConnection, myProgressDialog,
						mIsBound);
				sBoundRenewConnService = null;
			}
		}
	}

	/**
	 * Callback-Handler for Local Service that handles synchronous
	 * Server-Responses (permanent-connection-method)
	 */
	public class PermanentRunnable implements Runnable {
		public ResponseParameters msg;
		public byte responseType;
		public LogMessage logRequestMsg;
		public LogMessage logResponseMsg;

		@Override
		public void run() {
			// process server-result
			processSRCResponseParameters(responseType, msg, logRequestMsg,
					logResponseMsg);
			mResponseType = responseType;

			// unbind service
			if (sBoundPermConnService != null) {
				mIsBound = UnbinderClass.doUnbindConnectionService(
						getApplicationContext(), mPermConnection,
						myProgressDialog, mIsBound);
				sBoundPermConnService = null;
			}
		}
	}

	// -------------------------------------------------------------------------
	// RESPONSE-MESSAGE-HANDLING
	// -------------------------------------------------------------------------

	/**
	 * process incoming response-parameters from Map-Server Response on
	 * src-channel and set resulting application states
	 * 
	 * @param messageType
	 *            type of message to process
	 * @param msg
	 *            response-parameters-object to process
	 * @param requestMsg
	 *            log-message containing outgoing parameters send to MAP-Server
	 * @param responseMsg
	 *            log-message containing incoming parameters received from
	 *            MAP-Server
	 */
	public void processSRCResponseParameters(byte messageType,
			ResponseParameters msg, LogMessage requestMsg,
			LogMessage responseMsg) {
		switch (messageType) {

		// -----> NEW SESSION RESPONSE <-----
		case MessageHandler.MSG_TYPE_REQUEST_NEWSESSION:
			mIsConnected = true;

			// lock parts of preferences-tab that cannot be changed as
			// long as a connection is established
			PreferencesValues.sLockPreferences = true;
			PreferencesValues.sLockConnectionPreferences = true;
			PreferencesValues.sLockLocationTrackingOptions = true;

			mCurrentSessionId = msg
					.getParameter(ResponseParameters.RESPONSE_PARAMS_SESSIONID);
			sCurrentPublisherId = msg
					.getParameter(ResponseParameters.RESPONSE_PARAMS_PUBLISHERID);

			// in case of renew-session method, start renew-session-handler
			// to periodically send renew-session-requests
			if (sBoundRenewConnService != null) {

				// (re)initialize handler for periodically sending location-data
				if (mRenewHandler == null) {
					mRenewHandler = new Handler();
				}
				mRenewHandler.removeCallbacks(mUpdateRenewTimeTask);
				mRenewHandler.postDelayed(mUpdateRenewTimeTask, 15000);
			}

			// (re)initialize handler for periodically sending meta-data
			if (mUpdateHandler == null) {
				mUpdateHandler = new Handler();
			}

			mUpdateHandler.removeCallbacks(mUpdateTimeTask);
			mUpdateHandler.postDelayed(mUpdateTimeTask, 2000);
			break;

		// -----> END SESSION RESPONSE <-----
		case MessageHandler.MSG_TYPE_REQUEST_ENDSESSION:
			mIsConnected = false;

			// "unlock" some parts of preferences
			PreferencesValues.sLockPreferences = false;
			PreferencesValues.sLockConnectionPreferences = false;
      PreferencesValues.sLockLocationTrackingOptions = false;
			mCurrentSessionId = null; // session has ended!

			// deactivate handler for sending metadata and renew-messages to
			// server
			if (mRenewHandler != null) {
				mRenewHandler.removeCallbacksAndMessages(null);
				mRenewHandler = null;
			}
			if (mUpdateHandler != null) {
				mUpdateHandler.removeCallbacksAndMessages(null);
				mUpdateHandler = null;
			}
			break;

		// -----> ERROR-MESSAGE RESPONSE <-----
		case MessageHandler.MSG_TYPE_ERRORMSG:
			// error-response, reset all messaging-related values
			mIsConnected = false;
			PreferencesValues.sLockPreferences = false;
			PreferencesValues.sLockLocationTrackingOptions = false;
			mCurrentSessionId = null;
			MessageParametersGenerator.sInitialDevCharWasSend = false;

			// deactivate handler for sending renew-messages to server
			if (mRenewHandler != null) {
				mRenewHandler.removeCallbacksAndMessages(null);
				mRenewHandler = null;
			}
			break;

		// -----> PUBLISH DEVICE CHARACTERISTICS RESPONSE <-----
		case MessageHandler.MSG_TYPE_PUBLISH_CHARACTERISTICS:
			mIsConnected = true;
			PreferencesValues.sLockPreferences = true;
			break;
		}

		// set output to main-text-output-field
		if (messageType != MessageHandler.MSG_TYPE_ERRORMSG) {
			mStatusMessageField
					.append("\n"
							+ getResources().getString(
									R.string.main_status_message_prefix)
							+ " "
							+ (msg.getParameter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG)));
		} else {
			// in case of error message, add a text-prefix
			mStatusMessageField
					.append("\n"
							+ getResources().getString(
									R.string.main_status_message_errorprefix)
							+ " "
							+ (msg.getParameter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG)));
		}

		// set notification about incoming response
		Toolbox.showNotification(
				getResources().getString(
						R.string.main_notification_message_label),
				getResources().getString(
						R.string.main_notification_message_message),
				msg.getParameter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG),
				getApplicationContext());

		// add collected Log Messages from Request/Response to Log-Message-List
		if (messageType == MessageHandler.MSG_TYPE_PUBLISH_CHARACTERISTICS
				|| messageType == MessageHandler.MSG_TYPE_METADATA_UPDATE) {
			// currently, the esukom-specific data is to much for the db to
			// handle
			// so for now we disable logging in this case
			if (mPreferences.isUseNonConformMetadata()) {
				requestMsg
						.setMsg("logging of esukom specific metadata is currently not supported!\n");
				responseMsg
						.setMsg("logging of esukom specific metadata is currently not supported!\n");
			}
		}
		LogMessageHelper.getInstance().logMessage(messageType, requestMsg,
				responseMsg, mPreferences, mLogDB);

		// change activity button states
		changeButtonStates(mIsConnected);
	}

	// -------------------------------------------------------------------------
	// RENEW SESSION HANDLING
	// -------------------------------------------------------------------------

	/**
	 * handler which is executed at predefined interval, sends renews-session
	 * messages
	 */
	private Handler mRenewHandler = new Handler();
	private Runnable mUpdateRenewTimeTask = new Runnable() {
		public void run() {
			sendRenewSessionToServer();
			mRenewHandler.postDelayed(this,
					mPreferences.getRenewRequestMinInterval());
		}
	};

	/**
	 * Triggers the sending of the renew-session message
	 */
	public void sendRenewSessionToServer() {
		Toolbox.logTxt(this.getLocalClassName(), "sendRenewSession(...) called");
		mMessageType = MessageHandler.MSG_TYPE_REQUEST_RENEWSESSION;
		// generate publish-request-object
		PublishRequest publishReq = parameters.generateSRCRequestParamteres(
				mMessageType, mDeviceProperties,
				mPreferences.isUseNonConformMetadata(),
				mPreferences.ismSendApplicationsInfos(),
				mPreferences.ismDontSendGoogleApps());

		if (!mIsBound) {
			// renew-session-connection
			// gather local service parameters
			mLocalServicePreferences = new LocalServiceParameters(
					LocalServiceParameters.SERVICE_BINDER_TYPE_RENEW_CONNECTION_SERVICE,
					mPreferences, mDeviceProperties.getSystemProperties()
							.getLocalIpAddress(), mMessageType, publishReq,
					mMsgHandler);

			// initialize and bind service
			mConnection = LocalServiceSynchronous.getConnection(
					getApplicationContext(), mLocalServicePreferences,
					new SynchronousRunnable(), Toolbox
							.generateRequestLogMessageFromPublishRequest(
									mMessageType, publishReq));
			mIsBound = BinderClass.doBindRenewConnectionService(
					getApplicationContext(), mConnection);
		}
	}

	// -------------------------------------------------------------------------
	// LOCATION TRACKING HANDLING
	// -------------------------------------------------------------------------

	/**
	 * Lets set the current position in the status tab if it is already active
	 * 
	 * @param latitude
	 *            current latitude value
	 * @param longitude
	 *            current longitude value
	 * @param altitude
	 *            current altitude value
	 */
	public void setCurrentLocation(double latitude, double longitude,
			double altitude) {
		sLatitude = String.valueOf(latitude);
		sLongitude = String.valueOf(longitude);
		sAltitude = String.valueOf(altitude);

		// if status activity is active, pass current location values to it
		if (StatusActivity.sIsActivityActive) {
			StatusActivity.setCurrentLocation(latitude, longitude, altitude);
		}
	}

	// -------------------------------------------------------------------------
	// AUTO-UPDATE-HANDLING
	// -------------------------------------------------------------------------

	/**
	 * handler which is executed at predefined interval, sends the current
	 * location to the map-server
	 */
	private Handler mUpdateHandler = new Handler();
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if (PreferencesValues.sAutoUpdate) {
				sendMetadataUpdateToServer();
				mUpdateHandler.postDelayed(this,
						mPreferences.getmUpdateInterval());
			}
		}
	};

	/**
	 * Triggers the sending of device characteristics and location-metadata, if
	 * a connection to the server is established
	 */
	public void sendMetadataUpdateToServer() {
		if (mIsConnected) {
			mMessageType = MessageHandler.MSG_TYPE_METADATA_UPDATE;
			startConnectionService();
		}
	}

	// -------------------------------------------------------------------------
	// iMonitor-Connection
	// -------------------------------------------------------------------------

	/**
	 * create service connection
	 * send InfoEvent and AppEvent on first connect
	 */
	private ServiceConnection mNscaConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        mNscaServiceBind = binder.getService();

	        Encryption mNscaEncryption = null;
	        switch(mPreferences.getNscaEncPreference()) {
	        case "0":
	            mNscaEncryption = Encryption.NONE;
	            break;
	        case "1":
	            mNscaEncryption = Encryption.XOR;
	            break;
	        case "2":
	            mNscaEncryption = Encryption.TRIPLE_DES;
	            break;
	        }

	        mNscaServiceBind.setupConnection(mPreferences.getServerIpPreference(), mPreferences.getServerPortPreference(), mPreferences.getNscaPassPreference(), mNscaEncryption);

	        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMonitorEventReceiver, new IntentFilter("iMonitor-Event"));

	        EventParameters eP = new EventParameters(mDeviceProperties);
	        mNscaServiceBind.publish(eP.genInfoEvent());
	        mNscaServiceBind.publish(eP.genAppEvents());

	        mNscaServiceBind.startMonitor(mPreferences.getmUpdateInterval());
	        mIsBound = true;
	    }

	    public void onServiceDisconnected(ComponentName arg0) {
	        mIsBound = false;
	    }
	};

	/**
	 * receive (local) intents to generate and publish new events or drop connection
	 */
	private BroadcastReceiver mMonitorEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			EventParameters eP = new EventParameters(mDeviceProperties);
			String type = intent.getStringExtra("Event");
			if (type != null) {
				switch (type) {
				case "AppEvent":
					mNscaServiceBind.publish(eP.genAppEvents());
					mStatusMessageField.append("\n"
							+ getResources().getString(
									R.string.main_status_message_prefix) + " "
							+ "AppEvent sent.");
					break;
				case "MonitorEvent":
					mNscaServiceBind.publish(eP.genMonitorEvent());
					mStatusMessageField.append("\n"
							+ getResources().getString(
									R.string.main_status_message_prefix) + " "
							+ "MonitorEvent sent.");
					break;
				case "ConnectionError":
					mStatusMessageField
							.append("\n"
									+ getResources()
											.getString(
													R.string.main_default_connectionerror_nsca));
					mDisconnectButton.performClick();
					break;
				}
			}
		}
	};
}
