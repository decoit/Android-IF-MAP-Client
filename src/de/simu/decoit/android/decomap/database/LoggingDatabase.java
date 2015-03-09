/* 
 * LoggingDatabase.java        0.1.6 07/02/12
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
package de.simu.decoit.android.decomap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import de.simu.decoit.android.decomap.database.dao.LogMessageTbl;
import de.simu.decoit.android.decomap.logging.LogMessage;
import de.simu.decoit.android.decomap.util.Toolbox;

/**
 * class for initializing a sql-connection with the log message database and
 * performing different operations on it
 * 
 * @version 0.1.6
 * @author  Dennis Dunekacke, Decoit GmbH 
 * @author  Marcel Jahnke, DECOIT GmbH
 */
public class LoggingDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "log.db";
	private static final int DATABASE_VERSION = 12;

	/**
	 * constructor
	 * 
	 * @param context
	 *            application Context
	 */
	public LoggingDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * executed when no database exists
	 * 
	 * @param SQLLiteDatase
	 *            database-connection to perform delete on
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LogMessageTbl.SQL_CREATE);
	}

	/**
	 * executed when new database version is available...will be executed if
	 * DATABASE_VERSION is increased
	 * 
	 * drop all tables in database and recreate the database
	 * 
	 * @param SQLLiteDatase
	 *            database-connection to perform delete on
	 * @param oldVersion
	 *            old version number of database
	 * @param newVersion
	 *            new version number of database
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(LogMessageTbl.SQL_DROP);
		onCreate(db);
	}

	/**
	 * delete log message from database determined by passed in in
	 * 
	 * @param SQLLiteDatase
	 *            database-connection to perform delete on
	 * @param id
	 *            index of table-row to delete
	 */
	public void deleteMessageAtId(SQLiteDatabase db, String id) {
		final SQLiteStatement stmtDeleteLogMessage = db.compileStatement(LogMessageTbl.STMT_LOGMESSAGE_DELETE_BY_ID);
		db.beginTransaction();

		try {
			stmtDeleteLogMessage.bindString(1, id);
			stmtDeleteLogMessage.execute();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Toolbox.logTxt(this.getClass().getName(), "Error while deleting log message from db: " + e);
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * insert new log message into database
	 * 
	 * @param db
	 *            database-connection to perform insert on
	 * @param msg
	 *            LogMessage message to insert
	 */
	public void insertMessage(SQLiteDatabase db, LogMessage msg) {
		final SQLiteStatement stmtInsertLogMessage = db.compileStatement(LogMessageTbl.STMT_LOGMESSAGE_INSERT);
		db.beginTransaction();
		try {	   
			stmtInsertLogMessage.bindString(1, msg.getTimestamp());
			stmtInsertLogMessage.bindString(2, msg.getMsgType());
			stmtInsertLogMessage.bindString(3, msg.getMsg());
			// target, status
			stmtInsertLogMessage.bindString(4, msg.getTarget());
			stmtInsertLogMessage.bindString(5, msg.getStatus());
			stmtInsertLogMessage.executeInsert();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Toolbox.logTxt(this.getClass().getName(), "Error while inserting log message into db: " + e);
		} finally {
			db.endTransaction();
			db.close();
		}
	}

	/**
	 * delete all log messages from database
	 * 
	 * @param db
	 *            database-connection to perform delete on
	 */
	public void deleteAllMassages(SQLiteDatabase db) {
		final SQLiteStatement stmtDeleteAllLogMessages = db.compileStatement(LogMessageTbl.STMT_LOGMESSAGE_DELETE);
		db.beginTransaction();
		try {
			stmtDeleteAllLogMessages.execute();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Toolbox.logTxt(this.getClass().getName(), "Error while deleting log messages from db: " + e);
		} finally {
			db.endTransaction();
		}
	}
}
