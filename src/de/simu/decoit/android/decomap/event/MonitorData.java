/* 
 * MonitorData.java
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

package de.simu.decoit.android.decomap.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.simu.decoit.android.decomap.device.application.Process;

/**
 * data for MonitorEvents
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

@JsonPropertyOrder({ "trafficin", "trafficout", "cpuload", "mem", "processcount"})
public class MonitorData extends EventData {
	private long trafficin;
	private long trafficout;
	private int cpuload;
	private int mem;
	private int processcount;
	private List<Process> processdetail;

	public long getTrafficin() {
		return trafficin;
	}
	public void setTrafficin(long trafficin) {
		this.trafficin = trafficin;
	}
	public long getTrafficout() {
		return trafficout;
	}
	public void setTrafficout(long trafficout) {
		this.trafficout = trafficout;
	}
	public int getCpuload() {
		return cpuload;
	}
	public void setCpuload(int cpuload) {
		this.cpuload = cpuload;
	}
	public int getMem() {
		return mem;
	}
	public void setMem(int i) {
		this.mem = i;
	}
	public int getProcesscount() {
		return processcount;
	}
	public void setProcesscount(int processcount) {
		this.processcount = processcount;
	}
	public List<Process> getProcessdetail() {
		return processdetail;
	}
	public void setProcessdetail(List<Process> processdetail) {
		this.processdetail = processdetail;
	}
}
