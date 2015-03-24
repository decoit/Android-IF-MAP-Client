/* 
 * ReadOutMessages.java        0.2 2015-03-08
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.util.Log;
import de.hshannover.f4.trust.ifmapj.messages.PublishElement;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.PublishUpdate;

/**
 * This Class read out requests and response messages.
 * 
 * @author  Marcel Jahnke, DECOIT GmbH
 * @version 0.2
 */
public class ReadOutMessages extends ArrayList<HashMap<String, String>> {

    private static final long serialVersionUID = -2402232115753747461L;

    /**
     * constructor
     */
    public ReadOutMessages() {
        super();
    }

    /**
     * This method reads out the parameters of PublishRequest message
     * 
     * @param request
     *            The PubblishReqest message
     * @return ArrayList that contains the paramteres
     */
    public static ArrayList<HashMap<String, String>> readOutRequest(PublishRequest request) {
        ReadOutMessages requestList = null;
        HashMap<String, String> tempEventData = new HashMap<String, String>();

        if (request != null) {
            requestList = new ReadOutMessages();
            Collection<PublishElement> pECol = request.getPublishElements();
            Iterator<PublishElement> pEColIt = pECol.iterator();

            while (pEColIt.hasNext()) {
                PublishElement pE = pEColIt.next();
                PublishUpdate pU = null;

                if (pE.toString().toLowerCase(Locale.ENGLISH).contains("update")) {
                    pU = (PublishUpdate) pE;

                    Collection<Document> metaData = pU.getMetadata();

                    for (Document currentMetaDoc : metaData) {
                        NodeList list = currentMetaDoc.getChildNodes();
                        for (int j = 0; j < list.getLength(); j++) {
                            // child nodes
                            NodeList child = list.item(j).getChildNodes();
                            for (int k = 0; k < child.getLength(); k++) {
                                if (child.item(k).getNodeName() != null && child.item(k).getTextContent() != null) {
                                    // get child name and data and put it in map
                                    tempEventData.put(child.item(k).getNodeName(), child.item(k).getTextContent());
                                }
                                for (int l = 0; l < child.item(k).getAttributes().getLength(); l++) {
                                    NamedNodeMap attributes = child.item(k).getAttributes();
                                    tempEventData.put(attributes.item(l).getNodeName(), attributes.item(l).getNodeValue());
                                }
                            }
                        }
                    }
                }
            }
            if (tempEventData.size() > 0) {
                requestList.add(tempEventData);
            }
        } else {
            Log.d("PublishRequest", "request is null!");
        }
        return requestList;
    }
}
