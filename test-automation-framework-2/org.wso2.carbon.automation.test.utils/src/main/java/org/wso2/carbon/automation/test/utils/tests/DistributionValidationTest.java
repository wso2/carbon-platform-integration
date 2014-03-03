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
package org.wso2.carbon.automation.test.utils.tests;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.automation.test.utils.tests.utils.DistributionValidationUtils;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * This class can be used to to test,
 * 1.Validation of jar files inside the product distribution against the LICENSE.txt.
 * 2.Recursive scan for no occurrence of SNAPSHOT in file names and contents of textual files
 * (.txt, .xsd, etc).
 * 3.Validate duplicate .jar entries in LICENSE file.
 * 4.Check whether maven variables are properly replaced inside configuration files found of
 * repository/conf
 * 5.Validate product distribution size against the latest release distribution in the maven repo
 * 6.Identification of duplicate jars inside the distribution
 * 7.Product specific checks - validate sample directory - versions, resources validation
 */
public abstract class DistributionValidationTest {

    private static final Log log = LogFactory.getLog(DistributionValidationTest.class);
    private static final HashSet<String> licenceJarList = new HashSet<String>();
    private static final HashSet<String> duplicateJarList = new HashSet<String>();
    private static final HashMap<File, Integer> snapshotKeywordMap = new HashMap<File, Integer>();
    private static final HashMap<String, Exception> xsdValidateMap = new HashMap<String, Exception>();
    private static final String SAMPLES_DIRECTORY = "samples";
    private static final String KEYWORD = "SNAPSHOT";
    private static ArrayList<String> unidentifiedVersionJars = new ArrayList<String>();
    private static HashSet<String> distributionDuplicateJarList = new HashSet<String>();
    private static ArrayList<String> directoryLists = new ArrayList<String>();
    private ArrayList<String> versionArr = new ArrayList<String>();
    private List<File> jarFileListInDistribution;
    private String productPath;
    private File reportFile;
    private File distributionVersion;

    abstract public String getPathToXSD();

    abstract public String[] getPathToXML();

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        productPath = System.getProperty(ServerConstants.CARBON_HOME);

        reportFile = new File(FrameworkPathUtil.getReportLocation() + File.separator
                + "DistributionValidationTestReport.txt");

        // getting the jar files list inside the distribution directory
        jarFileListInDistribution = (List<File>) FileUtils.listFiles(new File(productPath),
                new String[]{"jar"}, true);

        // getting the running distribution name
        distributionVersion = new File(productPath.split("/")
                [productPath.split("/").length - 1] + ".zip");

