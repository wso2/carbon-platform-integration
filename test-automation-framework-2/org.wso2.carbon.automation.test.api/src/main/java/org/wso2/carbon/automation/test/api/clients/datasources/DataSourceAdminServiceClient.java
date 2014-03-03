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
package org.wso2.carbon.automation.test.api.clients.datasources;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.commons.datasource.DataSourceInformation;
import org.apache.synapse.commons.datasource.factory.DataSourceInformationFactory;
import org.apache.synapse.commons.datasource.serializer.DataSourceInformationSerializer;
import org.wso2.carbon.automation.test.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.datasource.ui.stub.DataSourceAdminStub;
import org.wso2.carbon.datasource.ui.stub.DataSourceManagementException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Properties;

public class DataSourceAdminServiceClient {

    private static final Log log = LogFactory.getLog(DataSourceAdminServiceClient.class);

    private final String serviceName = "DataSourceAdmin";
    private DataSourceAdminStub dataSourceAdminStub;

    public DataSourceAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        dataSourceAdminStub = new DataSourceAdminStub(endPoint);
        AuthenticateStub.authenticateStub(sessionCookie, dataSourceAdminStub);
    }

    public DataSourceAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        String endPoint = backEndUrl + serviceName;
        dataSourceAdminStub = new DataSourceAdminStub(endPoint);
        AuthenticateStub.authenticateStub(userName, password, dataSourceAdminStub);
    }

    public void addDataSourceInformation(String dataSourceName,
                                         DataSourceInformation dataSourceInfo)
            throws DataSourceManagementException, RemoteException {

        OMElement dataSourceInfoElement;

        Properties properties = DataSourceInformationSerializer.serialize(dataSourceInfo);
        if (properties.isEmpty()) {
            throw new RuntimeException("DataSource Information Not Found. properties Empty");
        }
        dataSourceInfoElement = createOMElement(properties);
        dataSourceAdminStub.addDataSourceInformation(dataSourceName, dataSourceInfoElement);
    }

    public void editCarbonDataSources(String name,
                                      DataSourceInformation dataSourceInformation)
            throws DataSourceManagementException, RemoteException {

        Properties properties = DataSourceInformationSerializer.serialize(dataSourceInformation);
        if (properties.isEmpty()) {
            throw new RuntimeException("DataSource Information Not Found. properties Empty");
        }
        OMElement dataSourceElement = createOMElement(properties);
        dataSourceAdminStub.editDataSourceInformation(name, dataSourceElement);


    }

    public DataSourceInformation getCarbonDataSources(String name)
            throws DataSourceManagementException, RemoteException {
        OMElement dataSource;
        DataSourceInformation dataSourceInformation;

        dataSource = dataSourceAdminStub.getDataSourceInformation(name);

        dataSourceInformation = validateAndCreate(name, dataSource.getFirstElement());

        return dataSourceInformation;
    }

    public void removeCarbonDataSources(String name)
            throws DataSourceManagementException, RemoteException {

        dataSourceAdminStub.removeDataSourceInformation(name);

    }

    private static DataSourceInformation validateAndCreate(String name, OMElement element) {

        Properties properties = loadProperties(element);
        if (properties.isEmpty()) {
            throw new RuntimeException("DataSource Information Not Found. properties Empty");
        }

        DataSourceInformation information = DataSourceInformationFactory.createDataSourceInformation(name, properties);

        return information;
    }

    private static Properties loadProperties(OMElement element) {
        if (log.isDebugEnabled()) {
            log.debug("Loading properties from : " + element);
        }
        String xml = "<!DOCTYPE properties   [\n" +
                     "\n" +
                     "<!ELEMENT properties ( comment?, entry* ) >\n" +
                     "\n" +
                     "<!ATTLIST properties version CDATA #FIXED \"1.0\">\n" +
                     "\n" +
                     "<!ELEMENT comment (#PCDATA) >\n" +
                     "\n" +
                     "<!ELEMENT entry (#PCDATA) >\n" +
                     "\n" +
                     "<!ATTLIST entry key CDATA #REQUIRED>\n" +
                     "]>" + element.toString();
        final Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(xml.getBytes());
            properties.loadFromXML(in);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("IOException while reading DataSource information" + e, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException inored) {
                }
            }

        }
    }

    private static OMElement createOMElement(Properties properties) {

        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            properties.storeToXML(baos, "");
            String propertyS = new String(baos.toByteArray());
            String correctedS = propertyS.substring(propertyS.indexOf("<properties>"),
                                                    propertyS.length());
            String inLined = "<!DOCTYPE properties   [\n" +
                             "\n" +
                             "<!ELEMENT properties ( comment?, entry* ) >\n" +
                             "\n" +
                             "<!ATTLIST properties version CDATA #FIXED \"1.0\">\n" +
                             "\n" +
                             "<!ELEMENT comment (#PCDATA) >\n" +
                             "\n" +
                             "<!ELEMENT entry (#PCDATA) >\n" +
                             "\n" +
                             "<!ATTLIST entry key CDATA #REQUIRED>\n" +
                             "]>";
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(
                    new StringReader(inLined + correctedS));
            StAXOMBuilder builder = new StAXOMBuilder(reader);
            return builder.getDocumentElement();

        } catch (XMLStreamException e) {
            throw new RuntimeException("Error Creating a OMElement from properties : " + properties + ":" + e, e);
        } catch (IOException e) {
            throw new RuntimeException("Error Creating a OMElement from properties : " + properties + ":" + e, e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ignored) {
                }

            }
        }
    }
}
