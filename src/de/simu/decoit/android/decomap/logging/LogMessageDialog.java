/* 
 * LogMessageDialog.java        0.1.6 07/02/12
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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.simu.decoit.android.decomap.activities.R;

/**
 * class representing a log-message dialog view-element
 * 
 * @author 	Dennis Dunekacke, DECOIT GmbH
 * @author Markus Schölzel, DECOIT GmbH
 * @version 0.1.6
 */
public class LogMessageDialog extends Dialog {
	private String mMessage;
	
	/**
	 * constructor
	 * 
	 * @param context	current context
	 * @param msg		message to display
	 */
	public LogMessageDialog(Context context, String msg) {
		super(context);
		mMessage = msg;
	}

	/**
	 * Called when the activity is first created
	 * 
	 * @param	savedInstanceState	state-bundle
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_message_dialog);
		setTitle(R.string.log_msgDialogBox_label);
		TextView twMsg = (TextView)findViewById (R.id.msgView);
		
		twMsg.setHorizontallyScrolling(true);
		twMsg.setVerticalScrollBarEnabled(true);
		twMsg.setVerticalFadingEdgeEnabled(true);
		twMsg.setText(mMessage);
		Button buttonOK = (Button) findViewById(R.id.buttonOK);
		buttonOK.setWidth((int)100);
		buttonOK.setOnClickListener(new OKListener());
	}
	
	/**
	 * on-click listener class
	 */
	private class OKListener implements android.view.View.OnClickListener {
		
		@Override
		public void onClick(View v) {
			// close dialog-box
			LogMessageDialog.this.dismiss();
		}
	}
}
