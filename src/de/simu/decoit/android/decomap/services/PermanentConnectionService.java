/* 
 * PermanentConnectionService.java       0.1.6 07/02/12
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
package de.simu.decoit.android.decomap.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.simu.decoit.android.decomap.activities.MainActivity;
import de.simu.decoit.android.decomap.connection.SyncConnectionThread;
import de.simu.decoit.android.decomap.logging.LogMessage;
import de.simu.decoit.android.decomap.logging.LogMessageHelper;
import de.simu.decoit.android.decomap.messaging.MessageHandler;
import de.simu.decoit.android.decomap.messaging.ResponseParameters;
import de.simu.decoit.android.decomap.util.Toolbox;

/**
 * Local Service for sending synchronous messages with a permanently connection
 * to the ifmap server.
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Marcel Jahnke, DECOIT GmbH
 * 
 * @version 0.1.6
 */
public class PermanentConnectionService extends Service {

	// some connection properties (used for connecting and log messages)
	private String mServerAddress;
	private String mServerPort;
	private String mClientAddress;

	// Callback-Handler for Activity that started the service
	private Handler mCallbackHandler;
	private MainActivity.PermanentRunnable mRunnable;

	// callback from Connection-Thread to service
	private PermanentConnectionService mCallback = this;

	// service binder
	private final IBinder mBinder = new LocalBinder();

	/*
	 * Values for Log-Messages. Will be assigned during handleServerRequest and
	 * handlerServerResponse Method Calls and will be passed to relating
	 * Activity via Callback-Handler afterwards (see (As)SynchronousRunnable)
	 */
	private LogMessage mLogRequestMessage;
	private LogMessage mLogResponseMessage;

	// interface for service binding
	public interface IPermanentConnectionService {

		/**
		 * Connect to Server using passed in Connection-Type and Message for
		 * publishing
		 * 
		 * @param adr
		 *            server i.p.
		 * @param prt
		 *            server por
		 * @param clientIP
		 *            device/client ip adress
		 * @param quest
		 *            message(type) to send to the server
		 * @param params
		 *            PublishRequest Object that contains the request
		 * @param msgContent
		 *            contains the message data for logging
		 */
		void connect(String adr, String prt, String clientIP, byte quest, PublishRequest params, String msgContent);
	}

	/**
	 * service binder-class
	 */
	public class LocalBinder extends Binder implements IPermanentConnectionService {
		Thread sslThread = null;
		SyncConnectionThread sslClient = null;

		public void setActivityCallbackHandler(final Handler callback) {
			mCallbackHandler = callback;
		}

		public void setRunnable(final MainActivity.PermanentRunnable runnable) {
			mRunnable = runnable;
		}

		@Override
		public void connect(String adr, String prt, String clientIP, byte quest, PublishRequest params, String msgContext) {
			Toolbox.logTxt("PermanetConnectionService", "connect(...) called");

			// some connection params, used for logging, etc.
			mServerAddress = adr;
			//mServerPort = new Integer(prt).toString();
			mServerPort = prt;
			mClientAddress = clientIP;

			//Toolbox.logTxt("PermanetConnectionService", "outgoing message: " + msgContext);

			// create log message
			mLogRequestMessage = LogMessageHelper.getInstance().generateRequestLogMessage(quest, msgContext,
					mServerAddress, mServerPort);

			// start communication
			new SyncConnectionThread(mCallback, quest, params);
		}
	}

	/**
	 * Callback-Method for Connection-Thread, will be called when Thread
	 * finishes. Will pass the passed in Message to the Activity that started
	 * the Connection Service via Callback-Handler
	 * 
	 * @param responseMessageType
	 *            type of response message
	 * @param msg
	 *            server message
	 */
	public void handleServerResponse(byte responseMessageType, String msg) {
		Toolbox.logTxt("PermanetConnectionService", "handleServerResponse(...) called");

		// get message from incoming server response
		ResponseParameters responseParams = MessageHandler.getInstance().getResponseParameters(responseMessageType,
				msg, false, getApplicationContext());

		Toolbox.logTxt("PermanetConnectionService", "incoming message: " + msg);
	    Toolbox.logTxt("PermanetConnectionService", "END OF incoming message!! ");
	    
	    
		// create response-log message
		mLogResponseMessage = LogMessageHelper.getInstance().generateResponseLogMessage(responseMessageType,
				responseParams, mClientAddress);

		// assign log messages to callback
		mRunnable.logRequestMsg = mLogRequestMessage;
		mRunnable.logResponseMsg = mLogResponseMessage;

		mRunnable.msg = responseParams;
		mRunnable.responseType = responseMessageType;
		mCallbackHandler.post(mRunnable);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky. This Method will only be called when
		// service is started via startService(...)
		return Service.START_STICKY;
	}
}
