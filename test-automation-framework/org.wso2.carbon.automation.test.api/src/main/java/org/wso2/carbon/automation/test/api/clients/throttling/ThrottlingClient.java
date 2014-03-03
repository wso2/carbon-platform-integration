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

package org.wso2.carbon.automation.test.api.clients.throttling;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.throttle.stub.ThrottleAdminServiceStub;
import org.wso2.carbon.throttle.stub.types.ThrottlePolicy;

/**
 * Client class for throttling operations
 */
public class ThrottlingClient {
    private static final Log log = LogFactory.getLog(ThrottlingClient.class);
    private ThrottleAdminServiceStub throttleAdminServiceStub;

    public ThrottlingClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String serviceName = "ThrottleAdminService";
        String endPoint = backEndUrl + serviceName;
        try {
            throttleAdminServiceStub = new ThrottleAdminServiceStub(endPoint);
            AuthenticateStub.authenticateStub(sessionCookie, throttleAdminServiceStub);
        } catch (AxisFault axisFault) {
            log.error("ThrottleAdminServiceStub Initialization fail " + axisFault.getMessage());
            throw new AxisFault("throttleAdminServiceStub Initialization fail " + axisFault.getMessage());
        }
    }

    public void enableThrottling(String serviceName, ThrottlePolicy policy) throws Exception {
        throttleAdminServiceStub.enableThrottling(serviceName, policy);
    }
}
