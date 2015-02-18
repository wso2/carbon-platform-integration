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

package org.wso2.carbon.automation.test.utils.JMXclient.MemoryInfo;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.hat.internal.model.JavaObject;
import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.oql.OQLEngine;
import com.sun.tools.hat.internal.oql.OQLException;
import com.sun.tools.hat.internal.parser.HprofReader;
import com.sun.tools.hat.internal.parser.PositionDataInputStream;
import org.wso2.carbon.automation.test.utils.JMXclient.QueryVisitor;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 *
 * provide methods to generate memory dump of a remote VM
 *
 */
public class MemoryInformationProvider {
    private Snapshot memorySnapshot;

    /**
     * generate memory dump and write it int a file
     *
     * @param mBeanServerConnection connection with MBean server
     * @param registeredMBean       registered MBean Objectname of HotSpotDiagnosticMXBean
     * @param path                  location that file should be generated.
     * @throws com.sun.tools.attach.AgentInitializationException
     * @throws com.sun.tools.attach.AgentLoadException
     * @throws com.sun.tools.attach.AttachNotSupportedException
     * @throws java.io.IOException
     * @throws javax.management.MalformedObjectNameException
     */
    public void createMemoryDumpFile(MBeanServerConnection mBeanServerConnection,
                                     ObjectName registeredMBean, String path)
            throws AgentInitializationException, AgentLoadException, AttachNotSupportedException,
                   IOException, MalformedObjectNameException {
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
                                                                                                   registeredMBean.toString(),
                                                                                                   HotSpotDiagnosticMXBean.class);
        hotSpotDiagnosticMXBean.dumpHeap(path, true);
    }

    /**
     * parse memory dump file.
     *
     * @param path       path of memory dump file has located
     * @param debugLevel debug level
     * @throws java.io.IOException
     */
    public void parseMemoryDumpFile(String path, int debugLevel) throws IOException {
        PositionDataInputStream positiondatainputstream = new PositionDataInputStream(new BufferedInputStream
                                                                                              (new FileInputStream(path)));
        HprofReader hprofReader = new HprofReader(path, positiondatainputstream, 1, true, debugLevel);
        memorySnapshot = hprofReader.read();
        positiondatainputstream.close();
        memorySnapshot.resolve(true);
    }

    /**
     * get OQL query as an argument and retrieve the results from memory dump file.
     *
     * @param query OQL query in as a string
     * @return JavaObject Object
     * @throws OQLException
     */
    public JavaObject executeQuery(String query) throws OQLException {
        OQLEngine engine = new OQLEngine(memorySnapshot);
        QueryVisitor queryVisitor = new QueryVisitor(engine);
        engine.executeQuery(query, queryVisitor);

        return (JavaObject) queryVisitor.getResults();
    }

}
