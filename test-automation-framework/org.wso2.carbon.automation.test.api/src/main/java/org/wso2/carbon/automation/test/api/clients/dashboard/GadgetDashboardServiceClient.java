package org.wso2.carbon.automation.test.api.clients.dashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.dashboard.stub.DashboardServiceStub;

import java.rmi.RemoteException;

public class GadgetDashboardServiceClient {
    private static final Log log = LogFactory.getLog(GadgetDashboardServiceClient.class);

    public void isSessionValid(DashboardServiceStub dashboardServiceStub) throws Exception {
        try {
            boolean isValid = dashboardServiceStub.isSessionValid();
            if (!isValid) {
                log.error("Invalid session found in isSessionValid test");
                throw new Exception("Invalid session found in isSessionValid test");
            } else {
                log.info("Successfully executed isSessionValid test");
            }
        } catch (RemoteException e) {
            throw new Exception("Failed to executed isSessionValid test" + e);
        }
    }

    // Get default gadget url set
    public void getDefaultGadgetUrlSet(DashboardServiceStub dashboardServiceStub, String userID)
            throws Exception {
        try {
            String[] defaultGadgetUrlSet = dashboardServiceStub.getDefaultGadgetUrlSet(userID);
            if (defaultGadgetUrlSet != null) {
                for (String gadget : defaultGadgetUrlSet) {
                    if (log.isDebugEnabled()) {
                        log.debug("DefaultGadget: " + gadget + " -available");
                    }
                }
            }
            log.info("Successfully executed getDefaultGadgetUrlSet test");
        } catch (RemoteException e) {
            throw new Exception("Failed to executed getDefaultGadgetUrlSet test" + e);
        }
    }

    // Get dashboard content for the given user as a bean
    public void getDashboardContent(DashboardServiceStub dashboardServiceStub, String userId,
                                    String dashboardName, String tDomain, String backendServerURL)
            throws Exception {

        try {
            dashboardServiceStub.getDashboardContent(userId, dashboardName, tDomain, backendServerURL);
            log.info("Successfully executed getDashboardContent test");

        } catch (RemoteException e) {
            throw new Exception("Failed to executed getDashboardContent test" + e);
        }
    }

