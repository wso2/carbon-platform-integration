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
package org.wso2.carbon.automation.engine.configurations.mappertype;

import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationElement {
    private String name;
    private String value;
    private List<Attribute> attributes;
    private boolean isNode;
    private ConfigurationElement parentElement;
    private List<ConfigurationElement> childElements;
    private HashMap<String, String> propertiesMap;

    public ConfigurationElement() {
        childElements = new ArrayList<ConfigurationElement>();
        attributes = new ArrayList<Attribute>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public boolean isNode() {
        return isNode;
    }

    public void setNode(boolean node) {
        isNode = node;
    }

    public ConfigurationElement getParentElement() {
        return parentElement;
    }

    public void setParentElement(ConfigurationElement parentElement) {
        this.parentElement = parentElement;
    }

    public List<ConfigurationElement> getElementList() {
        return childElements;
    }

    public void setElementList(List<ConfigurationElement> elementList) {
        this.childElements = elementList;
    }

    public void addElement(ConfigurationElement element) {
        this.childElements.add(element);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void addChildElement(ConfigurationElement childElement) {
        childElements.add(childElement);
    }

    public Attribute getAttribute(String attName) throws NonExistenceException {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(attName)) {
                return attribute;
            }
        }
        throw new NonExistenceException("Attribute not found");
    }

    public boolean hasAttribute(Attribute attribute) {
        for (Attribute childAttributes : attributes) {
            if (childAttributes.getName().equals(attribute.getName()) &&
                    childAttributes.getValue().equals(attribute.getValue())) {
                return true;
            }
        }
        return false;
    }

    public ConfigurationElement getChildElement(String elementName) throws NonExistenceException {
        for (ConfigurationElement child : childElements) {
            if (child.getName().equals(elementName)) {
                return child;
            }
        }
        throw new NonExistenceException();
    }

    public ConfigurationElement getChildElement(String elementName, Attribute attribute) throws NonExistenceException {
        for (ConfigurationElement child : childElements) {
            if (child.getName().equals(elementName) && child.hasAttribute(attribute)) {
                return child;
            }
        }
        throw new NonExistenceException("Element not found");
    }

    //to be implemented
    public ConfigurationElement getChildElement(String elementName, Attribute[] attributes) {
        return null;
    }

    public HashMap<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public void setPropertiesMap(HashMap<String, String> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }
}
