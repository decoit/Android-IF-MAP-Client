/* 
 * LogMessageAdapter.java        0.2 2015-03-08
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
package de.simu.decoit.android.decomap.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import de.simu.decoit.android.decomap.activities.LogActivity;
import de.simu.decoit.android.decomap.activities.R;
import de.simu.decoit.android.decomap.util.Toolbox;

/**
 * Adapter-Class for handling the Log-Message List View
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @version 0.2
 */
public class LogMessageAdapter extends BaseAdapter implements OnClickListener {
	private Context mContext;

	// called when opening/showing LogMessage-Content in a DialogBox
	private LogActivity mActivityCallback;

	// List containing the different Log-Entries
	private List<LogMessage> mListLogMessage;

	/**
	 * constructor
	 * 
	 * @param context
	 *            current context
	 * @param listLogMessage
	 *            List containing Log-Messages
	 */
	public LogMessageAdapter(Context context, List<LogMessage> listLogMessage) {
		this.mContext = context;
		this.mActivityCallback = (LogActivity) context;
		this.mListLogMessage = listLogMessage;
	}

	/**
	 * get size/count of Log-Messages-List
	 * 
	 * @return integer current size of list
	 */
	public int getCount() {
		return mListLogMessage.size();
	}

	/**
	 * get List-Item from passed in position
	 * 
	 * @param position
	 *            position of desired List-Item
	 * 
	 * @return Object List-Item at passed in position
	 */
	public Object getItem(int position) {
		return mListLogMessage.get(position);
	}

	/**
	 * get the id of List-Item at passed in position
	 * 
	 * @param position
	 *            position of List-Item to get ID from
	 * 
	 * @return long ID of desired List-Item
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * add new Log-Message-List-Item
	 * 
	 * @param newLogMessage
	 *            Log-Message to be added to List
	 */
	public void addItem(LogMessage newLogMessage) {
		mListLogMessage.add(newLogMessage);
	}

	/**
	 * get View-Element from passed position
	 * 
	 * @param position
	 *            index of View-Element
	 * @param convertView
	 *            view to be converted
	 * @param viewGroup
	 * 
	 * @return View View-Element from passed in position/index
	 */
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		LogMessage entry = mListLogMessage.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.log_message_listrow, null);
		}

		// timestamp
		TextView tvTimestamp = (TextView) convertView.findViewById(R.id.tvTimestamp);
		tvTimestamp.setText(entry.getTimestamp());

		// message-type
		TextView tvMessagetype = (TextView) convertView.findViewById(R.id.tvMessagetype);
		tvMessagetype.setText(entry.getMsgType());

		// target address [ip:port(if existing)]
		TextView tvTarget = (TextView) convertView.findViewById(R.id.tvTarget);
		tvTarget.setText(entry.getTarget());

		// message status (success/failure/etc.)
		TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
		tvStatus.setText(entry.getStatus());

		// --- DETAILS BUTTON ---
		Button btnDetails = (Button) convertView.findViewById(R.id.details_button);
		btnDetails.setTag(entry);
		btnDetails.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LogMessage entry = (LogMessage) v.getTag();
				mActivityCallback.showLogMessage(entry.getMsg());
			}
		});

		// --- REMOVE BUTTON ---
		Button btnRemove = (Button) convertView.findViewById(R.id.remove_button);
		btnRemove.setTag(entry);
		btnRemove.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LogMessage entry = (LogMessage) v.getTag();

				// delete message from database
				mActivityCallback.deleteEntry(entry.getId());

				// delete message from list
				mListLogMessage.remove(entry);
				notifyDataSetChanged();
			}
		});

		// --- REMOVE ALL BUTTON ---
		Button btnRemoveall = (Button) convertView.findViewById(R.id.removeall_button);
		btnRemoveall.setTag(entry);
		btnRemoveall.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Collection<LogMessage> log = mListLogMessage;

				// delete all messages from the database
				mActivityCallback.deleteLog();

				// delete all messages from the list
				mListLogMessage.removeAll(log);
				notifyDataSetChanged();
			}
		});

		// --- EXPORT BUTTON ---
		Button btnExport = (Button) convertView.findViewById(R.id.export_button);
		btnExport.setTag(entry);
		btnExport.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// name for export file
				String exportFileName = "message_export_" + Toolbox.now(Toolbox.DATE_FORMAT_NOW_EXPORT) + ".txt";

				// prepare export
				BufferedWriter bw = null;
				ArrayList<LogMessage> exportList = new ArrayList<LogMessage>();
				
				try {
				    if (Toolbox.sLogFolderExists) {
			            try {
			                // write log-message to file
			                bw = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + Toolbox.sLogPath + exportFileName, true));
			                exportList = mActivityCallback.getAllEntrys();
			                // write entries to file
		                    for (int i = 0; i < exportList.size(); i++){
		                        bw.newLine();
		                        bw.write("----------------------------------------------");
		                        bw.newLine();
		                        bw.write("Type:   " + exportList.get(i).getMsgType());
		                        bw.newLine();
		                        bw.write("Target: " + exportList.get(i).getTarget());
		                        bw.newLine();
		                        bw.write("Time:   " + exportList.get(i).getTimestamp());
		                        bw.newLine();
		                        bw.write("----------------------------------------------");
		                        bw.newLine();
		                        bw.write(exportList.get(i).getMsg());
		                        bw.newLine();
		                    }
		                    bw.close();
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
			        } else {
			            Toolbox.sLogFolderExists = Toolbox.createDirIfNotExists(Toolbox.sLogPath);
			        }

					// show quick success-message
					LogMessageDialog dialog = new LogMessageDialog(mContext,
							"messages have been exported");
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	/**
	 * on-click handler
	 * 
	 * @param view
	 *            from where the event has been originated
	 */
	@Override
	public void onClick(View view) {
		// empty for now
	}
}
