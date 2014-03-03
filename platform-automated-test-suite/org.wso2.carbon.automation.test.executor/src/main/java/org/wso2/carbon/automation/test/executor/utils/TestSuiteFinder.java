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

import org.testng.internal.Utils;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestSuiteFinder {

    public List<XmlSuite> initializeSuitesAndJarFile(String jarPath) {
        List<XmlSuite> suiteList = new ArrayList<XmlSuite>();
        File jarFile = new File(jarPath);
        try {
            URL jarfileUrl = jarFile.getCanonicalFile().toURI().toURL();
            URLClassLoader jarLoader = new URLClassLoader(new URL[]{jarfileUrl});
            Thread.currentThread().setContextClassLoader(jarLoader);

            JarFile jf = new JarFile(jarFile);
            System.out.println("   result: " + jf);
            Enumeration<JarEntry> entries = jf.entries();
            int count = 0;
            boolean foundTestngXml = false;
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.getName().contains(".xml")) {
                    TestNgIdentifier identifier = new TestNgIdentifier();
                    if (identifier.getTestNgXml(jf.getInputStream(je))) {
                        count++;
                        CreateTempXML tempXML = new CreateTempXML("test-" + count + ".xml", jf.getInputStream(je));
                        System.out.println("-------------" + je.getName() + "----------------");
                        suiteList.addAll(tempXML.parse());
                        foundTestngXml = true;
                    }
                }
            }
            if (!foundTestngXml) {
                Utils.log("No valid testng XML file found or invalid jar");
            }
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return suiteList;
    }

    private Parser getParser(InputStream is) {
        Parser result = new Parser(is);
        initProcessor(result);
        return result;
    }

    private void initProcessor(Parser result) {
        //  result.setPostProcessor(new OverrideProcessor(m_includedGroups, m_excludedGroups));
    }
}
