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

package org.wso2.carbon.automation.extensions.distributed.util;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * This class can be used for git related operations
 */
public class GitOperations {

    /**
     * This methods clones specified git repo
     *
     * @param gitURL  - URL of the git repository
     * @param dirPath - Destination directory location
     * @throws java.io.IOException
     * @throws GitAPIException
     */
    public void gitRepoClone(String gitURL, String dirPath) throws IOException, GitAPIException {
        CloneCommand cloneCommand = Git.cloneRepository();
        File dirLocation = new File(dirPath);

        if (dirLocation.exists()) {
            FileUtils.forceDelete(dirLocation);
        }

        cloneCommand.setURI(gitURL);
        cloneCommand.setDirectory(dirLocation);

        Git local = cloneCommand.call();

        assertEquals(local.getRepository().getDirectory(), new File(dirLocation, ".git"));
        assertEquals(local.getRepository().getWorkTree(), dirLocation);
    }

}
