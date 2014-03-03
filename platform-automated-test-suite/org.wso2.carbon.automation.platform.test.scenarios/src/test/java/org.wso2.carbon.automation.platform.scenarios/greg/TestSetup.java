package org.wso2.carbon.automation.platform.scenarios.greg;

import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

import java.io.File;
import java.rmi.RemoteException;

public class TestSetup {

    protected static Registry registry = null;
    private static final Log log = LogFactory.getLog(TestSetup.class);

    public void init() throws RemoteException, LoginAuthenticationExceptionException, RegistryException {
        log.info("Initializing G-Reg Platform Tests");
        try {
            EnvironmentBuilder builder = new EnvironmentBuilder().greg(0);
            EnvironmentVariables environmentVariables = builder.build().getGreg();
            String url = environmentVariables.getBackEndUrl();
            UserInfo userInfo = UserListCsvReader.getUserInfo(0);
            String CARBON_HOME = ProductConstant.getCarbonHome("greg");

            registry = new WSRegistryServiceClient(url, userInfo.getUserName(),
                    userInfo.getPassword(),
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                            CARBON_HOME +
                                    File.separator + "repository" + File.separator + "deployment" +
                                    File.separator + "client", ServerConfiguration.getInstance().getFirstProperty("Axis2Config.clientAxis2XmlLocation")));

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("G-Reg Platform Tests Initialized");
    }

}
