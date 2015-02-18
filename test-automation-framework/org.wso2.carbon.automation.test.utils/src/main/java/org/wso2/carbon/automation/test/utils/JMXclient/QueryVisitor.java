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

package org.wso2.carbon.automation.test.utils.JMXclient;

import com.sun.tools.hat.internal.model.JavaObject;
import com.sun.tools.hat.internal.oql.OQLEngine;
import com.sun.tools.hat.internal.oql.ObjectVisitor;

import java.util.Enumeration;

public class QueryVisitor implements ObjectVisitor {
    private OQLEngine engine;
    private Object resultsHolder;

    /**
     *set OQLEngine and process OQL queries.
     *
     * @param engine OQLEngine object
     */
    public QueryVisitor(OQLEngine engine){
        this.engine = engine;
    }

    @Override
    public boolean visit(Object o) {
        try {
            Object object = engine.call("wrapIterator",new Object[]{o});
            if(object instanceof Enumeration){
                handleEnumeration((Enumeration) object);
            }else if(object instanceof JavaObject){
                resultsHolder = object;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * handle the OQL query result if it is an enumeration.
     *
     * @param enumeration response of OQL query
     */
    private void handleEnumeration(Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            try {
                Object obj = enumeration.nextElement();
                resultsHolder = (JavaObject)engine.call("unwrapJavaObject",new Object[]{obj});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * return the result
     *
     * @return
     */
    public JavaObject getResults(){
        return (JavaObject) resultsHolder;
    }
}
