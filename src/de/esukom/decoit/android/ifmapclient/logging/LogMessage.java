/* 
 * LogMessage.java        0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.logging;

/**
 * Class representing a single Log-Message
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @version 0.1.6
 */
public class LogMessage {
	private int mId;
	private String mMsg;
	private String mTimestamp;
	private String mMsgType;
	private String mTarget;
	private String mStatus;

	
	/**
	 * constructor without id...is used for generating new log-messages
	 * for inserting into database
	 * 
	 * id is auto generated (auto-increment value) in insert-case, so we
	 * don´t need it for this task
	 * 
	 * @param timestamp
	 *            current date/time
	 * @param message
	 *            message content
	 * @param msgType
	 *            type of message
	 * @param hostIP
	 *            target IP-Address
	 * @param hostPort
	 *            target port number
	 * @param status
	 *            status/result of request/response-message
	 */
	public LogMessage(String timestamp, String message, String msgType,
			String target, String status) {
		super();
		this.mMsg = message;
		this.mTimestamp = timestamp;
		this.mMsgType = msgType;
		this.mTarget = target;
		this.mStatus = status;
		
	}
	
	/**
	 * constructor with id...this is used when creating message for display
	 * the internally generated id from DB is used in this case for the
	 * navigation inside the log-message list view
	 * 
	 * @param timestamp
	 *            current date/time
	 * @param message
	 *            message content
	 * @param msgType
	 *            type of message
	 * @param hostIP
	 *            target IP-Address
	 * @param hostPort
	 *            target port number
	 * @param status
	 *            status/result of request/response-message
	 */
	public LogMessage(int aid,String timestamp, String message, String msgType,
			String target, String status) {
		super();

		this.mMsg = message;
		this.mTimestamp = timestamp;
		this.mMsgType = msgType;
		this.mTarget = target;
		this.mStatus = status;
		this.mId = aid;
	}

	/**
	 * get request/response-message status
	 * 
	 * @return String message status
	 */
	public String getStatus() {
		return mStatus;
	}

	/**
	 * set request/response mssage status
	 * 
	 * @param msgStatus
	 *            message status
	 */
	public void setStatus(String msgStatus) {
		this.mStatus = msgStatus;
	}

	/**
	 * get message-content
	 * 
	 * @return String content of message
	 */
	public String getMsg() {
		return mMsg;
	}

	/**
	 * set message content
	 * 
	 * @param msg
	 *            content of message
	 */
	public void setMsg(String msg) {
		this.mMsg = msg;
	}

	/**
	 * get target IP-Address
	 * 
	 * @return String target IP
	 */
	public String getTarget() {
		return mTarget;
	}

	/**
	 * set target IP-Address
	 * 
	 * @param hostIp
	 *            target IP
	 */
	public void setTarget(String target) {
		this.mTarget = target;
	}

	
	/**
	 * get timsestamp string
	 * 
	 * @return String timestamp-string(Date/Time)
	 */
	public String getTimestamp() {
		return mTimestamp;
	}

	/**
	 * set timestamp string
	 * 
	 * @param timestamp
	 *            timestamp-string(date/time)
	 */
	public void setTimestamp(String timestamp) {
		this.mTimestamp = timestamp;
	}

	/**
	 * get type of message, corresponding to static message-type values in
	 * ConnectionService-Class
	 * 
	 * @return type of message as String
	 */
	public String getMsgType() {
		return mMsgType;
	}

	/**
	 * set type of message, corresponding to static message-type values in
	 * ConnectionService-Class
	 * 
	 * @param msgType
	 *            message type as string
	 */
	public void setMsgType(String msgType) {
		this.mMsgType = msgType;
	}

	/**
	 * get log-message id
	 * 
	 * @return id of log message
	 */
	public int getId() {
		return mId;
	}

	/**
	 * set log-message id
	 * 
	 * @param id the id of the log message
	 */
	public void setId(int id) {
		this.mId = id;
	}
}
