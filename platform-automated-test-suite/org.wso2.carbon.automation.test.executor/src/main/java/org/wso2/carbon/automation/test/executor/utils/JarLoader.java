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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JarLoader {

    private static final Class[] parameters = new Class[]{URL.class};

    public void addFile(File aFile) throws IOException {
        addURL(aFile.toURL());
    }

    public void addURL(URL aURL) throws IOException {
        URLClassLoader sysloader = (URLClassLoader) JarLoader.class.getClassLoader();
        Class sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{aURL});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + aURL + " to system classloader");
        }
    }

    public List<File> getJarList() {
        List<File> jarsList = new ArrayList<File>();
        boolean jarAvailable = false;
        File file = new File("src" + File.separator + "main" + File.separator + "resources");
        String[] extensions = {"jar"};
        boolean recursive = true;
        Collection files = org.apache.commons.io.FileUtils.listFiles(file, extensions, recursive);
        String[] products = System.getProperty("server.list").split(",");
        for (String product : products) {
            for (Object objFile : files) {
                if (((File) objFile).getName().contains(product.toLowerCase() + ".test-")
                    ||((File) objFile).getName().contains("platform.test.scenarios")) {
                    jarsList.add(((File) objFile).getAbsoluteFile());
                }
            }
        }

        return jarsList;
    }
}
