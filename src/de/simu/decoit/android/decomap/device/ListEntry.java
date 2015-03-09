/*
 * ListEntry.java         0.2 2015-03-08
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
package de.simu.decoit.android.decomap.device;

/**
 * Item that will be added to a ListView
 * 
 * @author  Leonid Schwenke, Decoit GmbH
 * @version 0.1
 */
public class ListEntry {
	
	private String mTitle;
	private String mValue;
	
	
	/**
	 * constructor
	 * @param title title/label of list-entry
	 * @param value if list-entry 
	 */
	public ListEntry(String title, String value){
		this.mTitle = title;
		this.mValue = value;
	}
	
	/**
	 * @return the title of the item
	 */
	public String getTitle(){
		return mTitle;
	}
	
	/**
	 * @param title 
	 * 				set title of the item
	 */
	public void setTitle(String title){
		this.mTitle = title;
	}
	
	/**
	 * @return value of Item
	 */
	public String getValue(){
		return mValue;
	}
	
	/**
	 * @param value
	 * 				set value if item
	 */			
	public void setValue(String value){
		this.mValue = value;
	}
	
}
