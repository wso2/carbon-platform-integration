/*
 * Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.automation.test.utils.governance;

import org.testng.Assert;
import org.wso2.carbon.automation.test.api.clients.governance.LifeCycleManagementClient;
import org.wso2.carbon.automation.test.utils.common.FileManager;
import org.wso2.carbon.governance.custom.lifecycles.checklist.stub.beans.xsd.LifecycleBean;
import org.wso2.carbon.governance.custom.lifecycles.checklist.stub.util.xsd.Property;
import org.wso2.carbon.governance.lcm.stub.LifeCycleManagementServiceExceptionException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LifeCycleUtils {

     public static String[] getLifeCycleProperty(Property[] properties, String key) {
         Assert.assertTrue((properties.length > 0), "LifeCycle properties missing some properties");
         String[] values = null;
         boolean stateFound = false;
         for (Property prop : properties) {
             if (key.equalsIgnoreCase(prop.getKey())) {
                 stateFound = true;
                 Assert.assertNotNull(prop.getValues(), "State Value Not Found");
                 values = prop.getValues();
    
             }
         }
         Assert.assertTrue(stateFound, key + " property not found");
         return values;
     }
    
     public static String getLifeCycleState(LifecycleBean lifeCycle) {
         Assert.assertTrue((lifeCycle.getLifecycleProperties().length > 0), "LifeCycle properties missing some properties");
         String state = null;
         boolean stateFound = false;
         for (Property prop : lifeCycle.getLifecycleProperties()) {
             if ("registry.lifecycle.ServiceLifeCycle.state".equalsIgnoreCase(prop.getKey())) {
                 stateFound = true;
                 Assert.assertNotNull(prop.getValues(), "State Value Not Found");
                 state = prop.getValues()[0];
    
             }
         }
         Assert.assertTrue(stateFound, "LifeCycle State property not found");
         return state;
     }
    
     public static void createNewLifeCycle(String lifeCycleName
             , LifeCycleManagementClient lifeCycleManagerAdminService, String filePath)
             throws IOException, LifeCycleManagementServiceExceptionException, InterruptedException {
         String lifeCycleConfiguration = FileManager.readFile(filePath).replace("IntergalacticServiceLC", lifeCycleName);
         Assert.assertTrue(lifeCycleManagerAdminService.addLifeCycle(lifeCycleConfiguration)
                 , "Adding New LifeCycle Failed");
         Thread.sleep(2000);
         lifeCycleConfiguration = lifeCycleManagerAdminService.getLifecycleConfiguration(lifeCycleName);
         Assert.assertTrue(lifeCycleConfiguration.contains("aspect name=\"" + lifeCycleName + "\""),
                           "LifeCycleName Not Found in lifecycle configuration");
    
         String[] lifeCycleList = lifeCycleManagerAdminService.getLifecycleList();
         Assert.assertNotNull(lifeCycleList);
         Assert.assertTrue(lifeCycleList.length > 0, "Life Cycle List length zero");
         boolean found = false;
         for (String lc : lifeCycleList) {
             if (lifeCycleName.equalsIgnoreCase(lc)) {
                 found = true;
             }
         }
         Assert.assertTrue(found, "Life Cycle list not contain newly added life cycle");
    
     }
    
     public static void deleteLifeCycleIfExist(String lifeCycleName
             , LifeCycleManagementClient lifeCycleManagerAdminService)
             throws LifeCycleManagementServiceExceptionException, RemoteException {
         String[] lifeCycleList = lifeCycleManagerAdminService.getLifecycleList();
         if (lifeCycleList != null && lifeCycleList.length > 0) {
             for (String lc : lifeCycleList) {
                 if (lifeCycleName.equalsIgnoreCase(lc)) {
                     lifeCycleManagerAdminService.deleteLifeCycle(lifeCycleName);
                 }
             }
         }
     }
    
     public static String formatDate(Date date) {
         Format formatter = new SimpleDateFormat("MM/dd/yyyy");
         return formatter.format(date);
     }
}
