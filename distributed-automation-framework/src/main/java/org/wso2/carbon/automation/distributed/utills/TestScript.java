package org.wso2.carbon.automation.distributed.utills;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestScript {

    private String pattern;
    private String databaseName;
    private String databaseConnector;
    private String testResourceLocation;
    private String distributionName;
    private String puppetModuleLocation;

    public static void main(String[] args) throws IOException {

        // 1. bash
        // 2. Pattern
        // 3. DB
        // 4. JDK

        String[] command = {"/bin/bash", "/home/dimuthu/Desktop/Learning/distributedsetup.sh", "default" ,"MySql"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        System.out.println("Output of running " + command + " is: ");
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

     /*   InputStream is = p.getInputStream();
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
        System.out.println(sb);*/

    }

}