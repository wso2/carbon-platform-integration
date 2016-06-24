/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.distributed.utills;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.distributed.beans.EnvironmentInfoBeans;
import org.wso2.carbon.automation.distributed.beans.RepositoryInfoBeans;
import org.wso2.carbon.automation.distributed.commons.GenericYamlParser;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;

/**
 * This class serves as the automation script runner
 */
public class ScriptExecutorUtil {

    private static final Log log = LogFactory.getLog(ScriptExecutorUtil.class);

    private static EnvironmentInfoBeans instanceBeans = new EnvironmentInfoBeans();

    public void scriptExecution() throws IOException, InterruptedException {

        //TODO - Calling for assigning values only ....
        tempFunction();

        //TODO - selecting base image - This will not work for default deployment ....
        String distributedSetupScriptLocation = FrameworkPathUtil.getSystemResourceLocation()
                + "artifacts" + File.separator + "AM" + File.separator + "scripts"
                + File.separator + "bashscripts";

        String[] command = new String[0];

        if (instanceBeans.getPatternName().equals("default")) {

            command = new String[]{"/bin/bash", distributedSetupScriptLocation
                    + File.separator + "distributedsetup.sh", RepositoryInfoBeans.getDockerRegistryLocation(),
                    instanceBeans.getPatternName(), instanceBeans.getDatabaseName(), instanceBeans.getJdkVersion(),
                    FrameworkPathUtil.getSystemResourceLocation()};
        } else {
            String connectorJarName;
            command = new String[]{"/bin/bash", distributedSetupScriptLocation
                    + File.separator + "distributedsetup.sh",  RepositoryInfoBeans.getDockerRegistryLocation(),
                    instanceBeans.getPatternName(),instanceBeans.getDatabaseName(), instanceBeans.getJdkVersion(),
                    FrameworkPathUtil.getSystemResourceLocation()};
        }

        processOutputGenerator(command);

    }

    private void processOutputGenerator(String[] command) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        log.info(" Listing Docker Run Output .... " + Arrays.toString(command) + " is: ");

        while ((line = br.readLine()) != null) {
            log.info(line);

        }
    }

    private static void tempFunction() {
        // setting some values
        instanceBeans.setOsVersion("ubuntu:latest");
        instanceBeans.setJdkVersion("1.7.0_80");
        instanceBeans.setDatabaseName("MySql");
        instanceBeans.setPatternName("default");
    }



    public static void main(String[] args) throws IOException {

      /*  String param01 = "45";

        Process p1=Runtime.getRuntime().exec("/home/dimuthu/Desktop/Test.sh" +" "+param01);

        BufferedReader br = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }*/


    }


 /*   public static void main(String[] args) throws IOException {
     *//*   ProcessBuilder pb = new ProcessBuilder("/home/dimuthu/Desktop/MySample.sh", "pattern02");
        Process p = pb.start();*//*


     *//*   String[] cmd = { "bash", "-c", "/home/dimuthu/Desktop/MySample.sh pattern02" };
        Process p = Runtime.getRuntime().exec(cmd);*//*

       *//* CommandLine cmd = new CommandLine("/home/dimuthu/Desktop/MySample.sh");
        cmd.addArgument("pattern02");


        Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(FileUtils.getUserDirectory());
        exec.execute(cmd);*//*


*//*
        Process p = new ProcessBuilder("/home/dimuthu/Desktop/MySample.sh", "pattern02").start();

        InputStream is = p.getInputStream();
        StringBuilder sb = new StringBuilder();

        int i=0;
        try {
            while ((i=is.read())!=-1){
                sb.append((char)i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(sb);*//*


     *//*   ProcessBuilder pb2=new ProcessBuilder("/home/dimuthu/Desktop/MySample.sh");
        pb2.environment().put("param1", "default");
        Process script_exec = pb2.start();
*//*




    }*/

}
