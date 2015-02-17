/* 
 * EventBuilder.java
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

package de.esukom.decoit.android.ifmapclient.event;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generate event messages for iMonitor
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

public class EventBuilder {

	private final Event mEvent = new Event();
	private final String sEventType = "Android";

	private String mEventIP;

	/**
     * Return the built event
     * 
     * @return the built event
     */
    public Event create() {
        return mEvent;
    }

	/**
     * Use the correct header
     * 
     * @return the event
     */
    public EventBuilder withHeader(String ip) {
    	// format: 2014-06-17 11:56:05
		SimpleDateFormat sTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mEvent.setTimestamp(sTimeformat.format(new Date()));

		// set event type
		mEvent.setType(sEventType);

		// set event ip
		mEventIP = ip;
		mEvent.setIpsrc(mEventIP);

        return this;
    }

    /**
     * Set event class (info/monitor/apps)
     * 
     * @return the event
     */
    public EventBuilder Class(String eventClass) {
		// set info/monitor/apps
		mEvent.setEventClass(eventClass);

		// set message based on class
		switch (eventClass) {
		case "info":
			mEvent.setMessage("Android device information for " +  mEventIP);
			break;
		case "monitor":
			mEvent.setMessage("Android monitoring information for " + mEventIP);
			break;
		case "apps":
			mEvent.setMessage("Android application information for " + mEventIP);
			break;
		}

        return this;
    }

    /**
     * Set event data
     * 
     * @return the event
     */
    public EventBuilder data(EventData data) {
		mEvent.setData(data);

	return this;
    }
}
