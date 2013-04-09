/* 
 * UnbinderClass.java       0.1.6 07/02/12
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
package de.esukom.decoit.android.ifmapclient.services.binder;

import de.esukom.decoit.android.ifmapclient.util.Toolbox;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ServiceConnection;

/**
 * Class for detaching connection and time counting services
 * 
 * @author Dennis Dunekacke, DECOIT GmbH
 * @version 0.1.6
 */
public class UnbinderClass {

  /**
   * detach local service
   * 
   * @param applicationContext  current application context
   * @param serviceConnection  local service to be unbound
   * @param myProgressDialog    progress dialog for passed in service that should be dismissed
   * @param mIsBound            flag indicating if passed in service is currently bound
   */
    public static boolean doUnbindConnectionService(Context applicationContext, ServiceConnection serviceConnection,
            ProgressDialog myProgressDialog, boolean mIsBound) {
        if (mIsBound && serviceConnection != null) {
            applicationContext.getApplicationContext().unbindService(serviceConnection);
            Toolbox.logTxt(UnbinderClass.class.getName(), "Unbounded Service: " + serviceConnection.getClass().getName());

            if (myProgressDialog != null) {
                myProgressDialog.dismiss();
                Toolbox.logTxt(UnbinderClass.class.getName(), "dismissed progress-dialog");
            }
        } else {
            Toolbox.logTxt(UnbinderClass.class.getName(), "not unbounded Service " + serviceConnection.getClass().getName()
                    + " because it is not bound or is null!");
        }
        
        return false;
    }
}
