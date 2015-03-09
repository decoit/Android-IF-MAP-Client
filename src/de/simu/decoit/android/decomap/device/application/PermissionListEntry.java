/* 
 * PermissionListEntry.java        0.1.6 07/02/12
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
package de.simu.decoit.android.decomap.device.application;

import java.util.ArrayList;

/**
 * class representing a permission-list-entry
 * 
 * @version 0.1.6
 * @author Dennis Dunekacke, Decoit GmbH
 */
public class PermissionListEntry {
	
    private String permissionName = null;

    /* applications that are using this permission */
    private ArrayList<String> permissionApplications = null;

    private volatile int hashCode = 0;

    /**
     * constructor
     * 
     * @param name
     *            permission-name
     * @param permissionType
     * 			  type of permission
     */
    public PermissionListEntry(String name) {
        permissionApplications = new ArrayList<String>();
        permissionName = name;
    }

    /**
     * @return the permissionName
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * @param permissionName
     *            the permissionName to set
     */
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    /**
     * @return the permissionApplications
     */
    public ArrayList<String> getPermissionApplications() {
        return permissionApplications;
    }

    /**
     * @param permissionApplications
     *            the permissionApplications to set
     */
    public void setPermissionApplications(ArrayList<String> permissionApplications) {
        this.permissionApplications = permissionApplications;
    }

    public void addApplication(String name) {
        this.permissionApplications.add(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PermissionListEntry)) {
            return false;
        }

        PermissionListEntry entry = (PermissionListEntry) obj;
        if (permissionName.equals(entry.getPermissionName())) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        final int multiplier = 23;
        if (hashCode == 0) {
            int code = 133;
            code = multiplier * code + permissionName.hashCode();
            hashCode = code;
        }
        return hashCode;
    }
}
