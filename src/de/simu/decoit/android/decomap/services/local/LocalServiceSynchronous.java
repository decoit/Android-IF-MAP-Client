/*
 * LocalServiceSynchronous.java       0.1.6 07/02/12
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

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import de.simu.decoit.android.decomap.activities.MainActivity;
import de.simu.decoit.android.decomap.activities.MainActivity.SynchronousRunnable;
import de.simu.decoit.android.decomap.services.RenewConnectionService;

/**
 * This class is used for the outsourcing of the instantiation of mConnection
 * variable and his overriden methods
 * 
 * @author  Marcel Jahnke, DECOIT GmbH
 * @author  Dennis Dunekacke, Decoit GmbH
 * @version 0.1.6
 */
public class LocalServiceSynchronous {

	/**
	 * 
	 * get local service for synchronous communication (renew) using the
	 * "renew-session" approach
	 * 
	 * @return bound local service
	 * 
	 * @return the Object of ServiceConnection
	 */
	public static ServiceConnection getConnection(final Context mainActivity, final LocalServiceParameters values,
			final SynchronousRunnable callbackHandler, final String msgContext) {
		ServiceConnection mConnection = new ServiceConnection() {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			public void onServiceConnected(ComponentName className, IBinder service) {
				MainActivity.sBoundRenewConnService = (RenewConnectionService.LocalBinder) service;
				MainActivity.sBoundRenewConnService.setActivityCallbackHandler(values.getmMsgHandler());
				MainActivity.sBoundRenewConnService.setRunnable(callbackHandler);

				// connect to local service
				MainActivity.sBoundRenewConnService
						.connect(values.getmServerIpPreference(), values.getmServerPort(), values.getmIpAddress(),
								values.getmMessageType(), values.getmReguestParamsPublish(), msgContext);

			}

			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			public void onServiceDisconnected(ComponentName className) {
				MainActivity.sBoundRenewConnService = null;
			}
		};
		return mConnection;
	}
}
