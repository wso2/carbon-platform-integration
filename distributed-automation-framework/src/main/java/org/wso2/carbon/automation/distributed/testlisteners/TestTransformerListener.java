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

package org.wso2.carbon.automation.distributed.testlisteners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.IAnnotationTransformer;
import org.testng.IAnnotationTransformer2;
import org.testng.annotations.IConfigurationAnnotation;
import org.testng.annotations.IDataProviderAnnotation;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.ITestAnnotation;
import org.wso2.carbon.automation.distributed.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.distributed.annotations.SetEnvironment;
import org.wso2.carbon.automation.distributed.context.AutomationContext;
import org.wso2.carbon.automation.distributed.context.ContextXpathConstants;
import org.wso2.carbon.automation.distributed.exceptions.AutomationFrameworkException;
import org.wso2.carbon.automation.distributed.extentions.ExtensionConstants;
import org.wso2.carbon.automation.distributed.extentions.TestNGExtensionExecutor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.xpath.XPathExpressionException;



/**
 * Transformation TesNg listener.
 */
public class TestTransformerListener implements IAnnotationTransformer, IAnnotationTransformer2 {

    private static final Log log = LogFactory.getLog(TestTransformerListener.class);
    private AutomationContext context;

    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor,
                          Method method) {


        if (aClass != null) {
            log.info("Started Class method transform for " + aClass.getName());
            try {
                context = new AutomationContext();
                if (aClass.getAnnotation(SetEnvironment.class) != null) {
                    Annotation annotation = aClass.getAnnotation(SetEnvironment.class);
                    if (!annotationComparator(annotation.toString())) {
                        if (method != null) {
                            iTestAnnotation.setEnabled(false);
                            log.info("Skipped test Class method <" + method.getName() + "> on " +
                                     "annotation <" + annotation.toString() + ">");
                        }
                    }
                }
            } catch (XPathExpressionException e) {
                handleException("Error while running tests", e);
            } catch (AutomationFrameworkException e) {
                handleException("Error while running tests", e);
            }
        }


        if (method != null) {
            try {
                context = new AutomationContext();
                Annotation classAnnotation = method.getDeclaringClass().getAnnotation(SetEnvironment.class);
                Annotation methodAnnotation = method.getAnnotation(SetEnvironment.class);
                log.info("Started Test method Transform manager for " + method.getName());
                if (classAnnotation != null) {
                    if (!annotationComparator(classAnnotation.toString())) {
                        iTestAnnotation.setEnabled(false);
                        log.info("Skipped test method <" + method.getName() + "> on " +
                                 "annotation <" + classAnnotation.toString() + ">");
                    }
                } else if (methodAnnotation != null) {
                    if (!annotationComparator(methodAnnotation.toString())) {
                        iTestAnnotation.setEnabled(false);
                        log.info("Skipped test method <" + method.getName() + "> on " +
                                 "annotation <" + methodAnnotation.toString() + ">");
                    }
                }
                TestNGExtensionExecutor.executeExtensible(ExtensionConstants.TRANSFORM_LISTENER,
                                                          ExtensionConstants.TRANSFORM_LISTENER_TRANSFORM, false);
            }  catch (NoSuchMethodException e) {
                handleException("Error while running tests", e);
            } catch (IllegalAccessException e) {
                handleException("Error while running tests", e);
            } catch (InvocationTargetException e) {
                handleException("Error while running tests", e);
            } catch (XPathExpressionException e) {
                handleException("Error while running tests", e);
            } catch (AutomationFrameworkException e) {
                handleException("Error while running tests", e);;
            }
        }
    }

    private boolean annotationComparator(String annotation) throws AutomationFrameworkException {
        boolean compSetup = false;
        if (annotation.contains(ExecutionEnvironment.ALL.name())) {
            compSetup = true;

        } else {
            try {
                if (annotation.contains(ExecutionEnvironment.STANDALONE.name()) &&
                    annotation
                            .contains(context.getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT))) {
                    compSetup = true;
                } else if (annotation.contains(ExecutionEnvironment.PLATFORM.name()) &&
                           annotation
                                   .contains(context.getConfigurationValue(ContextXpathConstants
                                                                                   .EXECUTION_ENVIRONMENT))) {
                    compSetup = true;
                }
            } catch (XPathExpressionException e) {
                throw new AutomationFrameworkException("Error while reading"
                                                       + ContextXpathConstants.EXECUTION_ENVIRONMENT
                                                       + " from automation.xml ", e);
            }
        }
        return compSetup;
    }

    @Override
    public void transform(IConfigurationAnnotation iConfigurationAnnotation, Class aClass,
                          Constructor constructor, Method method) {
        if (method != null) {
            try {
                context = new AutomationContext();
                Annotation classAnnotation = method.getDeclaringClass().getAnnotation(SetEnvironment.class);
                Annotation methodAnnotation = method.getAnnotation(SetEnvironment.class);
                log.info("Started Configuration Transform manager  " + method.getName());
                //skip configuration methods if class level custom annotation is set.
                if (classAnnotation != null) {
                    if (!annotationComparator(classAnnotation.toString())) {
                        iConfigurationAnnotation.setEnabled(false);
                        log.info("Skipped Configuration method <" + method.getName() + "> on " +
                                 "annotation <" + classAnnotation.toString() + ">");
                    }
                    //skip configuration methods if configuration method level custom annotation is set.
                } else if (methodAnnotation != null) {
                    if (!annotationComparator(methodAnnotation.toString())) {
                        iConfigurationAnnotation.setEnabled(false);
                        log.info("Skipped Configuration method <" + method.getName() + "> on " +
                                 "annotation <" + methodAnnotation.toString() + ">");
                    }
                }
            } catch (XPathExpressionException e) {
                handleException("Error while running tests", e);
            } catch (AutomationFrameworkException e) {
                handleException("Error while running tests", e);
            }
        }
    }

    private void handleException(String msg, Exception e) {
        log.error("Execution error occurred in TestTransformerListener:-", e);
        throw new RuntimeException(msg, e);
    }

    @Override
    public void transform(IDataProviderAnnotation iDataProviderAnnotation, Method method) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void transform(IFactoryAnnotation iFactoryAnnotation, Method method) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