    // Get tab layout for the user
    public void getTabLayout(DashboardServiceStub dashboardServiceStub, String userId,
                             String dashboardName) throws Exception {
        try {
            String tabLayout = dashboardServiceStub.getTabLayout(userId, dashboardName);
            if (!"0".equals(tabLayout)) {
                log.error("Failed to get tab layout");
                throw new Exception("Failed to get tab layout");
            } else {
                log.info("Successfully executed getTabLayout test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to executed getTabLayout test" + e);
        }
    }


    // Retrieves the stored layout
    public void getGadgetLayout(DashboardServiceStub dashboardServiceStub, String userId,
                                String tabId,
                                String dashboardName) throws Exception {
        try {
            String gadgetLayout = dashboardServiceStub.getGadgetLayout(userId, tabId, dashboardName);
            if (gadgetLayout == null) {
                log.error("Failed to get gadget layout");
                throw new Exception("Failed to get gadget layout");
            } else {
                log.info("Successfully executed getGadgetLayout test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to execute getGadgetLayout test" + e);
        }

    }

    // Dashboard read-only mode checking
    public void isReadOnlyMode(DashboardServiceStub dashboardServiceStub, String userId)
            throws Exception {
        try {
            boolean isReadOnlyMode = dashboardServiceStub.isReadOnlyMode(userId);
            if (isReadOnlyMode) {
                log.error("Dashboard can not be read only for this user");
                throw new Exception("Dashboard can not be read only for this user");
            } else {
                log.info("Successfully executed isReadOnlyMode test");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            throw new Exception("Failed to execute isReadOnlyMode test" + e);
        }
    }

    // Populate Default Three Column Layout
    public void populateDefaultThreeColumnLayout(DashboardServiceStub dashboardServiceStub,
                                                 String userId, String tabID) throws Exception {
        try {
            // this call caused to test setGadgetLayout service too
            String columnLayout = dashboardServiceStub.populateDefaultThreeColumnLayout(userId, tabID);
            if (columnLayout == null) {
                log.error("Failed to populate default three column layout");
                throw new Exception("Failed to populate default three column layout");
            } else {
                log.info("Successfully executed populateDefaultThreeColumnLayout test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to executed populateDefaultThreeColumnLayout test" + e);
        }
    }

    // Set a given preference value for a Gadget
    public void setGadgetPrefs(DashboardServiceStub dashboardServiceStub, String userId,
                               String gadgetId,
                               String prefId, String value, String dashboardName) throws Exception {
        try {
            boolean setGadgetPrefsStatus = dashboardServiceStub.setGadgetPrefs(userId, gadgetId, prefId, value, dashboardName);
            if (!setGadgetPrefsStatus) {
                log.error("Failed to set given preference value for the gadget");
                throw new Exception("Failed to set given preference value for the gadget");
            } else {
                log.info("Successfully executed setGadgetPrefs test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to executed setGadgetPrefs test" + e);
        }
    }

    // Retrieves a given preference value for a Gadget
    public void getGadgetPrefs(DashboardServiceStub dashboardServiceStub, String userId,
                               String gadgetId,
                               String prefId, String dashboardName) throws Exception {
        try {
            String gadgetPerfs = dashboardServiceStub.getGadgetPrefs(userId, gadgetId, prefId, dashboardName);
            if (gadgetPerfs == null) {
                log.error("Failed to retrieves a given preference value for a gadget");
                throw new Exception("Failed to retrieves a given preference value for a gadget");
            } else {
                log.info("Successfully executed getGadgetPrefs test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to executed getGadgetPrefs test" + e);
        }
    }

    // Get gadget urls to layout
    public void getGadgetUrlsToLayout(DashboardServiceStub dashboardServiceStub, String userID,
                                      String tabID,
                                      String dashboardName, String backendServerURL)
            throws Exception {
        try {
            String[] gadgetUrlsToLayout = dashboardServiceStub.getGadgetUrlsToLayout(
                    userID, tabID, dashboardName, backendServerURL);
            if (gadgetUrlsToLayout != null) {
                for (String gadget : gadgetUrlsToLayout) {
                    if (log.isDebugEnabled()) {
                        log.debug("gadgetUrl: " + gadget + " -available to layout");
                    }
                }
            }
            log.info("Successfully executed getGadgetUrlsToLayout test");
        } catch (RemoteException e) {
            throw new Exception("Failed to executed getGadgetUrlsToLayout test" + e);
        }
    }

    // Add new tab
    public int addNewTab(DashboardServiceStub dashboardServiceStub, String userID,
                         String tabTitle, String dashboardName) throws Exception {
        int tabID = 0;
        try {
            tabID = dashboardServiceStub.addNewTab(userID, tabTitle, dashboardName);
            log.info("Successfully executed addNewTab test");
        } catch (RemoteException e) {
            throw new Exception("Failed to executed addNewTab test" + e);
        }
        return tabID;
    }

    // Get title of the given tab
    public String getTabTitle(DashboardServiceStub dashboardServiceStub, String userId,
                              String tabId,
                              String dashboardName, String addedTabName) throws Exception {
        String tabTitle = null;
        try {
            tabTitle = dashboardServiceStub.getTabTitle(userId, tabId, dashboardName);
            if (!addedTabName.equals(tabTitle)) {
                log.error("Failed to get tab title");
                throw new Exception("Failed to get tab title");
            } else {
                log.info("Successfully executed getTabTitle test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to execute getTabTitle test" + e);
        }
        return tabTitle;
    }

    // Removes a given tab from the system
    public void removeTab(DashboardServiceStub dashboardServiceStub, String userId, String tabId,
                          String dashboardName) throws Exception {
        try {
            // This call caused to test removeGadget service too
            boolean removeTabStatus = dashboardServiceStub.removeTab(userId, tabId, dashboardName);
            if (!removeTabStatus) {
                log.error("Failed to remove tab");
                throw new Exception("Failed to remove tab");
            } else {
                log.info("Successfully executed removeTab test");
            }

        } catch (RemoteException e) {
            throw new Exception("Failed to execute removeTab test" + e);
        }
    }

    // Duplicate tab
    public int duplicateTab(DashboardServiceStub dashboardServiceStub, String userID,
                            String dashboardName, String sourceTabId, String newTabName)
            throws Exception {
        int tabID = 0;
        try {
            tabID = dashboardServiceStub.duplicateTab(userID, dashboardName, sourceTabId, newTabName);
            log.info("Successfully executed duplicateTab test");
        } catch (RemoteException e) {
            throw new Exception("Failed to executed duplicateTab test" + e);
        }
        return tabID;
    }

    // Add gadget to user
    public void addGadgetToUser(DashboardServiceStub dashboardServiceStub, String userID,
                                String tabID, String url,
                                String dashboardName, String gadgetGroup) throws Exception {
        try {
            boolean addGadgetToUserStatus = dashboardServiceStub.addGadgetToUser(userID, tabID, url, dashboardName, gadgetGroup);
            if (!addGadgetToUserStatus) {
                log.error("Failed to add gadget to user");
                throw new Exception("Failed to add gadget to user");
            } else {
                log.info("Successfully executed addGadgetToUser test");
            }
        } catch (RemoteException e) {
            throw new Exception("Failed to executed addGadgetToUser test" + e);
        }

    }
}
