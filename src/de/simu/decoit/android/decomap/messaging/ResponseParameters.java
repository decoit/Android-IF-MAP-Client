/* 
 * ResponseParameters.java        0.1.6 07/02/12
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

/**
 * class for holding request parameters
 * 
 * @author 	Dennis Dunekacke, DECOIT GmbH
 * @author	Marcel Jahnke, DECOIT GmbH
 * @version	0.1.6
 */
public class ResponseParameters {

	public static final byte RESPONSE_PARAMS_STATUSMSG = 0;
	public static final byte RESPONSE_PARAMS_MSGCONTENT = 1;
	public static final byte RESPONSE_PARAMS_SESSIONID = 2;
	public static final byte RESPONSE_PARAMS_ERRORMSG = 3;
	public static final byte RESPONSE_PARAMS_PUBLISHERID = 4;
	
	public static final int RESPONSE_PARAMS_NUM = 5;

	private String[] mParams;
	
	/**
	 * constructor
	 */
	public ResponseParameters(){
		mParams = new String[RESPONSE_PARAMS_NUM];
	}
	
	/**
	 * get all device properties
	 * 
	 * @return	string[][]	list of device properties
	 */
	public String[] getParameters(){
		return mParams;
	}
	
	/**
	 * get a parameter specified by passed in byte
	 * 
	 * @param parameter	desired parameter
	 * 
	 * @return	String parameter value
	 */
	public String getParameter(byte parameter){
		return mParams[parameter];
	}
	
	/**
	 * set a parameter specified by passed in byte
	 * 
	 * @param parameter	parameter to set
	 * @param value		parameter value
	 */
	public void setParamter(byte parameter, String value){
		mParams[parameter] = value;
	}
}
