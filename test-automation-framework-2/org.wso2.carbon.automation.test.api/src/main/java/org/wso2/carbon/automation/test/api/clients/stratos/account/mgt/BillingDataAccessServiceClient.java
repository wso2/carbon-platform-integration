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

package org.wso2.carbon.automation.test.api.clients.stratos.account.mgt;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.account.mgt.stub.services.BillingDataAccessServiceExceptionException;
import org.wso2.carbon.account.mgt.stub.services.BillingDataAccessServiceStub;
import org.wso2.carbon.account.mgt.stub.services.beans.xsd.Customer;
import org.wso2.carbon.account.mgt.stub.services.beans.xsd.Subscription;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;

import java.rmi.RemoteException;

public class BillingDataAccessServiceClient {

    private static final Log log = LogFactory.getLog(BillingDataAccessServiceClient.class);

    private BillingDataAccessServiceStub billingDataAccessServiceStub;
    private final String serviceName = "BillingDataAccessService";

    public BillingDataAccessServiceClient(String backendServerURL, String sessionCookie)
            throws AxisFault {

        String endPoint = backendServerURL + serviceName;
        billingDataAccessServiceStub = new BillingDataAccessServiceStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, billingDataAccessServiceStub);
    }

    public BillingDataAccessServiceClient(String backendServerURL, String userName, String password)
            throws AxisFault {

        String endPoint = backendServerURL + serviceName;
        billingDataAccessServiceStub = new BillingDataAccessServiceStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, billingDataAccessServiceStub);
    }

    public boolean updateUsagePlan(String usagePlanName)
            throws Exception {

        return billingDataAccessServiceStub.changeSubscriptionByTenant(usagePlanName);

    }

    public void getActiveSubscriptionOfCustomer()
            throws RemoteException, BillingDataAccessServiceExceptionException {
        try {
            billingDataAccessServiceStub.getActiveSubscriptionOfCustomerByTenant();
        } catch (RemoteException e) {
            log.error("Subscription update failed:", e);
            throw new RemoteException("Subscription update failed:", e);
        } catch (BillingDataAccessServiceExceptionException e) {
            log.error("Subscription update failed :", e);
            throw new BillingDataAccessServiceExceptionException("Subscription update failed :", e);
        }
    }

    public Customer getCustomerWithName(String customerName)
            throws RemoteException, BillingDataAccessServiceExceptionException {
        Customer customer;
        try {
            customer = billingDataAccessServiceStub.getCustomerWithName(customerName);
        } catch (RemoteException e) {
            log.error("Subscription update failed:", e);
            throw new RemoteException("Subscription update failed:", e);
        } catch (BillingDataAccessServiceExceptionException e) {
            log.error("Subscription update failed :", e);
            throw new BillingDataAccessServiceExceptionException("Subscription update failed :", e);
        }
        return customer;
    }

    public Subscription getSubscription(int subscriptionId)
            throws RemoteException, BillingDataAccessServiceExceptionException {
        Subscription subscription;
        try {
            subscription = billingDataAccessServiceStub.getSubscription(subscriptionId);
        } catch (RemoteException e) {
            log.error("Subscription update failed:", e);
            throw new RemoteException("Subscription update failed:", e);
        } catch (BillingDataAccessServiceExceptionException e) {
            log.error("Subscription update failed :", e);
            throw new BillingDataAccessServiceExceptionException("Subscription update failed :", e);
        }
        return subscription;
    }

    public String getUsagePlanName(String tenantName) throws Exception {
        Customer customer;
        Subscription subscription;

        try {
            customer = billingDataAccessServiceStub.getCustomerWithName(tenantName);
            if (customer != null) {
                subscription = billingDataAccessServiceStub.getActiveSubscriptionOfCustomerByTenant();
                if (subscription != null) {
                    return subscription.getSubscriptionPlan();
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            String msg = "Error occurred while getting the usage place for tenant: " + tenantName;
            log.error(msg, e);
            throw new Exception(msg, e);
        }
        return null;
    }
}
