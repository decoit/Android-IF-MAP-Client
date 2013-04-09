/*
 * ConnectionsObject.java        0.1.6. 12/03/07
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

package de.esukom.decoit.android.ifmapclient.connection;

import de.fhhannover.inform.trust.ifmapj.channel.ARC;
import de.fhhannover.inform.trust.ifmapj.channel.SSRC;
import de.fhhannover.inform.trust.ifmapj.exception.InitializationException;

/**
 * Container-Class for ConnectionObjects from ifmapj
 * 
 * @version 0.1.6
 * @author Marcel Jahnke, DECOIT GmbH
 */
public class ConnectionObjects {

    // channel-connection objects
    private static SSRC sSsrcConnection = null;
    private static ARC sArcConnection = null;

    /**
     * Sets the SSRC object and generates an ARC object If the delivered object
     * is null, this method sets the ARC object to null
     * 
     * @param sSsrcConnection
     *            the mSsrcConnection to set
     */
    public static void setSsrcConnection(SSRC ssrcConnection) {
        ConnectionObjects.sSsrcConnection = ssrcConnection;
        try {
            if (ssrcConnection != null) {
                setArcConnection(sSsrcConnection.getArc());
            } else {
                setArcConnection(null);
            }
        } catch (InitializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the mSsrcConnection
     */
    public static SSRC getSsrcConnection() {
        return sSsrcConnection;
    }

    /**
     * @param sArcConnection
     *            the mArcConnection to set
     */
    private static void setArcConnection(ARC arcConnection) {
        ConnectionObjects.sArcConnection = arcConnection;
    }

    /**
     * @return the mArcConnection
     */
    public static ARC getArcConnection() {
        return sArcConnection;
    }
}