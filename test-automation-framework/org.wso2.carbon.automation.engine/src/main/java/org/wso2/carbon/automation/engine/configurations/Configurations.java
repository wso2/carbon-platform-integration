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

import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;
import org.wso2.carbon.automation.engine.configurations.mappertype.Attribute;
import org.wso2.carbon.automation.engine.configurations.mappertype.ConfigurationElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Configurations related with the
 */
public class Configurations {
    ConfigurationElement configurationElement;

    public Configurations(ConfigurationElement automationElement) {
        configurationElement = automationElement;
    }

    /**
     * @param element   name of the element
     * @param attribute requested attribute
     * @return class object itself
     */
    public Configurations getElement(String element, Attribute attribute)
            throws NonExistenceException {
        configurationElement = configurationElement.getChildElement(element, attribute);
        return this;
    }

    /**
     * @param element    requested name of the element
     * @param attributes requested element's attribute
     * @return class object itself
     */
    public Configurations getElement(String element, Attribute[] attributes) {
        configurationElement = configurationElement.getChildElement(element, attributes);
        return this;
    }

    /**
     * @param element name of the element
     * @return class object itself
     */
    public Configurations getElement(String element) throws NonExistenceException {
        configurationElement = configurationElement.getChildElement(element);
        return this;
    }

    /**
     * @return the end value of the requested node
     */
    public String getValue() {
        return configurationElement.getValue();
    }

    public ConfigurationElement getCurrentElement(){
        return configurationElement;
    }

    public List<ConfigurationElement> getElementList(String elementName){
        List<ConfigurationElement> elementList=new ArrayList<ConfigurationElement>();
        for(ConfigurationElement element:configurationElement.getElementList()){
            if(element.getName().equals(elementName)){
                elementList.add(element);
            }
        }
        return elementList;
    }

    public List<ConfigurationElement> getElementList(String elementName,Attribute attribute) throws
            NonExistenceException {
        List<ConfigurationElement> elementList=new ArrayList<ConfigurationElement>();
        for(ConfigurationElement element:configurationElement.getElementList()){
            if(element.getName().equals(elementName) && (element.getAttribute(attribute.getName()).
                    getValue().equals(attribute.getValue()))){
                elementList.add(element);
            }
        }
        return elementList;
    }

    private HashMap<String, String> getPropertiesMap() throws NonExistenceException {
        if (configurationElement.getPropertiesMap() != null) {
            return configurationElement.getPropertiesMap();
        } else {
            throw new NonExistenceException("Error requesting property map");
        }
    }

    public String getProperty(String propertyName) throws NonExistenceException {

        return getPropertiesMap().get(propertyName);
    }
    /**
     * @param attribute name of the attribute
     * @return value of the requested attribute
     */
    public Attribute getAttribute(String attribute) throws NonExistenceException {
        return configurationElement.getAttribute(attribute);
    }
}
