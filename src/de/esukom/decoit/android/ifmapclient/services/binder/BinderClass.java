/* 
 * BinderClass.java       0.1.6 07/02/12
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

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import de.esukom.decoit.android.ifmapclient.services.NscaService;
import de.esukom.decoit.android.ifmapclient.services.PermanentConnectionService;
import de.esukom.decoit.android.ifmapclient.services.RenewConnectionService;
import de.esukom.decoit.android.ifmapclient.util.Toolbox;


public class BinderClass {

    /**
     * Establish a connection with the synchronous service. We use an explicit
     * class name because we want a specific service implementation that we know
     * will be running in our own process (and thus won't be supporting
     * component replacement by other applications).
     */
    public static boolean doBindRenewConnectionService(Context applicationContext, ServiceConnection connectionService) {
        Toolbox.logTxt(UnbinderClass.class.getName(), "Binding Service: RenewConnectionService");
        applicationContext.bindService(
                new Intent(applicationContext, RenewConnectionService.class),
                connectionService, Context.BIND_AUTO_CREATE);
        return true;
    }
    
    public static boolean doBindPermConnectionService(Context applicationContext, ServiceConnection connectionService) {
        Toolbox.logTxt(UnbinderClass.class.getName(), "Binding Service: PermanentConnectionService");
        applicationContext.bindService(new Intent(applicationContext, PermanentConnectionService.class), connectionService,
                Context.BIND_AUTO_CREATE);
        return true;
    }

    public static boolean doBindNscaService(Context applicationContext, ServiceConnection connectionService) {
        Toolbox.logTxt(UnbinderClass.class.getName(), "Binding Service: NscaService");
        applicationContext.bindService(new Intent(applicationContext, NscaService.class), connectionService,
                Context.BIND_AUTO_CREATE);
        return true;
    }
}