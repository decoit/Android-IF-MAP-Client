/* 
 * Process.java
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

package de.esukom.decoit.android.ifmapclient.device.application;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Entity of a running process
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

@JsonPropertyOrder({ "pid", "name", "uid", "mem"})
public class Process {

	private int pid;
	private String name;
	private int uid;
	private String mem;

	public Process(int pid, String name, int uid, String mem) {
		this.pid = pid;
		this.name = name;
		this.uid = uid;
		this.mem = mem;
	}

	public int getPid() {
		return pid;
	}

	public String getName() {
		return name;
	}

	public int getUid() {
		return uid;
	}

	public String getMem() {
		return mem;
	}
}
