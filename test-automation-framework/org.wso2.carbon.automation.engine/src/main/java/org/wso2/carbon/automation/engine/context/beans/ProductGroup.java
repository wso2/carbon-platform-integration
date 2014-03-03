/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.automation.engine.context.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductGroup {

    private boolean isClusterEnabled;
    private String groupName;
    private Map<String, Instance> instanceMap = new HashMap<String, Instance>();
    private Map<String, ArrayList<Instance>> instanceMapByType = new HashMap<String, ArrayList<Instance>>();

    public Map<String, ArrayList<Instance>> getInstanceMapByType() {
        return instanceMapByType;
    }

    public void setInstanceMapByType(Map<String, ArrayList<Instance>> instanceMapByType) {
        this.instanceMapByType = instanceMapByType;
    }

    public boolean isClusterEnabled() {
        return isClusterEnabled;
    }

    public void setClusterEnabled(boolean isClusterEnabled) {
        this.isClusterEnabled = isClusterEnabled;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public Map<String, Instance> getInstanceMap() {
        return instanceMap;
    }

    public void setInstanceMap(Map<String, Instance> instanceMap) {
        this.instanceMap = instanceMap;
    }

    public void addInstance(Instance instance) {

        this.instanceMap.put(instance.getName(), instance);

    }
}