        // derive the jar list from LICENSE.txt
        DistributionValidationUtils.readLicenseFile(productPath, licenceJarList, duplicateJarList);

    }

    @Test(groups = "wso2.all", description = "Validation of jar files mentioned in the" +
            " LICENSE.txt file against the distribution")
    public void testValidateJarFilesAgainstDistribution() throws Exception {

        assertTrue("Jar entries does not exist in LICENSE.txt", licenceJarList.size() > 0);

        ArrayList<String> additionalJarFilesInLicenceFile = new ArrayList<String>();

        for (String licenceJar : licenceJarList) {
            if (!jarFileListInDistribution.toString().contains(licenceJar)) {
                additionalJarFilesInLicenceFile.add(licenceJar);
            }
        }

        DistributionValidationUtils.reportGeneratorList(additionalJarFilesInLicenceFile, "Following jar files mentioned" +
                " in the LICENSE.txt cloud not be found inside the product distribution", reportFile);

        assertFalse("Some of the jar files mentioned in the LICENSE.txt are unavailable" +
                " in the product distribution", additionalJarFilesInLicenceFile.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Validation of jar files inside the" +
            " distribution against LICENSE.txt",
            dependsOnMethods = "testValidateJarFilesAgainstDistribution")
    public void testValidateJarFilesAgainstLicenceFile() throws Exception {

        ArrayList<String> jarsNotMentionedInLicenceFile = new ArrayList<String>();

        for (Object jarFileInDistribution : jarFileListInDistribution) {
            String fileName = ((File) jarFileInDistribution).getName();
            if (!licenceJarList.toString().contains(fileName) && !fileName.contains("emma")) {
                jarsNotMentionedInLicenceFile.add(jarFileInDistribution.toString());
            }
        }

        DistributionValidationUtils.reportGeneratorList(jarsNotMentionedInLicenceFile, "Following jar files inside the " +
                "product distribution should be mentioned in LICENSE.txt file", reportFile);

        assertTrue("Some jar file names in the product distribution were not" +
                "mentioned in the LICENCE.txt file", jarsNotMentionedInLicenceFile.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Validate duplicate jar entries inside LICENSE.txt",
            dependsOnMethods = "testValidateJarFilesAgainstLicenceFile")
    public void testValidateDuplicateJarEntriesInLicenceFile() throws Exception {

        boolean isDuplicateJars = false;

        if (duplicateJarList.size() > 0) {

            isDuplicateJars = true;

            for (String duplicateJarFile : duplicateJarList) {
                log.info("Duplicate jar entry name " + duplicateJarFile);
            }

            DistributionValidationUtils.reportGeneratorList(new ArrayList<String>(duplicateJarList), "Following are the " +
                    "duplicate jar file list in the LICENSE.txt file", reportFile);

        }

        assertFalse("Duplicate jar entries exist in LICENSE.txt", isDuplicateJars);
    }

    @Test(groups = "wso2.all", description = "Recursive scan to identify SNAPSHOT keyword inside " +
            "the distribution", dependsOnMethods = "testValidateDuplicateJarEntriesInLicenceFile")
    public void testRecursiveScanForSnapshotKeyword() throws Exception {

        String[] extensions = new String[]{"txt", "xsd", "js"};

        // getting the textual file list
        List<File> distributionTextualFileList = DistributionValidationUtils.recursiveScanToRetrieveTextualFiles
                (productPath, extensions);

        ArrayList<String> textualFileNameWithSnapshotKeyword = new ArrayList<String>();

        for (File textualFile : distributionTextualFileList) {

            int count;

            //checking for file names which contains the keyword
            if (textualFile.getName().contains(KEYWORD)) {
                textualFileNameWithSnapshotKeyword.add(textualFile.toString());
            }

            // checking for the specific keyword inside textual file contents
            String contents = FileUtils.readFileToString(textualFile);
            count = StringUtils.countMatches(contents, KEYWORD);

            if (count > 0) {
                snapshotKeywordMap.put(textualFile, count);
            }
        }

        Iterator<File> it = jarFileListInDistribution.iterator();
        ArrayList<String> jarFileNameWithSnapshotKeyword = new ArrayList<String>();

        while (it.hasNext()) {
            String fileName = (it.next()).getName();
            if (fileName.contains(KEYWORD)) {
                jarFileNameWithSnapshotKeyword.add(fileName);
            }
        }

        DistributionValidationUtils.reportGeneratorList(textualFileNameWithSnapshotKeyword, "Following are the" +
                " textual file names which contain " + KEYWORD + " keyword", reportFile);

        DistributionValidationUtils.reportGeneratorMap(snapshotKeywordMap, "List of textual file names together" +
                " with number of occurrences of " + KEYWORD + " keyword", reportFile);

        DistributionValidationUtils.reportGeneratorList(jarFileNameWithSnapshotKeyword, "List of jar file " +
                "names which contain " + KEYWORD + " keyword", reportFile);

        assertFalse("Textual file names with " + KEYWORD + " exists inside the product distribution",
                textualFileNameWithSnapshotKeyword.size() > 0);

        assertFalse("Occurrence of the keyword" + KEYWORD + " inside a textual file/s contents " +
                "detected", snapshotKeywordMap.size() > 0);

        assertFalse("Jar file names with " + KEYWORD + " exists inside the product distribution",
                jarFileNameWithSnapshotKeyword.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Check whether Maven variables are properly " +
            "being replaced inside configuration files of repository/conf"/*,
            dependsOnMethods = "testRecursiveScanForSnapshotKeyword"*/)
    public void testMavenVariablesReplacement() throws Exception {

        // getting xml file path
        String[] xmlFileList = getPathToXML();

        String pathToXML = null;

        for (String xmlFile : xmlFileList) {
            boolean isRecursive = true;
            Collection files = FileUtils.listFiles(new File(productPath + File.separator
                    + "repository" + File.separator + "conf"), null, isRecursive);

            for (Object fileName : files) {
                File file = (File) fileName;
                if (file.getName().equals(xmlFile)) {
                    pathToXML = file.getAbsolutePath();
                    break;
                }
            }
            DistributionValidationUtils.validateXml(xsdValidateMap, pathToXML, (getPathToXSD() + File.separator +
                    new File(pathToXML).getName().replace(".xml", ".xsd")));
        }

        DistributionValidationUtils.reportGeneratorMap(xsdValidateMap, "Following are the configuration file " +
                "names together with the exceptions encountered while validating respective " +
                "xml schemas", reportFile);

        assertFalse("configuration files - maven variable replacement failure",
                xsdValidateMap.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Comparison of distribution sizes",
            dependsOnMethods = "testMavenVariablesReplacement")
    public void testCompareDistributionSize() throws Exception {

        double runningDistributionSize;
        double mavenDistributionSize = 0;

        boolean mavenDistributionStatus = false;
        boolean sizeDifferenceStatus = false;

        String path;
        String productName = distributionVersion.getName().split("wso2")[1].split("-")[0];

        if (productName.contains("as")) {
            path = "appserver" + File.separator + "wso2as" + File.separator;
        } else {
            path = productName + File.separator + "wso2" + productName + File.separator;

        }

        String urlToDistributionList = "http:" + File.separator + File.separator +
                "maven.wso2.org" + File.separator + "nexus" + File.separator + "content" +
                File.separator + "repositories" + File.separator + "wso2maven2" + File.separator +
                "org" + File.separator + "wso2" + File.separator + path;

        List<String> linksToVersion = DistributionValidationUtils.getLinks(urlToDistributionList);
        List<String> linksFromVersion = DistributionValidationUtils.getLinks(linksToVersion.get(linksToVersion.size() - 1));

        URL url = null;

        for (Object link : linksFromVersion) {
            String temp = link.toString();
            if (temp.contains(".zip") && !temp.contains(".zip.")) {
                url = new URL(temp);
                break;
            }
        }

        HttpURLConnection conn;
        if (url != null) {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            mavenDistributionSize = conn.getContentLength();
            mavenDistributionStatus = true;
            conn.disconnect();
        }

        assertTrue("Either no product distribution detected in the maven repo " +
                "or connection establishment failure", mavenDistributionStatus);

        File distributionFile = new File(System.getProperty("carbon.zip"));

        //calculating the running zip file size
        runningDistributionSize = distributionFile.length();

        HashMap<String, String> resultsDataMap = new HashMap<String, String>();
        DecimalFormat df = new DecimalFormat("#.00");

        String runningDistribution = df.format(runningDistributionSize / (1024 * 1024));
        String mavenDistribution = df.format(mavenDistributionSize / (1024 * 1024));
        String difference = df.format((mavenDistributionSize - runningDistributionSize)
                / (1024 * 1024));

        resultsDataMap.put("Running Distribution Size", runningDistribution + " MB");
        resultsDataMap.put("Maven Distribution Size", mavenDistribution + " MB");
        resultsDataMap.put("The difference between running distribution and maven distribution" +
                " sizes are ", difference + " MB");

        // checking whether size of the running distribution is acceptable
        if (Double.parseDouble(difference) >= 20) {

            DistributionValidationUtils.reportGeneratorMap(resultsDataMap, "Running distribution size " +
                    "comparison failure.Below are the sizes of two packs compared.", reportFile);

            sizeDifferenceStatus = true;
        } else {
            DistributionValidationUtils.reportGeneratorMap(resultsDataMap, "Running distribution size comparison passed." +
                    " Below are the sizes of two packs compared.", reportFile);
        }

        assertFalse("Running Distribution size exceeds the acceptable limit size range " +
                "compared to the previous released distribution size", sizeDifferenceStatus);
    }

    @Test(groups = "wso2.all", description = "Identification of duplicate jar files",
            dependsOnMethods = "testCompareDistributionSize")
    public void testIdentifyingDuplicateJarFiles() throws Exception {

        //check same directory duplicate jars
        Set<String> duplicateSet = DistributionValidationUtils.getDuplicateJarSet(jarFileListInDistribution);

        //check different directories duplicate jars
        DistributionValidationUtils.identifyDuplicateJars(jarFileListInDistribution, distributionVersion,
                distributionDuplicateJarList, unidentifiedVersionJars);

        DistributionValidationUtils.reportGeneratorList(new ArrayList<String>(duplicateSet),
                "These are duplicate jar files in the same directory", reportFile);
        DistributionValidationUtils.reportGeneratorList(new ArrayList<String>(distributionDuplicateJarList),
                "These jar files were identified as duplicates", reportFile);
        DistributionValidationUtils.reportGeneratorList(new ArrayList<String>(unidentifiedVersionJars),
                "Following jars were excluded from the search for duplicate jars", reportFile);

        assertFalse("Duplicated jar file identified in same directories", duplicateSet.size() > 0);
        assertFalse("Duplicated jar file identified in different versions",
                distributionDuplicateJarList.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Product specific checks - whether " +
            "samples/resources are properly packaged",
            dependsOnMethods = "testIdentifyingDuplicateJarFiles")
    public void testSamplesDirectoryStructureSrcValidation() throws Exception {

        HashMap<String, String> structureViolation = new HashMap<String, String>();
        String samplesDirPath = productPath + File.separator + SAMPLES_DIRECTORY;

        if (new File(samplesDirPath).exists()) {
            versionArr = DistributionValidationUtils.validateSamplesDirectoryStructureIdentifyVersions(samplesDirPath,
                    versionArr);
            DistributionValidationUtils.reportGeneratorList(versionArr, "Following are the sample directories " +
                    "with versions embedded", reportFile);
        } else {
            DistributionValidationUtils.reportGeneratorList(versionArr, "No sample directory detected.", reportFile);
        }

        DistributionValidationUtils.searchDirectoryByName(samplesDirPath, directoryLists, "/src");

        // violation of samples/src standard
        Iterator<String> itTemp = directoryLists.iterator();

        String readmeFile;
        String pomFile;
        String buildFile;

        while (itTemp.hasNext()) {

            readmeFile = "Not Available";
            pomFile = "Not Available";
            buildFile = "Not Available";

            String parentDir = (new File(itTemp.next())).getParent();
            File folder = new File(parentDir);
            // gets you the list of files at this folder
            File[] listOfFiles = folder.listFiles();
            // loop through each of the files looking for filenames that match
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    String filename = listOfFile.getName();
                    if (filename.equals("README")) {
                        readmeFile = "Available";
                    } else if (filename.equals("pom.xml")) {
                        pomFile = "Available";
                    } else if (filename.equals("build.xml")) {
                        buildFile = "Available";
                    }
                }
            }
            if (!readmeFile.equals("Available") || (!pomFile.equals("Available") ||
                    !buildFile.equals("Available"))) {
                structureViolation.put(parentDir, "README File " + readmeFile + " pom.xml" +
                        " " + pomFile + " build.xml " + buildFile);
            }
        }
        DistributionValidationUtils.reportGeneratorMap(structureViolation, "Following are the maven directory " +
                "structure violating src folders with the details of the violation", reportFile);

        assertFalse("Samples directory contains sample directories with versions ",
                versionArr.size() > 0);
    }

    @Test(groups = "wso2.all", description = "Product specific checks - whether " +
            "samples/resources are properly packaged",
            dependsOnMethods = "testSamplesDirectoryStructureSrcValidation")
    public void testSamplesDirectoryStructureResourcesValidation() throws Exception {

        if (directoryLists.size() == 0) {
            String samplesDirPath = productPath + File.separator + SAMPLES_DIRECTORY;
            DistributionValidationUtils.searchDirectoryByName(samplesDirPath, directoryLists, "/src");
        }

        HashMap<String, String> resourcesStructureViolation = new HashMap<String, String>();
        ArrayList<String> tempArrList = new ArrayList<String>();

        String samplesDirPath = productPath + File.separator + SAMPLES_DIRECTORY;

        if (new File(samplesDirPath).exists()) {

            // violation of samples/src - resources standard
            for (String dirName : directoryLists) {
                DistributionValidationUtils.searchDirectoryByName(dirName, tempArrList, "/resources");
                if (tempArrList.size() == 0) {
                    resourcesStructureViolation.put(dirName, "No resources directory detected.");
                } else {
                    for (String item : tempArrList) {
                        File file[] = new File(item).listFiles();
                        if (file != null) {
                            int length = file.length;
                            if (length == 0) {
                                resourcesStructureViolation.put(dirName, "Resources directory" +
                                        " contains 0 files.");
                            }
                        }
                    }
                }
                tempArrList.clear();
            }
        } else {
            DistributionValidationUtils.reportGeneratorList(versionArr, "No sample directory detected.", reportFile);
        }

        DistributionValidationUtils.reportGeneratorMap(resourcesStructureViolation, "Following are the src " +
                "directories which violates the maven resources directory structure ", reportFile);
    }
}
