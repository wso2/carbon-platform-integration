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
package org.wso2.carbon.automation.test.api.clients.rssmanager;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.rssmanager.ui.stub.RSSAdminRSSManagerExceptionException;
import org.wso2.carbon.rssmanager.ui.stub.RSSAdminStub;
import org.wso2.carbon.rssmanager.ui.stub.types.*;
import org.wso2.carbon.rssmanager.ui.stub.types.config.environment.RSSEnvironmentContext;

import java.rmi.RemoteException;

public class RSSManagerAdminServiceClient {
    private static final Log log = LogFactory.getLog(RSSManagerAdminServiceClient.class);

    private final String serviceName = "RSSManagerAdminService";
    private RSSAdminStub rssAdminStub;

    private static final String ADMIN_CONSOLE_EXTENSION_NS =
            "http://www.wso2.org/products/wso2commons/adminconsole";

    private static final OMNamespace ADMIN_CONSOLE_OM_NAMESPACE =
            OMAbstractFactory.getOMFactory().createOMNamespace(ADMIN_CONSOLE_EXTENSION_NS, "instance");

    private static final OMFactory omFactory = OMAbstractFactory.getOMFactory();

    private static final String NULL_NAMESPACE = "";

    private static final OMNamespace NULL_OMNS = omFactory.createOMNamespace(NULL_NAMESPACE, "");

