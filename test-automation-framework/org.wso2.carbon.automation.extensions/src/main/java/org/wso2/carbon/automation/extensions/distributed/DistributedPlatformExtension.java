/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.extensions.distributed;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.engine.extensions.ExecutionListenerExtension;

import java.io.IOException;

import static org.apache.commons.exec.util.DebugUtils.handleException;

/**
 * This extension class serves as container management extension
 */
public class DistributedPlatformExtension extends ExecutionListenerExtension {

    private static final Log log = LogFactory.getLog(DistributedPlatformExtension.class);
    private BaseManager baseManager;

    @Override
    public void initiate() throws AutomationFrameworkException {

        log.info("Executing DistributedPlatformExtension pluggable module");

        try {
            baseManager = new BaseManager();
        } catch (IOException e) {
            handleException("Error while initiating test environment", e);
        }

    }

    @Override
    public void onExecutionStart() throws AutomationFrameworkException {


        // build docker containers

        try {
            baseManager.dockerContainerRunner();
        } catch (IOException | JSONException | ParseException e) {
            handleException("Error while initiating test environment", e);
        }
    }

    @Override
    public void onExecutionFinish() throws AutomationFrameworkException {

        for (int x = 0; x <= baseManager.getDockerContainerList().size(); x++) {
            try {
                baseManager.stopContainer(baseManager.getDockerContainerList().get(x));
            } catch (JSONException e) {
                handleException("Error while initiating test environment", e);
            }
        }
    }
}
