package org.wso2.carbon.automation.distributed.commons;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.model.Platform;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.automation.distributed.utills.TestLinkSiteUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class TestLinkBuilder {

    public TestLinkSiteUtil getTestLinkSite(String testLinkUrl, String testLinkDevKey, String testProjectName,
            String testPlanName, String platformName) throws MalformedURLException {
        final TestLinkAPI api;
        final URL url = new URL(testLinkUrl);
        api = new TestLinkAPI(url, testLinkDevKey);

        final TestProject testProject = api.getTestProjectByName(testProjectName);
        final TestPlan testPlan = api.getTestPlanByName(testPlanName, testProjectName);

        Platform platform = null;
        if (StringUtils.isNotBlank(platformName)) {
            final Platform platforms[] = api.getProjectPlatforms(testProject.getId());
            for (Platform p : platforms) {
                if (p.getName().equals(platformName)) {
                    platform = p;
                    break;
                }
            }
        }
        return new TestLinkSiteUtil(api, testProject, testPlan, platform);
    }
}
