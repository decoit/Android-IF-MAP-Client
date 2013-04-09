/* 
 * MessageHandler.java        0.1.6 07/02/12
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

import android.content.Context;
import de.esukom.decoit.android.ifmapclient.activities.R;

/**
 * singleton for handling server-response-messages
 * 
 * @author Dennis Dunekacke, DECOIT GmbH,
 * @author Marcel Jahnke, DECOIT GmbH
 * @version 0.1.6
 */
public class MessageHandler {

    // message types
    public static final byte MSG_TYPE_REQUEST_NEWSESSION = 1;
    public static final byte MSG_TYPE_REQUEST_ENDSESSION = 2;
    public static final byte MSG_TYPE_SUBSCRIPE_REQUEST_TESTCASE1 = 3;
    public static final byte MSG_TYPE_POLL_REQUEST = 4;
    public static final byte MSG_TYPE_REQUEST_RENEWSESSION = 5;
    public static final byte MSG_TYPE_METADATA_UPDATE = 6;
    public static final byte MSG_TYPE_DELETE_LOCATION = 7;
    public static final byte MSG_TYPE_PUBLISH_CHARACTERISTICS = 8;
    public static final byte MSG_TYPE_SEARCH = 9;
    public static final byte MSG_TYPE_ERRORMSG = 99;
    public static final byte MSG_TYPE_INVALID_RESPONSE = 100;

    // singleton
    static private MessageHandler _instance;
    private MessageHandler() {
    }

    /**
     * get an instance of the message handler
     * 
     * @return MessageHandler
     */
    static public MessageHandler getInstance() {
        if (_instance == null) {
            _instance = new MessageHandler();
        }
        return _instance;
    }

    /**
     * create response object from passed in byte array and serialize it as string
     * 
     * @param msgType
     *            identifier for desired message type
     * @param response
     *            array containing the response as byte stream
     * @param stripLineBreaks
     *            strip all linebreaks in string before returning,
     * @param serviceContext
     *            application context from service witch is calling this method. This is necessary to get access to the String-Resources
     * 
     * @return String
     */
    public ResponseParameters getResponseParameters(byte msgType, String response, boolean stripLineBreaks, Context serviceContext) {
        ResponseParameters responseParams = new ResponseParameters();
        SerializedResponse serResponse = new SerializedResponse();
        serResponse.setSerializedMsg(response, msgType, serviceContext);

        // get content of (soap)-message depending on passed in message type
        switch (msgType) {
        case MSG_TYPE_REQUEST_NEWSESSION:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_newsession));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_SESSIONID, serResponse.getSessionID());
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_PUBLISHERID, serResponse.getPublisherId().replace("=", ""));
            break;
        case MSG_TYPE_REQUEST_ENDSESSION:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_endsession));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            break;
        case MSG_TYPE_REQUEST_RENEWSESSION:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_renewsession));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            break;
        case MSG_TYPE_PUBLISH_CHARACTERISTICS:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_devicecharacteristics));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            break;
        case MSG_TYPE_ERRORMSG:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG, serResponse.getSerializedMsg());
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            break;
        case MSG_TYPE_METADATA_UPDATE:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_location));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, serResponse.getSerializedMsg());
            break;
        case MSG_TYPE_INVALID_RESPONSE:
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_STATUSMSG,
                    serviceContext.getResources().getString(R.string.messagehandler_mainstatus_message_invalidresponse));
            responseParams.setParamter(ResponseParameters.RESPONSE_PARAMS_MSGCONTENT, "no message to show");
            break;
        }
        return responseParams;
    }
}