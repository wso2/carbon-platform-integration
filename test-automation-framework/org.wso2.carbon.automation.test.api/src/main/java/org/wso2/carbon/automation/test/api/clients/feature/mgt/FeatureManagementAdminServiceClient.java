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
package org.wso2.carbon.automation.test.api.clients.feature.mgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.feature.mgt.stub.ProvisioningAdminServiceStub;
import org.wso2.carbon.feature.mgt.stub.RepositoryAdminServiceStub;
import org.wso2.carbon.feature.mgt.stub.prov.data.*;

import java.rmi.RemoteException;

public class FeatureManagementAdminServiceClient {
    private static final Log log = LogFactory.getLog(FeatureManagementAdminServiceClient.class);
    private static final String INSTALL_ACTION = "org.wso2.carbon.prov.action.install";
    private static final String UNINSTALL_ACTION = "org.wso2.carbon.prov.action.uninstall";
//    private static final String REVERT_ACTION = "org.wso2.carbon.prov.action.revert";

    private RepositoryAdminServiceStub repositoryAdminServiceStub;
    private ProvisioningAdminServiceStub provisioningAdminServiceStub;
    private String endPoint;

    public FeatureManagementAdminServiceClient(String sessionCookie, String backEndUrl)
            throws AxisFault {
        this.endPoint = backEndUrl;
        repositoryAdminServiceStub = new RepositoryAdminServiceStub(endPoint + "RepositoryAdminService");
        provisioningAdminServiceStub = new ProvisioningAdminServiceStub(endPoint + "ProvisioningAdminService");
        AuthenticateStub.authenticateStub(sessionCookie, repositoryAdminServiceStub);
        AuthenticateStub.authenticateStub(sessionCookie, provisioningAdminServiceStub);
    }

    public boolean addRepository(String location, String name) throws RemoteException {
        return repositoryAdminServiceStub.addRepository(location, name);
    }

    public RepositoryInfo[] getAllRepositories() throws RemoteException {
        return repositoryAdminServiceStub.getAllRepositories();
    }

    public void removeRepository(String location) throws RemoteException {
        repositoryAdminServiceStub.removeRepository(location);
    }

    public void getInstallableFeatures(String location, boolean groupByCategory,
                                       boolean hideInstalled,
                                       boolean showLatest) throws RemoteException {
        repositoryAdminServiceStub.getInstallableFeatures(location, groupByCategory, hideInstalled, showLatest);
    }

    public Feature[] getInstallableFeatures(String location) throws RemoteException {
        return repositoryAdminServiceStub.getInstallableFeatures(location, false, true, false);
    }

    public boolean installFeatures(FeatureInfo[] featuresToInstall) throws Exception {
        ProvisioningActionInfo provisioningActionInfo = new ProvisioningActionInfo();
        ProvisioningActionResultInfo resultInfo;
        provisioningActionInfo.setFeaturesToInstall(featuresToInstall);
        provisioningActionInfo.setActionType(INSTALL_ACTION);

        resultInfo = provisioningAdminServiceStub.reviewProvisioningAction(provisioningActionInfo);
        if (resultInfo != null) {

            if (resultInfo.getProceedWithInstallation()) {
                return provisioningAdminServiceStub.performProvisioningAction(INSTALL_ACTION);
            } else {
                return false;
            }
        } else {
            throw new Exception("Failed to review the installation plan");
        }

    }

    public boolean installFeature(String featureID, String version) throws Exception {
        FeatureInfo featuresToInstall = new FeatureInfo();
        featuresToInstall.setFeatureID(featureID);
        featuresToInstall.setFeatureVersion(version);

        return installFeatures(new FeatureInfo[]{featuresToInstall});


    }

    public boolean uninstallFeatures(FeatureInfo[] featuresToUninstall) throws Exception {
        ProvisioningActionInfo provisioningActionInfo = new ProvisioningActionInfo();
        ProvisioningActionResultInfo resultInfo;
        provisioningActionInfo.setFeaturesToUninstall(featuresToUninstall);
        provisioningActionInfo.setActionType(UNINSTALL_ACTION);

        resultInfo = provisioningAdminServiceStub.reviewProvisioningAction(provisioningActionInfo);
        if (resultInfo != null) {

            if (resultInfo.getProceedWithInstallation()) {
                return provisioningAdminServiceStub.performProvisioningAction(UNINSTALL_ACTION);
            } else {
                return false;
            }
        } else {
            throw new Exception("Failed to review the un installation plan");
        }

    }

    public boolean uninstallFeature(String featureID, String version) throws Exception {
        FeatureInfo featuresToUninstall = new FeatureInfo();
        featuresToUninstall.setFeatureID(featureID);
        featuresToUninstall.setFeatureVersion(version);

        return uninstallFeatures(new FeatureInfo[]{featuresToUninstall});


    }

    public ProvisioningActionResultInfo reviewProvisioningAction(FeatureInfo[] featuresInfo,
                                                                 String actionType)
            throws RemoteException {
        ProvisioningActionInfo provisioningActionInfo = new ProvisioningActionInfo();
        provisioningActionInfo.setFeaturesToInstall(featuresInfo);
        provisioningActionInfo.setActionType(actionType);
        return provisioningAdminServiceStub.reviewProvisioningAction(provisioningActionInfo);
    }

    public boolean performProvisioningAction(String actionType) throws RemoteException {
        return provisioningAdminServiceStub.performProvisioningAction(actionType);
    }
}
