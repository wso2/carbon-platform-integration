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
package org.wso2.carbon.automation.engine.configurations;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.wso2.carbon.automation.engine.configurations.mappertype.Attribute;
import org.wso2.carbon.automation.engine.configurations.mappertype.ConfigurationElement;

import java.util.HashMap;
import java.util.Iterator;

public class ConfigurationObjectTree {
    OMElement documentObject;
    ConfigurationElement rootObject;

    public ConfigurationObjectTree(OMElement omElement) {
        this.documentObject = omElement;
        rootObject = new ConfigurationElement();
    }

    /**
     * This method create the recursive data structure for the automation.xml file
     *
     * @param childOMElement OMElement for the child node
     * @param parent         ConfigurationObject type parent node
     */
    private void populateObjectTree(OMElement childOMElement, ConfigurationElement parent) {
        //this the when the root node is created
        if (parent.getName() == null) {
            parent = rootObject;
            createObject(childOMElement, parent);
            //this is where the other nodes are created
        } else {
            ConfigurationElement child = new ConfigurationElement();
            child.setParentElement(parent);
            createObject(childOMElement, child);
            parent.addChildElement(child);
            //As this is node has child elements we mark it as a node. leaf elements other way
            parent.setNode(true);
            parent.setPropertiesMap(setProperties(parent));
        }
    }

    private void createObject(OMElement childNode, ConfigurationElement element) {
        element.setName(childNode.getLocalName());
        element.setValue(childNode.getText());
        Iterator attributeIte = childNode.getAllAttributes();
        while (attributeIte.hasNext()) {
            element.addAttribute(createAttribute((OMAttribute) attributeIte.next()));
        }
        Iterator childNodeIterator = childNode.getChildElements();
        while (childNodeIterator.hasNext()) {
            populateObjectTree((OMElement) childNodeIterator.next(), element);
        }
    }

    /**
     * this method returns the property map of the parent element. Logic: if any child element has
     * it's child elements it returns null map, otherwise it generate and returns the map
     * @param parentElement parent element
     * @return  the property mpa of the parent element
     */
    private HashMap<String, String> setProperties(ConfigurationElement parentElement) {
        HashMap<String, String> properties = new HashMap<String, String>();
        for (ConfigurationElement propertyElement : parentElement.getElementList()) {
            if (propertyElement.isNode()) {
                properties = null;
                break;
            } else {
                properties.put(propertyElement.getName(), propertyElement.getValue());
            }
        }
        return properties;
    }

    /**
     * this method convert OMAttribute to the application Attribute type
     *
     * @param omAttribute OMAttribute  type attribute from the OMElement
     * @return Attribute object
     */
    private Attribute createAttribute(OMAttribute omAttribute) {
        return new Attribute(omAttribute.getLocalName(), omAttribute.getAttributeValue());
    }

    /**
     * @return Mapped object map of the Automation.xml file
     */
    public ConfigurationElement getObjectTree() {
        populateObjectTree(documentObject, rootObject);
        return rootObject;
    }
}
