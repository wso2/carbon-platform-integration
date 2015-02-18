/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.automation.artifacts.JMXclientUtils;

public class Deadlock {

    public static Object Lock1 = new Object();
    public static Object Lock2 = new Object();

    public static void main(String[] args) {

        ThreadDemo1 T1 = new ThreadDemo1("ThreadNumber-1");
        ThreadDemo2 T2 = new ThreadDemo2("ThreadNumber-2");
        T1.start();
        T2.start();

    }

    private static class ThreadDemo1 extends Thread {
        ThreadDemo1(String name){
            super(name);
        }
        public void run() {
            synchronized (Lock1) {
                //System.out.println("Thread 1: Holding lock 1...");
                try { Thread.sleep(10); }
                catch (InterruptedException e) {}
                //System.out.println("Thread 1: Waiting for lock 2...");
                synchronized (Lock2) {
                    //System.out.println("Thread 1: Holding lock 1 & 2...");
                }
            }
        }
    }


    private static class ThreadDemo2 extends Thread {
        ThreadDemo2(String name){
            super(name);
        }
        public void run() {
            synchronized (Lock2) {
                System.out.println("Thread 2: Holding lock 2...");
                try { Thread.sleep(10); }
                catch (InterruptedException e) {}
                System.out.println("Thread 2: Waiting for lock 1...");
                synchronized (Lock1) {
                    System.out.println("Thread 2: Holding lock 1 & 2...");
                }
            }
        }
    }

}







