/* 
 * SyncConnectionThread.java        0.2 2015-03-08
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

package de.simu.decoit.android.decomap.connection;

import java.util.ArrayList;
import java.util.HashMap;

import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.simu.decoit.android.decomap.messaging.MessageHandler;
import de.simu.decoit.android.decomap.messaging.ReadOutMessages;
import de.simu.decoit.android.decomap.services.PermanentConnectionService;
import de.simu.decoit.android.decomap.services.RenewConnectionService;

/**
 * Thread for Connecting to a map-server (by using the IfmapJ-lib)
 * 
 * @version 0.1.4.2
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Marcel Jahnke, DECOIT GmbH
 */
public class SyncConnectionThread implements Runnable {

    // connection object
    private static SSRC sSsrcConnection;

    // request-parameters
    private static String sSessionId;
    private static String sPublisherId;

    // service that started the Connection-Thread,
    // used for Callback when Thread finishes
    private RenewConnectionService mCallback;
    private PermanentConnectionService mPermCallback = null;

    // server-connection details
    private byte mMessageType;
    private PublishRequest mMessagePublish;

    // flag for server-connection type
    private boolean mIsPermConection;

    /**
     * constructor for the permanently connection with ifmapj
     * 
     * @param callback
     *            service (or activity) to handle server response
     * @param msgType
     *            the message type
     * @param msg
     *            the publish message
     */
    public SyncConnectionThread(PermanentConnectionService callback, byte msgType, PublishRequest msg) {
        // initialize members fields
        mPermCallback = callback;
        mMessageType = msgType;
        mIsPermConection = true;
        PermanentlyConnectionManager.getInstance().push(this);
        sSsrcConnection = ConnectionObjects.getSsrcConnection();
        mMessagePublish = msg;
    }

    /**
     * constructor for the renew-session connection with ifmapj
     * 
     * @param callback
     *            service (or activity) to handle server response
     * @param msgType
     *            the message type
     * @param msg
     *            the publish message
     */
    public SyncConnectionThread(RenewConnectionService callback, byte msgType, PublishRequest msg) {
        mIsPermConection = false;
        mCallback = callback;
        mMessageType = msgType;
        sSsrcConnection = ConnectionObjects.getSsrcConnection();
        mMessagePublish = msg;
    }

    /**
     * thread run-method
     */
    public void run() {
        ArrayList<HashMap<String, String>> responseMsg = null;
        try {
            switch (mMessageType) {
            case MessageHandler.MSG_TYPE_REQUEST_NEWSESSION:
                sSsrcConnection.newSession();
                sSessionId = sSsrcConnection.getSessionId();
                sSsrcConnection.purgePublisher();
                sPublisherId = sSsrcConnection.getPublisherId();
                break;
            case MessageHandler.MSG_TYPE_REQUEST_ENDSESSION:
                sSsrcConnection.endSession();
                ConnectionObjects.setSsrcConnection(null);
                break;
            case MessageHandler.MSG_TYPE_REQUEST_RENEWSESSION:
                sSsrcConnection.renewSession();
                break;
            case MessageHandler.MSG_TYPE_PUBLISH_CHARACTERISTICS:
                responseMsg = ReadOutMessages.readOutRequest(mMessagePublish);
                sSsrcConnection.publish(mMessagePublish);
                break;
            case MessageHandler.MSG_TYPE_METADATA_UPDATE:
                responseMsg = ReadOutMessages.readOutRequest(mMessagePublish);
                sSsrcConnection.publish(mMessagePublish);
                break;
            }

            // build response
            StringBuffer sb = new StringBuffer();
            if (responseMsg != null) {
                for (int i = 0; i < responseMsg.size(); i++) {
                    sb.append(responseMsg.get(i).toString().trim());
                }
            }

            if (mIsPermConection) {
                mPermCallback.handleServerResponse(mMessageType, "session-id=" + sSessionId + ",publisher-id=" + sPublisherId + "," + sb);
            } else {
                mCallback.handleServerResponse(mMessageType, "session-id=" + sSsrcConnection.getSessionId() + ",publisher-id="
                        + sSsrcConnection.getPublisherId() + "," + sb);
                sSessionId = sSsrcConnection.getSessionId();
                sPublisherId = sSsrcConnection.getPublisherId();
            }

        } catch (IfmapErrorResult e) {
            mMessageType = MessageHandler.MSG_TYPE_ERRORMSG;
            if (mIsPermConection) {
                mPermCallback.handleServerResponse(mMessageType, e.getErrorString());
            } else {
                mCallback.handleServerResponse(mMessageType, e.getErrorString());
            }
        } catch (IfmapException e) {
            mMessageType = MessageHandler.MSG_TYPE_ERRORMSG;
            if (mIsPermConection) {
                mPermCallback.handleServerResponse(mMessageType, e.getMessage());
            } else {
                mCallback.handleServerResponse(mMessageType, e.getMessage());
            }
        } catch (NullPointerException e) {
            mMessageType = MessageHandler.MSG_TYPE_ERRORMSG;
            if (mIsPermConection) {
                mPermCallback.handleServerResponse(mMessageType, e.getMessage());
            } else {
                mCallback.handleServerResponse(mMessageType, e.getMessage());
            }
        }

        if (mIsPermConection) {
            PermanentlyConnectionManager.getInstance().didComplete(this);
        }
    }
}
