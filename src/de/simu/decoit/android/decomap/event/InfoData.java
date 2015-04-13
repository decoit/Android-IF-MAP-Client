/* 
 * InfoData.java
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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * data for InfoEvents
 * 
 * @author Markus Schölzel, Decoit GmbH
 */

@JsonPropertyOrder({ "mac", "imei", "imsi", "kernel", "firmware", "root", "selinux", "baseband",
		"build", "brand", "manufacturer", "cellnumber" })
public
class InfoData extends EventData {
	private String mac;
	private String imei;
	private String imsi;
	private String kernel;
	private String firmware;
	private boolean root;
	private String selinux;
	private String baseband;
	private String build;
	private String brand;
	private String manufacturer;
	private String cellnumber;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getKernel() {
		return kernel;
	}

	public void setKernel(String kernel) {
		this.kernel = kernel;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public String getSelinux() {
		return selinux;
	}

	public void setSelinux(String selinux) {
		this.selinux = selinux;
	}

	public String getBaseband() {
		return baseband;
	}

	public void setBaseband(String baseband) {
		this.baseband = baseband;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getCellnumber() {
		return cellnumber;
	}

	public void setCellnumber(String cellnumber) {
		this.cellnumber = cellnumber;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
}
