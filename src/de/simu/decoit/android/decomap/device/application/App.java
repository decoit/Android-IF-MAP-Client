/* 
 * App.java
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

package de.simu.decoit.android.decomap.device.application;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Entity for an installed Application
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

@JsonPropertyOrder({ "name", "label", "version", "running", "installTime", "updateTime", "permissions"})
public class App {

	private String name;
	private String label;
	private String version;
	private boolean running;
	private long installTime;
	private long updateTime;
	private String[] permissions;

	public App(String name, String label, String version, boolean running, long installTime, long updateTime, String[] permissions) {
		this.name = name;
		this.label = label;
		this.version = version;
		this.running = running;
		this.installTime = installTime;
		this.updateTime = updateTime;
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public String getVersion() {
		return version;
	}

	public boolean isRunning() {
		return running;
	}

	public long getInstallTime() {
		return installTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public String[] getPermissions() {
		return permissions;
	}
}
