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
package org.wso2.carbon.automation.test.utils.tests.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains set of util methods which can be used with DistributionValidationTest class.
 */
public class DistributionValidationUtils {
    private static final Log log = LogFactory.getLog(DistributionValidationUtils.class);
    private static final String TEMP_DIRECTORY = "temp";

    public static void readLicenseFile(String productPath, HashSet<String> licenceJarList,
                                       HashSet<String> duplicateJarList) {
        //identifying the licence file
        File[] licenseFile = new File(productPath).listFiles
                (new FilenameFilter() {
                    public boolean accept(File dir, String filename) {
                        return filename.equals("LICENSE.txt");
                    }
                });
        BufferedReader br = null;
        String readLine;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(licenseFile[0]), "UTF-8"));
            while ((readLine = br.readLine()) != null) {
                if (readLine.contains(".jar")) {
                    String jarName = readLine.split(".jar ")[0] + ".jar";
                    if (!licenceJarList.contains(jarName)) {
                        licenceJarList.add(jarName); // LICENSE.txt - jar list
                    } else {
                        // if LICENSE.txt contains duplicate jars
                        duplicateJarList.add(jarName);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Exception - read License while reading data " + e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                log.error("Exception - read License while closing the stream " + e);
            }
        }
    }

    public static void validateXml(HashMap<String, Exception> xsdValidateMap,
                                   String distributionXml, String xsdFile)
            throws Exception {
        Source schemaFile = new StreamSource(new File(xsdFile));
        Source xmlFile = new StreamSource(new File(distributionXml));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try {
            schema = schemaFactory.newSchema(schemaFile);
        } catch (SAXException e) {
            log.error(distributionXml, e);
            xsdValidateMap.put(distributionXml, e);
        }
        if (schema != null) {
            Validator validator = schema.newValidator();
            try {
                validator.validate(xmlFile);
            } catch (SAXException e) {
                log.error(distributionXml, e);
                xsdValidateMap.put(distributionXml, e);
            }
        }
    }

    public static List<String> getLinks(String url) throws ParserException {
        Parser htmlParser = new Parser(url);
        List<String> links = new LinkedList<String>();
        NodeList tagNodeList = htmlParser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
        for (int m = 0; m < tagNodeList.size(); m++) {
            LinkTag loopLinks = (LinkTag) tagNodeList.elementAt(m);
            String linkName = loopLinks.getLink();
            links.add(linkName);
        }
        return links;
    }

    @SuppressWarnings("unchecked")
    public static List<File> recursiveScanToRetrieveTextualFiles(String path, String[] extensionList) {
        File dir = new File(path);
        return (List<File>) FileUtils.listFiles(dir, extensionList, true);
    }

    public static <T> Set getDuplicateJarSet(Collection<T> list) {
        final Set<T> sameDirDuplicatedJars = new HashSet<T>();
        Set<T> set = new HashSet<T>() {
            @Override
            public boolean add(T item) {
                if (contains(item)) {
                    sameDirDuplicatedJars.add(item);
                }
                return super.add(item);
            }
        };
        for (T element : list) {
            set.add(element);
        }
        log.info(set.size());
        return sameDirDuplicatedJars;
    }

    public static String[] getDirectoryNames(String path) {
        File fileName = new File(path);
        String[] directoryNamesArr = fileName.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        log.info("Directories inside " + path + " are " + Arrays.toString(directoryNamesArr));
        return directoryNamesArr;
    }

    public static void identifyDuplicateJars(List<File> jarFileListInDistribution, File distributionVersion,
                                             HashSet<String> distributionDuplicateJarList,
                                             ArrayList<String> unidentifiedVersionJars) {
        Iterator<File> itJarList = jarFileListInDistribution.iterator();
        ArrayList<String> tempArr = new ArrayList<String>();
        ArrayList<File> pathListForAddedJarToJarVersions = new ArrayList<File>();
        HashMap<String, String> jarVersions = new HashMap<String, String>();
        StringBuilder builder = new StringBuilder();
        Pattern numeric = Pattern.compile("[^0-9_.-]");
        Pattern nonNumeric = Pattern.compile("[a-zA-Z]");
        while (itJarList.hasNext()) {
            File jarFilePath = itJarList.next();
            String jarName = (jarFilePath).getName();
            if (!jarFilePath.getAbsolutePath().contains(distributionVersion.getName().
                    replaceAll(".zip", "") + File.separator + TEMP_DIRECTORY + File.separator)) {
                for (int letter = jarName.length() - 1; letter >= 0; letter--) {
                    char singleChar = jarName.charAt(letter);
                    Matcher matcher = numeric.matcher(Character.toString(singleChar));
                    // Find all matches
                    if (!matcher.find()) {
                        // Get the matching string
                        builder.append(singleChar);
                    } else if (nonNumeric.matcher(Character.toString(singleChar)).find()) {
                        if (builder.length() > 1) {
                            tempArr.add(builder.toString());
                            builder.setLength(0);
                        } else {
                            builder.setLength(0);
                        }
                    }
                }
                int max;
                int previousMax = 0;
                String[] version = new String[1];
                for (String element : tempArr) {
                    max = element.length();
                    if (max > previousMax) {
                        previousMax = max;
                        version[0] = element;
                    }
                }
                tempArr.clear();
                if (version[0] != null) {
                    String jar = jarName.split((StringUtils.reverse(version[0])))[0];
                    if (jar.length() >= 2) {
                        if (jarVersions.containsKey(jar)) {
                            if (!jarVersions.get(jar).equals(jarName.split(jar)[1])) {
                                // removing patches - plugins duplication
                                if (distributionDuplicateJarList.toString().contains(jarName)) {
                                    for (String itemDistributionDuplicateJarList : distributionDuplicateJarList) {
                                        if (itemDistributionDuplicateJarList.contains(jarName)
                                                && (itemDistributionDuplicateJarList.contains("patches")
                                                || itemDistributionDuplicateJarList.contains("plugins"))) {
                                            if (!(jarFilePath.getAbsolutePath().contains("patches")
                                                    || jarFilePath.getAbsolutePath().contains("plugins"))) {
                                                distributionDuplicateJarList.add(jarFilePath.getAbsolutePath());
                                            }
                                        }
                                    }
                                } else {
                                    distributionDuplicateJarList.add(jarFilePath.getAbsolutePath());
                                }
                                for (File pathListForAddedJarToJarVersion :
                                        pathListForAddedJarToJarVersions) {
                                    String path = pathListForAddedJarToJarVersion.toString();
                                    if (path.contains(jar + jarVersions.get(jar))) {
                                        distributionDuplicateJarList.add(path);
                                        break;
                                    }
                                }
                            }
                        } else {
                            jarVersions.put(jar, jarName.split(jar)[1]);
                            pathListForAddedJarToJarVersions.add(jarFilePath);
                        }
                    } else {
                        log.info("Unable to identify the version " + jar);
                        unidentifiedVersionJars.add(jarFilePath.getAbsolutePath());
                    }
                } else {
                    jarVersions.put(jarName, null);
                    pathListForAddedJarToJarVersions.add(jarFilePath);
                }
            }
        }
    }

    public static ArrayList<String> validateSamplesDirectoryStructureIdentifyVersions(String samplesDirPath,
                                                                                      ArrayList<String> versionArr) {
        String samplesDir[] = getDirectoryNames(samplesDirPath);
        StringBuilder builder = new StringBuilder();
        ArrayList<String> tempArr = new ArrayList<String>();
        Pattern numeric = Pattern.compile("[0-9_.-]");
        for (String dir : samplesDir) {
            for (int item = dir.length() - 1; item >= 0; item--) {
                char singleChar = dir.charAt(item);
                Matcher matcher = numeric.matcher(Character.toString(singleChar));
                // Find all matches
                if (matcher.find()) {
                    // Get the matching string
                    builder.append(singleChar);
                } else {
                    if (builder.length() > 1) {
                        tempArr.add(builder.toString());
                        builder.setLength(0);
                    } else {
                        builder.setLength(0);
                    }
                }
                if (item == 0 && builder.length() != 0) {
                    tempArr.add(builder.toString());
                    builder.setLength(0);
                }
            }
            int max;
            int previousMax = 0;
            String[] version = new String[1];
            for (String element : tempArr) {
                max = element.length();
                if (max > previousMax) {
                    previousMax = max;
                    version[0] = element;
                }
            }
            if (version[0] != null) {
                if (version[0].length() >= 2) {
                    versionArr.add(dir);
                }
            }
            tempArr.clear();
        }
        return versionArr;
    }

    public static void searchDirectoryByName(String baseDir, ArrayList<String> directoryLists, String dirName) {
        File sampleName = new File(baseDir);
        File[] fileArray = sampleName.listFiles();
        if (fileArray != null) {
            for (int i = 0; i < fileArray.length; i++) {
                File name = fileArray[i];
                if (name.isDirectory()) {
                    if (name.toString().subSequence(name.toString().lastIndexOf("/"),
                            name.toString().length()).equals(dirName)) {
                        directoryLists.add(name.getAbsolutePath());
                    }
                    searchDirectoryByName(fileArray[i].getAbsolutePath(), directoryLists, dirName);
                } else if (fileArray.length == i) {
                    return;
                }
            }
        }
    }

    public static void reportGeneratorList(List<String> list, String topic, File reportFile)
            throws FileNotFoundException, UnsupportedEncodingException {
        boolean fileCreation = false;
        if (!reportFile.exists()) {
            try {
                fileCreation = reportFile.createNewFile();
            } catch (IOException e) {
                log.error("Report file creation failure", e);
            }
            log.info("Report file creation status " + fileCreation);
        }
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(reportFile, true), "UTF-8");
        BufferedWriter outStream = new BufferedWriter(writer);
        try {
            outStream.write(topic);
            outStream.write("\n");
            outStream.write("----------------------------------------------------------");
            outStream.write("\n");
            if (list.size() == 0) {
                outStream.write("None" + "\n");
            } else {
                Collections.sort(list);
                for (Object item : list) {
                    outStream.write(item.toString() + "\n");
                }
            }
            outStream.write("----------------------------------------------------------");
            outStream.write("\n");
            outStream.write("\n");
        } catch (IOException e) {
            log.error("Report Generator List - error while writing to file" + e);
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                log.error("Report Generator List - error while closing the stream" + e);
            }
        }
    }

    public static void reportGeneratorMap(HashMap map, String topic, File reportFile)
            throws FileNotFoundException, UnsupportedEncodingException {
        boolean fileCreation = false;
        if (!reportFile.exists()) {
            try {
                fileCreation = reportFile.createNewFile();
            } catch (IOException e) {
                log.error("Report file creation failure", e);
            }
            log.info("Report file creation status " + fileCreation);
        }
        OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(reportFile, true), "UTF-8");
        BufferedWriter outStream = new BufferedWriter(writer);
        try {
            outStream.write(topic);
            outStream.write("\n");
            outStream.write("----------------------------------------------------------");
            outStream.write("\n");
            if (map.size() == 0) {
                outStream.write("None" + "\n");
            } else {
                Set set = map.entrySet();
                for (Object item : set) {
                    Map.Entry me = (Map.Entry) item;
                    outStream.write(me.getKey().toString());
                    outStream.write("\n");
                    outStream.write(" - " + me.getValue());
                    outStream.write("\n");
                }
            }
            outStream.write("----------------------------------------------------------");
            outStream.write("\n");
            outStream.write("\n");
        } catch (IOException e) {
            log.error("Report Generator Map - error while writing to file" + e);
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                log.error("Report Generator Map - error while closing the stream" + e);
            }
        }
    }
}
