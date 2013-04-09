/* 
 * LogMessageColumns.java        0.1.6 07/02/12
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

package de.esukom.decoit.android.ifmapclient.database.dao;

/**
 * interface for log-message-database-table
 * 
 * @version 0.1.4.2
 * @author Dennis Dunekacke, Decoit GmbH
 */
public interface LogMessageColums {
	String ID = "_id";
	String TIMESTAMP = "timestamp";
	String MESSAGETYPE = "msgtype";
	String MESSAGECONTENT = "msgcontent";
	String TARGET = "target";
	String STATUS = "status";
}
