/*
 * StatusMessageAdapter.java         0.1.6 07/02/12
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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.simu.decoit.android.decomap.activities.R;

/**
 * Adapter-Class for handling the Log-Message List View
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @author Leonid Schwenke, DECOIT GmbH
 * @author Markus Schölzel, DECOIT GmbH
 * @version 0.1.6
 */
public class StatusMessageAdapter extends BaseAdapter {
	public Activity context;
	public LayoutInflater inflater;
	ArrayList<ListEntry> listStatusMessage;

	/**
	 * 
	 * constructor
	 * 
	 * @param context
	 *            current context
	 * @param listStatusMessage
	 *            List containing Log-Messages
	 */
	public StatusMessageAdapter(Activity context, ArrayList<ListEntry> listStatusMessage) {
		super();
		this.context = context;
		this.listStatusMessage = listStatusMessage;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * get size/count of Item-List
	 * 
	 * @return integer current size of list
	 */
	public int getCount() {
		return listStatusMessage.size();
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
		return listStatusMessage.get(position);
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
	 * add new Item to List
	 * 
	 * @param newLogMessage
	 *            Log-Message to be added to List
	 */
	public void addItem(ListEntry entry) {
		listStatusMessage.add(entry);
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
		ListEntry entry = listStatusMessage.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.status_listview, null);
		}

		TextView title = (TextView) convertView
				.findViewById(R.id.status_listview_title);
		title.setText(entry.getTitle());

		TextView value = (TextView) convertView
		.findViewById(R.id.status_listview_value);
		value.setText(entry.getValue());

		return convertView;
	}
}
