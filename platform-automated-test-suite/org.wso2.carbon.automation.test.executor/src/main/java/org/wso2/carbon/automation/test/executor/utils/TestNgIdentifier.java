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


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class TestNgIdentifier {
    public static final String TESTNG_DTD = "testng-1.0.dtd";

    /**
     * The URL to the deprecated TestNG DTD.
     */
    public static final String DEPRECATED_TESTNG_DTD_URL = "http://beust.com/testng/" + TESTNG_DTD;

    /**
     * The URL to the TestNG DTD.
     */
    public static final String TESTNG_DTD_URL = "http://testng.org/" + TESTNG_DTD;

    protected boolean getTestNgXml(InputStream inputStream) {
        boolean istestng = false;
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String xmlString = writer.toString();
        String[] tags = xmlString.split("<");
        for (String tag : tags) {
            if (tag.contains("!DOCTYPE suite")) {
               // String doctype = xmlString.substring(tag.indexOf("<!DOCTYPE"), xmlString.indexOf(">"));
                if (tag.contains(TESTNG_DTD_URL) || tag.contains(DEPRECATED_TESTNG_DTD_URL)) {
                    istestng = true;
                    break;
                }
            }
        }
        return istestng;
    }
}
