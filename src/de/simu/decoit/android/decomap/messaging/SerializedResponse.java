/*
 * SerializedResponse.java        0.1.6 07/02/12
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
package de.simu.decoit.android.decomap.messaging;

import android.content.Context;
import de.simu.decoit.android.decomap.activities.R;

/**
 * Class representing a serialized server-response-object
 * 
 * @author  Marcel Jahnke, DECOIT GmbH
 * @author  Dennis Dunekacke, DECOIT GmbH
 * @version 0.1.6
 */
public class SerializedResponse {

	// error-strings (more precise: parts of error strings) from ifmapj
	// used to detect type of error and map it to our own error-messages
	private final String mIfMapJNull = "null";
	private final String mIfMapJTimeOut = "timed out";
	private final String mIfMapJServerNotResponding = "The target server failed to respond";
	private final String mIfMapJIOError = "I/O error";
	private final String mIfMapJNotAuthorized = "Unauthorized";
	private final String mIfMapJConnectionRefused = "refused";
	private final String mIfMapJNetworkUnreachable = "unreachable";
	private final String mIfMapJForbidden = "403";
	

	private String mSerializedMsg = null;
	private String mSessionID = null;
	private String mPublisherId = null;

	/**
	 * @return the serializes message
	 */
	public String getSerializedMsg() {
		return mSerializedMsg;
	}

	/**
	 * Format passed in server-response message or generate error message, when
	 * an error is detected.
	 * 
	 * @param response
	 *            The response message from the server
	 * @param msgType
	 *            type of the response message
	 * @param context
	 *            application/service context, used to access
	 *            String-Ressource-File
	 */
	public void setSerializedMsg(String response, byte msgType, Context context) {
		switch (msgType) {

		case MessageHandler.MSG_TYPE_ERRORMSG:
			// detect error-type and set relating error-message
			if (response.contains(mIfMapJNull)) {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_null);
			} else if (response.contains(mIfMapJTimeOut)) {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_time_out);
			} else if (response.contains(mIfMapJServerNotResponding)) {
				mSerializedMsg = context.getResources().getString(
						R.string.serialized_response_errormsg_server_not_responding);
			} else if (response.contains(mIfMapJIOError)) {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_io_error);
			} else if (response.contains(mIfMapJNotAuthorized)) {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_Unauthorized);
			} else if (response.contains(mIfMapJConnectionRefused)) {
				mSerializedMsg = context.getResources().getString(
						R.string.serialized_response_errormsg_connection_refused);
			} else if (response.contains(mIfMapJNetworkUnreachable)) {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_unreachable);
			}else if (response.contains(mIfMapJForbidden)) {
					mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_forbidden);
				}  
			else {
				mSerializedMsg = context.getResources().getString(R.string.serialized_response_errormsg_other);
			}
			break;

		default:
			// convert server-message
			String message[] = response.trim().split("\\,");
			for (int i = 0; i < message.length; i++) {
				if (message[i].startsWith("session-id")) {
					setSessionID(message[i].substring("session-id".length()));
				} else if (message[i].startsWith("publisher-id")) {
					setPublisherId(message[i].substring("publisher-id".length()));
				}
			}
			mSerializedMsg = response.replaceAll(",", "\n").replace("{", "").replace("}", "").trim();
			break;
		}
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionID() {
		return mSessionID;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionID(String sessionID) {
		this.mSessionID = sessionID;
	}

	/**
	 * @return the publisherId
	 */
	public String getPublisherId() {
		return mPublisherId;
	}

	/**
	 * @param publisherId
	 *            the publisherId to set
	 */
	public void setPublisherId(String publisherId) {
		this.mPublisherId = publisherId;
	}
}
