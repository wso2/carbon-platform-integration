package org.wso2.carbon.automation.distributed.utills;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by dimuthu on 6/7/16.
 */
public class Learn {


    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(new String[]{"/bin/sh", "/home/dimuthu/Desktop/Learning/he.sh" ,"some_program" ,"word1" ,"word2" ,"word3"});

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
