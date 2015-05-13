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
package org.wso2.carbon.automation.extensions.servers.tomcatserver;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.wso2.carbon.automation.extensions.ExtensionUtils;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

public class TomcatServerManager {
    private final static Log log = LogFactory.getLog(TomcatServerManager.class);
    Tomcat tomcat;
    boolean isRunning = false;
    int tomcatPort;
    String tomcatClass = null;
    String basedir = null;
    String serverType;
    String webappDirLocation;
    private Thread tomcatThread = null;

    public TomcatServerManager(String className, String server, int port) {
        this.tomcatPort = port;
        this.tomcatClass = className;
        this.serverType = server;
        this.basedir = ExtensionUtils.getReportLocation();
    }

    public TomcatServerManager(String className, String server, int port, String webAppDir) {
        this.tomcatPort = port;
        this.tomcatClass = className;
        this.serverType = server;
        this.webappDirLocation = webAppDir;
    }

    public void startJaxRsServer() throws LifecycleException, IOException {
        final File base = createBaseDirectory(basedir);
        log.info("Using base folder: " + base.getAbsolutePath());
        tomcat = new Tomcat();
        tomcat.setPort(tomcatPort);
        tomcat.setBaseDir(base.getAbsolutePath());
        Context context = tomcat.addContext("/", base.getAbsolutePath());
        Tomcat.addServlet(context, "CXFServlet", new CXFServlet());
        context.addServletMapping("/rest/*", "CXFServlet");
        context.addApplicationListener(ContextLoaderListener.class.getName());
        context.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));
        context.addParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        context.addParameter("contextConfigLocation", tomcatClass);
        tomcat.start();
        tomcat.getServer().await();
    }

    public void startWebAppServer() throws ServletException, LifecycleException {
        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = String.valueOf(tomcatPort);
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        tomcat.setPort(Integer.parseInt(webPort));
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("configuring app with basedir: " +
                           new File("./" + webappDirLocation).getAbsolutePath());
        tomcat.start();
        tomcat.getServer().await();
    }

    public synchronized void startServer() {
        if (tomcatThread == null) {
            tomcatThread = new Thread() {
                public void run() {
                    try {
                        if (serverType.equals(TomcatServerType.jaxrs.name())) {
                            startJaxRsServer();
                        } else if (serverType.equals(TomcatServerType.webapp.name())) {
                            startWebAppServer();
                        }
                    } catch (IOException e) {
                        handleException(e);
                    } catch (LifecycleException e) {
                        handleException(e);
                    } catch (ServletException e) {
                        handleException(e);
                    }
                }
            };
            tomcatThread.start();
            isRunning = true;
        }
    }

    private void handleException(Exception e) {
        String msg = "Tomcat server startup failed";
        log.error("Tomcat server startup failed ", e);
        throw new IllegalStateException(msg, e);
    }

    private File createBaseDirectory(String basedirLocal) throws IOException {
        final File base = File.createTempFile("jaxrs-tmp-", "", new File(basedirLocal));
        if (!base.delete()) {
            throw new IOException("Cannot (re)create base folder: " + base.getAbsolutePath());
        }
        if (!base.mkdir()) {
            throw new IOException("Cannot create base folder: " + base.getAbsolutePath());
        }
        return base;
    }

    public void stop() throws LifecycleException {
        if (!isRunning) {
            log.info("Tomcat server is running at the port " + tomcatPort);
            return;
        }
        tomcat.stop();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void run() {
        try {
            if (serverType.equals(TomcatServerType.jaxrs.name())) {
                startJaxRsServer();
            } else if (serverType.equals(TomcatServerType.webapp.name())) {
                startWebAppServer();
            }
        } catch (Exception e) {
            log.error("Server startup failed :" + e.getMessage());
        }
    }
}