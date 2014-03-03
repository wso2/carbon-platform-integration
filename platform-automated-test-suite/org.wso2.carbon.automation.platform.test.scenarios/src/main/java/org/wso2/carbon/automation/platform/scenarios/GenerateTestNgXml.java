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

package org.wso2.carbon.automation.platform.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateTestNgXml {
    private static final Log log = LogFactory.getLog(GenerateTestNgXml.class);

    public void generateXml(String name, String suiteXml) {
        try {

            File file = new File(System.getProperty("automation.settings.location") + File.separator + name + ".xml");
            if (file.createNewFile()) {
                log.info("Suite XML is created");
            } else {
                log.info("Suite XML already exists.");
            }
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(suiteXml);
            out.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
