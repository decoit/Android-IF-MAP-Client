/* 
 * LogMessageTbl.java        0.2 2015-03-08
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
package de.simu.decoit.android.decomap.database.dao;

/**
 * class containing some static predefined values and sql-querys
 * for the log-message table
 * 
 * @author 	Dennis Dunekacke, Decoit GmH
 * @version 0.2
 */
public final class LogMessageTbl implements LogMessageColums {

	public static final String TABLE_NAME = "logmessages";

	public static final String SQL_CREATE = "CREATE TABLE logmessages ("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "timestamp TEXT NOT NULL," + "msgtype TEXT NOT NULL,"
			+ "msgcontent TEXT," + "target TEXT,"
			+ "status TEXT"
			+ ");";

	public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public static final String STMT_LOGMESSAGE_INSERT = "INSERT INTO logmessages "
			+ "(timestamp,msgtype,msgcontent,target,status) " + "VALUES (?,?,?,?,?)";

	public static final String STMT_LOGMESSAGE_DELETE = "DELETE FROM logmessages ";

	public static final String STMT_LOGMESSAGE_DELETE_BY_ID = "DELETE FROM logmessages "
			+ "WHERE _id= ?";

	public static final String[] ALL_COLUMS = new String[] { ID, TIMESTAMP,
			MESSAGETYPE, MESSAGECONTENT, TARGET, STATUS };
}
