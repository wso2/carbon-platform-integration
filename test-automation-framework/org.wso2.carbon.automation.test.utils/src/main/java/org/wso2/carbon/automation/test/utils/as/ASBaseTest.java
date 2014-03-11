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
package org.wso2.carbon.automation.test.utils.as;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.security.SecurityAdminServiceClient;

public class ASBaseTest {
    private static final Log log = LogFactory.getLog(ASBaseTest.class);
    public SecurityAdminServiceClient securityAdminServiceClient;

   /* public void init() throws Exception {
        int userId = 2;
        userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId);
        asServer = builder.build().getAs();
    }

    public void init(int userId) throws Exception {
        userInfo = UserListCsvReader.getUserInfo(userId);
        EnvironmentBuilder builder = new EnvironmentBuilder().as(userId);
        asServer = builder.build().getAs();
    }

    public void cleanup() {
        userInfo = null;
        asServer = null;
    }

    public String getServiceUrl(String serviceName) {
        return asServer.getServiceUrl() + "/" + serviceName;
    }

    public String getServiceUrlHttps(String serviceName) {
        return asServer.getSecureServiceUrl() + "/" + serviceName;
    }

    public void deployAarService(String serviceName, String fileNameWithExtension,
                                    String filePath, String serviceHierarchy)
            throws Exception {
        AARServiceUploaderClient aarServiceUploaderClient =
                new AARServiceUploaderClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        aarServiceUploaderClient.uploadAARFile(fileNameWithExtension, filePath, serviceHierarchy);

        ServiceDeploymentUtil.isServiceDeployed(asServer.getBackEndUrl(), asServer.getSessionCookie(), serviceName);
        Assert.assertTrue(ServiceDeploymentUtil.
                isServiceDeployed(asServer.getBackEndUrl(), asServer.getSessionCookie(), serviceName),
                          "Service file uploading failed withing given deployment time");
    }

    public void deleteService(String serviceName) throws RemoteException {
        ServiceAdminClient adminServiceService =
                new ServiceAdminClient(asServer.getBackEndUrl(), asServer.getSessionCookie());
        if (ServiceDeploymentUtil.isFaultyService(asServer.getBackEndUrl(),
                                                  asServer.getSessionCookie(), serviceName)) {
            adminServiceService.deleteFaultyServiceByServiceName(serviceName);

        } else if (ServiceDeploymentUtil.isServiceExist(asServer.getBackEndUrl(),
                                                        asServer.getSessionCookie(), serviceName)) {
            adminServiceService.deleteService(new String[]{adminServiceService.getServiceGroup(serviceName)});
        }
        ServiceDeploymentUtil.isServiceDeleted(asServer.getBackEndUrl(), asServer.getSessionCookie(), serviceName);
    }

    public void gracefullyRestartServer() throws Exception {
        ServerAdminClient serverAdminClient = new ServerAdminClient(asServer.getBackEndUrl(),
                                                                    userInfo.getUserName(),
                                                                    userInfo.getPassword());
//        FrameworkProperties frameworkProperties =
//                FrameworkFactory.getFrameworkProperties(ProductConstant.APP_SERVER_NAME);
//        ServerGroupManager.getServerUtils().restartGracefully(serverAdminClient, frameworkProperties);

    }

    public boolean isServiceDeployed(String serviceName) throws RemoteException {
        return ServiceDeploymentUtil.isServiceDeployed(asServer.getBackEndUrl(),
                                                       asServer.getSessionCookie(), serviceName);
    }

    public boolean isServiceFaulty(String serviceName) throws RemoteException {
        return ServiceDeploymentUtil.isServiceFaulty(asServer.getBackEndUrl(),
                                                     asServer.getSessionCookie(), serviceName);
    }

    public String getSecuredServiceEndpoint(String serviceName) {
        return asServer.getSecureServiceUrl() + "/" + serviceName;
    }

    public void applySecurity(String scenarioNumber, String serviceName, String userGroup)
            throws SecurityAdminServiceSecurityConfigExceptionException, RemoteException,
                   InterruptedException {

        EnvironmentBuilder builder = new EnvironmentBuilder();
        securityAdminServiceClient =
                new SecurityAdminServiceClient(asServer.getBackEndUrl(), asServer.getSessionCookie());

        String path = builder.getFrameworkSettings().getEnvironmentVariables().getKeystorePath();
        String KeyStoreName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
        securityAdminServiceClient.applySecurity(serviceName, scenarioNumber, new String[]{userGroup},
                                                 new String[]{KeyStoreName}, KeyStoreName);
        Thread.sleep(2000);
    }*/
}
