package org.wso2.carbon.automation.engine.configurations;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException;
import org.wso2.carbon.automation.engine.configurations.mappertype.Attribute;

import java.util.ArrayList;

public class Configuration {
    DynamicEntity rootEntity;

    public Configuration(DynamicEntity root) {
        rootEntity = root;
    }

    public Configuration get(String childName) {
        rootEntity = rootEntity.get(childName);
        return this;
    }

    /**
     * This methods gives the requested child element for the given child name and attribute
     *
     * @param childName child element name
     * @param attribute attribute of the child element
     * @return
     * @throws org.wso2.carbon.automation.engine.configurations.exceptions.NonExistenceException
     */
    public Configuration get(String childName, Attribute attribute) throws NonExistenceException {
        ArrayList<DynamicEntity> childEntity = rootEntity.get(childName);
        for (DynamicEntity dynamicEntity : childEntity) {
            if (((dynamicEntity.get(attribute.getName()))).equals(attribute.getValue())) {
                rootEntity = dynamicEntity;
                return this;
            }
        }
        throw new NonExistenceException("No Such element found");
    }

    /**
     * This method provides the property value of the given property name in generic type
     *
     * @param propertyName name of the property
     * @param <T>   generic type
     * @return  requested property value
     */
    public <T> T getValue(String propertyName) {
        return rootEntity.get(propertyName);
    }
}