    public RSSManagerAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        rssAdminStub = new RSSAdminStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, rssAdminStub);
    }

    public RSSManagerAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        rssAdminStub = new RSSAdminStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, rssAdminStub);
    }

    public void createDatabase(RSSEnvironmentContext ctx, Database database)
            throws RemoteException {

        if (log.isDebugEnabled()) {
            log.debug("Database Name :" + database.getName());
            log.debug("RSSInstance Name :" + database.getRssInstanceName());
        }
        try {
            rssAdminStub.createDatabase(ctx, database);
            log.info("Database Created");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while creating database '" + database.getName() + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
    }

    public void createCarbonDataSource(RSSEnvironmentContext ctx,
                                       UserDatabaseEntry userDatabaseEntry)
            throws RemoteException, RSSAdminRSSManagerExceptionException {

        rssAdminStub.createCarbonDataSource(ctx, userDatabaseEntry);
    }

    public void dropDatabase(RSSEnvironmentContext ctx, String databaseName)
            throws RemoteException {
        if (log.isDebugEnabled()) {
            log.debug("DatabaseName :" + databaseName);
        }
        try {
            rssAdminStub.dropDatabase(ctx, databaseName);
            log.info("Database Dropped");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while dropping the database '" + databaseName + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
    }

    public Database[] getDatabaseInstanceList(RSSEnvironmentContext ctx)
            throws RemoteException {
        Database[] databaseList = new Database[0];
        try {
            databaseList = rssAdminStub.getDatabases(ctx);
        } catch (RSSAdminRSSManagerExceptionException e) {
            log.error("Error occurred while retrieving database list", e);
        }
        return databaseList;
    }

    public String[] getRSSEnvironmentNames()
            throws RemoteException {
        String[] rssEnvironment = new String[0];
        try {
            rssEnvironment = rssAdminStub.getRSSEnvironmentNames();
        } catch (RSSAdminRSSManagerExceptionException e) {
            log.error("Error occurred while rss environment names", e);
        }
        return rssEnvironment;
    }

    public Database getDatabaseInstance(RSSEnvironmentContext ctx, String databaseName)
            throws RemoteException {
        Database[] databaseList = getDatabaseInstanceList(ctx);
        Database dbInstance = null;
        if (databaseList == null) {
            return null;
        }
        for (Database dbEntry : databaseList) {
            if (dbEntry.getName().equals(databaseName)) {
                dbInstance = dbEntry;
                break;
            }
        }
        return dbInstance;
    }

    public void createPrivilegeGroup(RSSEnvironmentContext ctx, String privilegeGroupName)
            throws RemoteException {
        DatabasePrivilegeTemplate privilegeGroupTemplate = new DatabasePrivilegeTemplate();

        privilegeGroupTemplate.setName(privilegeGroupName);
        privilegeGroupTemplate.setPrivileges(getAllDatabasePermission());

        if (log.isDebugEnabled()) {
            log.debug("Privilege Group Name: " + privilegeGroupName);
        }

        try {
            rssAdminStub.createDatabasePrivilegesTemplate(ctx, privilegeGroupTemplate);
            log.info("Privilege Group Added");
        } catch (RSSAdminRSSManagerExceptionException e) {
            throw new RemoteException("");
        }
    }

    public DatabasePrivilegeTemplate getPrivilegeGroup(RSSEnvironmentContext ctx,
                                                       String privilegeGroupName)
            throws RemoteException {
        DatabasePrivilegeTemplate[] privilegeGroups = getUserPrivilegeGroups(ctx);
        DatabasePrivilegeTemplate userPrivilegeGroup = null;
        if (privilegeGroups == null) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("privilege group name :" + privilegeGroupName);
        }
        for (DatabasePrivilegeTemplate priGroup : privilegeGroups) {
            if (priGroup.getName().equals(privilegeGroupName)) {
                userPrivilegeGroup = priGroup;
                log.info("Privilege group found");
                break;
            }
        }

        return userPrivilegeGroup;
    }

    public void dropPrivilegeGroup(RSSEnvironmentContext ctx, String templateName)
            throws RemoteException {
        if (log.isDebugEnabled()) {
            log.debug("privilege group id :" + templateName);
        }
        try {
            rssAdminStub.dropDatabasePrivilegesTemplate(ctx, templateName);
            log.info("privilege group removed");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred dropping the database privilege template '" +
                         templateName + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
    }

    public DatabasePrivilegeTemplate[] getUserPrivilegeGroups(RSSEnvironmentContext ctx)
            throws RemoteException {
        DatabasePrivilegeTemplate[] template;
        try {
            template = rssAdminStub.getDatabasePrivilegesTemplates(ctx);
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving database privilege template list";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
        return template;
    }

    public DatabaseUser getDatabaseUser(RSSEnvironmentContext ctx, String username)
            throws RemoteException {
        DatabaseUser user;
        try {
            user = rssAdminStub.getDatabaseUser(ctx, username);
            log.info("Database user data received");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving information related to the database " +
                         "user '" + username + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
        return user;
    }

    public Database getDatabase(RSSEnvironmentContext ctx, String databaseName)
            throws RemoteException {
        Database database;
        try {
            database = rssAdminStub.getDatabase(ctx, databaseName);
            log.info("Database configuration received");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving the configuration of the database '" +
                         databaseName + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
        return database;
    }

    public RSSInstance[] getRSSInstanceList(RSSEnvironmentContext ctx)
            throws RemoteException {
        RSSInstance[] rssInstance = new RSSInstance[0];
        try {
            rssInstance = rssAdminStub.getRSSInstances(ctx);
            log.info("RSS instance list retrieved");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving the RSS instance list";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }

        return rssInstance;
    }

    public RSSInstance getRSSInstance(RSSEnvironmentContext ctx)
            throws RemoteException {
        RSSInstance rssInstance;
        try {
            rssInstance = rssAdminStub.getRSSInstance(ctx);
            log.info("RSS instance configuration retrieved");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving the configuration of RSS instance '";

            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
        return rssInstance;
    }

    public void createDatabaseUser(RSSEnvironmentContext ctx, String userName, String password,
                                   String rssInstanceName)
            throws RemoteException {
        DatabaseUser user = new DatabaseUser();
        user.setName(userName);
        user.setPassword(password);
        if (log.isDebugEnabled()) {
            log.debug("userName : " + userName);
            log.debug("rssInstanceName : " + rssInstanceName);
        }
        try {
            rssAdminStub.createDatabaseUser(ctx, user);
            log.info("Database user " + userName + " created");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while creating database user '" + userName + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
    }

    public void dropDatabaseUser(RSSEnvironmentContext ctx, String username)
            throws RemoteException {
        if (log.isDebugEnabled()) {
            log.debug("Username : " + username);
        }
        try {
            rssAdminStub.dropDatabaseUser(ctx, username);
            log.info("User Deleted");
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while dropping the database user '" + username + "'";
            log.error(msg, e);
            throw new RemoteException(msg, e);
        }
    }

    public DatabaseUser[] getUsersAttachedToDatabase(RSSEnvironmentContext ctx,
                                                             String databaseName)
            throws RemoteException {
        DatabaseUser[] userList = new DatabaseUser[0];
        if (log.isDebugEnabled()) {
            log.debug("RSS Instance Name : " + ctx.getRssInstanceName());
            log.debug("Database Name : " + databaseName);
        }

        try {
            userList = rssAdminStub.getUsersAttachedToDatabase(ctx, databaseName);
        } catch (RSSAdminRSSManagerExceptionException e) {
            String msg = "Error occurred while retrieving the database users attached to the " +
                         "database '" + databaseName + "' on RSS instance '" + databaseName + "'";
        }

        return userList;
    }

//    public String createCarbonDSFromDatabaseUserEntry(int databaseInstanceId,
//                                                      int dbUserId)
//            throws  RemoteException {
//        String carbonDataSource;
//        if (log.isDebugEnabled()) {
//            log.debug("databaseInstanceId " + databaseInstanceId);
//        }
//
//        carbonDataSource = rssAdminStub.createCarbonDSFromDatabaseUserEntry(databaseInstanceId, dbUserId);
//        log.debug(carbonDataSource);
//        carbonDataSource = carbonDataSource.substring((carbonDataSource.indexOf(" '") + 2), carbonDataSource.indexOf("' "));
//        if (log.isDebugEnabled()) {
//            log.debug("Data Source Name : " + carbonDataSource);
//        }
//        log.info("Data Source Created");
//
//        return carbonDataSource;
//    }

    private static DatabasePrivilegeSet getAllDatabasePermission() {

        DatabasePrivilegeSet privileges = new DatabasePrivilegeSet();
        privileges.setSelectPriv("Y");
        privileges.setInsertPriv("Y");
        privileges.setUpdatePriv("Y");
        privileges.setDeletePriv("Y");
        privileges.setCreatePriv("Y");
        privileges.setAlterPriv("Y");
        privileges.setCreateTmpTablePriv("Y");
        privileges.setLockTablesPriv("Y");
        privileges.setCreateRoutinePriv("Y");
        privileges.setAlterRoutinePriv("Y");
        privileges.setCreateViewPriv("Y");
        privileges.setShowViewPriv("Y");
        privileges.setExecutePriv("Y");
        privileges.setEventPriv("Y");
        privileges.setTriggerPriv("Y");
        privileges.setDropPriv("Y");
        privileges.setReferencesPriv("Y");
        privileges.setGrantPriv("Y");
        privileges.setIndexPriv("Y");

        return privileges;
    }

    public String getFullyQualifiedUsername(String username, String tenantDomain) {
        if (tenantDomain != null) {

            /* The maximum number of characters allowed for the username in mysql system tables is
             * 16. Thus, to adhere the aforementioned constraint as well as to give the username
             * an unique identification based on the tenant domain, we append a hash value that is
             * created based on the tenant domain */
            byte[] bytes = intToByteArray(tenantDomain.hashCode());
            return username + "_" + Base64.encode(bytes);
        }
        return username;
    }

    private static byte[] intToByteArray(int value) {
        byte[] b = new byte[6];
        for (int i = 0; i < 6; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
}
