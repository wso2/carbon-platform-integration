/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.automation.distributed.utills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

/**
 * GitRepositoryUtil.
 */
public class GitRepositoryUtil {
    private static final Log log = LogFactory.getLog(GitRepositoryUtil.class);

    public static boolean gitCloneRepository(String repositoryUrl, String localDirectory) throws GitAPIException {
        Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(new File(localDirectory))
                .setCloneAllBranches(true)
                .call();
        return true;
    }
}

