/* 
 * LogActivity.java        0.1.6. 12/03/07
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

package de.esukom.decoit.android.ifmapclient.activities;

import de.esukom.decoit.android.ifmapclient.database.LoggingDatabase;
import de.esukom.decoit.android.ifmapclient.logging.LogMessage;
import de.esukom.decoit.android.ifmapclient.logging.LogMessageAdapter;
import de.esukom.decoit.android.ifmapclient.logging.LogMessageDialog;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for showing Log-Messages
 * 
 * @author Dennis Dunekacke, Decoit GmbH
 * @version 0.1.6
 */
public class LogActivity extends Activity {

	// database for log messages
	private LoggingDatabase mLogDB = null;

	// -------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE HANDLING
    // -------------------------------------------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Toolbox.logTxt(this.getClass().getName(), "LogActivity.OnCreate(...) called");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab2);

		// create new database connection
		mLogDB = new LoggingDatabase(this);

		// create log message list
		createListAdapter();
	}

	@Override
	public void onResume() {
		Toolbox.logTxt(this.getClass().getName(), "LogActivity.OnResume(...) called");
		super.onStart();

		// re-create list adapter
		createListAdapter();
	}

	// -------------------------------------------------------------------------
    // LOG-ENTRIES-LIST OPERATIONS
    // -------------------------------------------------------------------------

	/**
	 * create new log-message-list-adapter for list-view and fill it with
	 * messages from database
	 */
	private void createListAdapter() {
		// get view for log-msg list
		ListView list = (ListView) findViewById(R.id.logMessages_ListView);

		// get messages from database
		List<LogMessage> listOfLogMessages = getAllEntrys();

		// create and set new adapter
		LogMessageAdapter adapter = new LogMessageAdapter(this, listOfLogMessages);
		list.setAdapter(adapter);
	}

	/**
	 * show message-content-Dialog-Box. Will be called by LogMessageAdapter to
	 * show message-content/details
	 * 
	 * @param msg
	 *            message to show
	 */
	public void showLogMessage(String msg) {
		LogMessageDialog dialog = new LogMessageDialog(this, msg);
		dialog.show();
	}

	/**
	 * get log-messages from database
	 * 
	 * @return log-messages from database
	 */
	public ArrayList<LogMessage> getAllEntrys() {
		// create log message list for list-view
		ArrayList<LogMessage> logMessageList = new ArrayList<LogMessage>();

		// get db content
		Cursor resultCursor = null;
		try {
			resultCursor = mLogDB.getReadableDatabase().query(false, "logmessages",
					new String[] { "_id", "timestamp", "msgtype", "msgcontent", "target", "status" }, null, null, null,
					null, null, null);

			while (resultCursor.moveToNext()) {
				LogMessage lMsg = new LogMessage(resultCursor.getInt(0), // id
						resultCursor.getString(1), // timestamp
						resultCursor.getString(3), // msg-type
						resultCursor.getString(2), // msg-content
						resultCursor.getString(4), // target address
						resultCursor.getString(5)); // msg-status
				logMessageList.add(lMsg);
			}
		} finally {
			resultCursor.close();
		}
		return logMessageList;
	}

	/**
	 * delete log-message from database at passed in index
	 * 
	 * @param id
	 *            index to delete message from database
	 */
	public void deleteEntry(int id) {
		mLogDB.deleteMessageAtId(mLogDB.getWritableDatabase(), new Integer(id).toString());
	}

	/**
	 * delete all log messages from database
	 */
	public void deleteLog() {
		mLogDB.deleteAllMassages(mLogDB.getWritableDatabase());
	}

	// -------------------------------------------------------------------------
    // BUTTON HANDLING
    // -------------------------------------------------------------------------
    
	/**
	 * we override the behavior of the back-button so that the application runs
	 * in the background (instead of destroying it) when pressing back (similar
	 * to the home button)
	 */
	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}
}
