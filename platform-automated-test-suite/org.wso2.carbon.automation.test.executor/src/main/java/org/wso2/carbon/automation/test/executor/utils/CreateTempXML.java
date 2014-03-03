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

package org.wso2.carbon.automation.test.executor.utils;

import org.testng.collections.Lists;
import org.testng.collections.Maps;
import org.testng.xml.IFileParser;
import org.testng.xml.SuiteXmlParser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CreateTempXML {
    public static final String DEFAULT_FILENAME = "testng.xml";
    private String testFileName;
    private InputStream inputStream;
    private boolean loadTestClasses = true;

    public CreateTempXML(String fileName, InputStream is) {
        init(fileName, is, null);
    }


    private void init(String fileName, InputStream is, IFileParser fp) {
        testFileName = fileName != null ? fileName : DEFAULT_FILENAME;
        inputStream = is;
    }

    public Collection<XmlSuite> parse()
            throws ParserConfigurationException, SAXException, IOException {
        List<String> processedSuites = Lists.newArrayList();
        XmlSuite resultSuite = null;

        File parentFile = null;
        String mainFilePath = null;

        if (testFileName != null) {
            File mainFile = new File(testFileName);
            mainFilePath = mainFile.getCanonicalPath();
            parentFile = mainFile.getParentFile();
        }

        List<String> toBeParsed = Lists.newArrayList();
        List<String> toBeAdded = Lists.newArrayList();
        List<String> toBeRemoved = Lists.newArrayList();
        toBeParsed.add(mainFilePath);
        Map<String, XmlSuite> childToParentMap = Maps.newHashMap();
        while (toBeParsed.size() > 0) {

            for (String currentFile : toBeParsed) {
                InputStream inputStream = this.inputStream != null
                                          ? this.inputStream
                                          : new FileInputStream(currentFile);

                IFileParser<XmlSuite> fileParser = new SuiteXmlParser();
                XmlSuite result = fileParser.parse(currentFile, inputStream, loadTestClasses);
                XmlSuite currentXmlSuite = result;
                processedSuites.add(currentFile);
                toBeRemoved.add(currentFile);

                if (childToParentMap.containsKey(currentFile)) {
                    XmlSuite parentSuite = childToParentMap.get(currentFile);
                    //Set parent
                    currentXmlSuite.setParentSuite(parentSuite);
                    //append children
                    parentSuite.getChildSuites().add(currentXmlSuite);
                }

                if (null == resultSuite) {
                    resultSuite = currentXmlSuite;
                }

                List<String> suiteFiles = currentXmlSuite.getSuiteFiles();
                if (suiteFiles.size() > 0) {
                    for (String path : suiteFiles) {
                        String canonicalPath;
                        if (parentFile != null && new File(parentFile, path).exists()) {
                            canonicalPath = new File(parentFile, path).getCanonicalPath();
                        } else {
                            canonicalPath = new File(path).getCanonicalPath();
                        }
                        if (!processedSuites.contains(canonicalPath)) {
                            toBeAdded.add(canonicalPath);
                            childToParentMap.put(canonicalPath, currentXmlSuite);
                        }
                    }
                }
            }

            //
            // Add and remove files from toBeParsed before we loop
            //
            for (String s : toBeRemoved) {
                toBeParsed.remove(s);
            }
            toBeRemoved = Lists.newArrayList();

            for (String s : toBeAdded) {
                toBeParsed.add(s);
            }
            toBeAdded = Lists.newArrayList();

        }

        List<XmlSuite> resultList = Lists.newArrayList();
        resultList.add(resultSuite);
        return resultList;
    }
}