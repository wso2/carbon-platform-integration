/*
 *Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.automation.extensions.servers.sftpserver;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class starts a SFTP Server instance which provides a secured backend for file transfer operations
 */
public class SFTPServer extends Thread {

    private Log log = LogFactory.getLog(SFTPServer.class);

    private static final String USERNAME = "SFTPUser";

    private static final char[] PASSWORD = {'S', 'F', 'T', 'P', '3', '2', '1'};

    private static SshServer sshServer = SshServer.setUpDefaultServer();

    private static int port = 8005;

    private static boolean isKeepAlive = false;

    public static final String DEFAULT_TEST_HOST_KEY_PROVIDER_ALGORITHM = KeyUtils.RSA_ALGORITHM;

    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    public static void setKeepAlive(boolean keepAlive) {
        isKeepAlive = keepAlive;
    }

    public void startServer() {
        Thread thread = new Thread(new SFTPServer());
        thread.start();
    }

    public void run() {

        sshServer.setPort(port);
        sshServer.setSubsystemFactories(
                Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory()));
        sshServer.setCommandFactory(new ScpCommandFactory());
        sshServer.setKeyPairProvider(createTestHostKeyProvider(Paths.get("hostkey.ser")));
        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(final String username, final String password,
                                        final ServerSession session) {
                return StringUtils.equals(username, USERNAME) && StringUtils.equals
                        (password, java.nio.CharBuffer.wrap(PASSWORD).toString());
            }
        });

        sshServer.setSubsystemFactories(
                Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory()));

        try {
            sshServer.start();
        } catch (IOException e) {
            log.error("Exception occurred " + e);
        }
        log.info("SFTP Server Stared successfully on port " + port);

        while (isKeepAlive) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("Exception occurred " + e);
            }
        }
    }

    public static KeyPairProvider createTestHostKeyProvider(Path path) {
        SimpleGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider();
        keyProvider.setPath(ValidateUtils.checkNotNull(path, "No path"));
        keyProvider.setAlgorithm(DEFAULT_TEST_HOST_KEY_PROVIDER_ALGORITHM);
        return keyProvider;
    }

    public void stopServer() throws InterruptedException {
        try {
            sshServer.stop();
            log.info("SFTP Server Run On Port " + port + " Stopped successfully ..... ");
        } catch (IOException e) {
            log.error("Unable to stop the SFTP server", e);;
        }
    }
}
