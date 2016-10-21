package org.wso2.carbon.automation.distributed.utills;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.ResponseDetails;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.*;
import org.wso2.carbon.automation.distributed.FrameworkConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yasassri on 10/5/16.
 */
public class TestLinkSiteUtil {


    protected final TestLinkAPI api;
    protected final TestProject testProject;
    protected final TestPlan testPlan;
    protected final Platform platform;
    protected final Build build;

      /**
     * @param api TestLink Java API object
     * @param testProject TestLink Test Project
     * @param testPlan TestLink Test Plan
     * @param platform TestLink platform
     * @param build TestLink Build
     */
    public TestLinkSiteUtil(TestLinkAPI api, TestProject testProject, TestPlan testPlan,
                            Platform platform, Build build) {
        this.api = api;
        this.testProject = testProject;
        this.testPlan = testPlan;
        this.platform = platform;
        this.build = build;
    }

    /**
     * @return the TestLink Java API object
     */
    public TestLinkAPI getApi() {
        return api;
    }

    /**
     * @return the testProject
     */
    public TestProject getTestProject() {
        return testProject;
    }

    /**
     * @return the testPlan
     */
    public TestPlan getTestPlan() {
        return testPlan;
    }

    /**
     * @return the platform
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * @return the build
     */
    public Build getBuild() {
        return build;
    }

    /**
     * @param customFieldsNames Array of custom fields names
     * @return Array of automated test cases with custom fields
     */
    public TestCase[] getAutomatedTestCases(String[] customFieldsNames) {
        final TestCase[] testCases = this.api.getTestCasesForTestPlan(
                getTestPlan().getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null, // execute status
                ExecutionType.AUTOMATED,
                Boolean.TRUE,
                TestCaseDetails.FULL);

        ArrayList<TestCase> filteredTestcases = new ArrayList<TestCase>();

        for( final TestCase testCase : testCases ) {
            testCase.setTestProjectId(getTestProject().getId());
            testCase.setExecutionStatus(ExecutionStatus.NOT_RUN);
            if (customFieldsNames != null) {
                for(String customFieldName : customFieldsNames) {
                    final CustomField customField = this.api.getTestCaseCustomFieldDesignValue(
                            testCase.getId(),
                            null, /* testCaseExternalId */
                            testCase.getVersion(),
                            testCase.getTestProjectId(),
                            customFieldName,
                            ResponseDetails.FULL);
                    testCase.getCustomFields().add(customField);
                }
            }

            if(platform == null || testCase.getPlatform().getName().equals(platform.getName()))
                filteredTestcases.add(testCase);
        }

        return filteredTestcases.toArray(new TestCase[filteredTestcases.size()]);
    }

    public ArrayList<String> getTestCaseClassList(String[] customFieldsNames){
        final TestCase[] testCases = this.api.getTestCasesForTestPlan(
                getTestPlan().getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null, // execute status
                ExecutionType.AUTOMATED,
                Boolean.TRUE,
                TestCaseDetails.FULL);

        ArrayList<String> classList = new ArrayList<>();
        for( final TestCase testCase : testCases ) {
            testCase.setTestProjectId(getTestProject().getId());
            if (customFieldsNames != null) {
                for(String customFieldName : customFieldsNames) {
                    final CustomField customField = this.api.getTestCaseCustomFieldDesignValue(
                            testCase.getId(),
                            null, /* testCaseExternalId */
                            testCase.getVersion(),
                            testCase.getTestProjectId(),
                            customFieldName,
                            ResponseDetails.FULL);
                    // Get class#method value and split it and take the full qualified
                    String customFieldValue = customField.getValue();
                    if (customField.getName() != null && !customFieldValue.isEmpty()) {
                        classList.add(customField.getValue().split(FrameworkConstants.TESTLINK_CLASSMAP_SPLITTER)[0]);
                    }else {
                        // TODO : Nitify that there are testcases without class mappings. Can pring the TC ID if necessary
                    }
                }
            }
        }
        return dropDuplicateClasses(classList);
    }

    private ArrayList dropDuplicateClasses(ArrayList classList){
        Set<String> uniqueClasses = new HashSet<>(classList);

        return new ArrayList(uniqueClasses);
    }
}
