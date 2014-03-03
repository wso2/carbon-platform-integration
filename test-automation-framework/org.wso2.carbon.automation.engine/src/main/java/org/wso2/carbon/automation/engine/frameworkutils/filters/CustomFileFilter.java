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
package org.wso2.carbon.automation.engine.frameworkutils.filters;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomFileFilter {
    public static List<File> getFilesRecursive(final File basedir, final FileFilter filter) {
        List<File> files = new ArrayList<File>();
        if (basedir != null && basedir.isDirectory()) {
            for (File subDir : basedir.listFiles(TypeFilter.DIR)) {
                files.addAll(CustomFileFilter.getFilesRecursive(subDir, filter));
            }
            files.addAll(Arrays.asList(basedir.listFiles(filter)));
        }
        return files;
    }
}
