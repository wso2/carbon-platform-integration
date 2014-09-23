package org.wso2.carbon.automation.engine.extensions;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;

import javax.xml.xpath.XPathExpressionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestNGExtensionExecutor {
    private static HashMap<String, List<ExtensibleClass>> extensionClassObjectMap = new HashMap<String, List<ExtensibleClass>>();

    public static void executeExtensible(String LISTENER, String LISTENERMethod,
                                         boolean reverseOrder) throws InvocationTargetException,
                                                                      IllegalAccessException,
                                                                      NoSuchMethodException {
        List<ExtensibleClass> extensionObjList = extensionClassObjectMap.get(LISTENER);
        List<ExtensibleClass> orderedObjList = new ArrayList<ExtensibleClass>();
        if (reverseOrder) {
            for (int index = extensionObjList.size() - 1; index >= 0; index--) {
                orderedObjList.add(extensionObjList.get(index));
            }
        } else {
            orderedObjList = extensionObjList;
        }
        for (ExtensibleClass extensibleClass : orderedObjList) {
            Method execMethod;
            if (LISTENERMethod.equals(ExtensionConstants.EXECUTION_LISTENER_ON_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.EXECUTION_LISTENER_ON_START);
            } else if (LISTENERMethod.equals(ExtensionConstants.EXECUTION_LISTENER_ON_FINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.EXECUTION_LISTENER_ON_FINISH);
            } else if (LISTENERMethod.equals(ExtensionConstants.REPORT_LISTENER_GENERATE_REPORT)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.REPORT_LISTENER_GENERATE_REPORT);
            } else if (LISTENERMethod.equals(ExtensionConstants.SUITE_LISTENER_ON_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.SUITE_LISTENER_ON_START);
            } else if (LISTENERMethod.equals(ExtensionConstants.SUITE_LISTENER_ON_FINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.SUITE_LISTENER_ON_FINISH);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_TEST_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_TEST_START);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_SUCCESS)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_SUCCESS);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FAILURE)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FAILURE);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_SKIPPED)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_SKIPPED);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_START);
            } else if (LISTENERMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FINISH);
            } else if (LISTENERMethod.equals(ExtensionConstants.TRANSFORM_LISTENER_TRANSFORM)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtensionConstants.TRANSFORM_LISTENER_TRANSFORM);
            } else {
                break;
            }
            execMethod.invoke(extensibleClass.getClassInstance());
        }
    }

    public void initiate() throws XPathExpressionException, ClassNotFoundException,
                                  InvocationTargetException, IllegalAccessException,
                                  NoSuchMethodException, InstantiationException {
        NodeList extensionNodeList = AutomationConfiguration
                .getConfigurationNodeList(ExtensionConstants.LISTENER_EXTENSION).item(0).getChildNodes();
        for (int nodeNo = 0; nodeNo < extensionNodeList.getLength(); nodeNo++) {
            List<ExtensibleClass> extensionObjList = new ArrayList<ExtensibleClass>();
            Node extensionNode = extensionNodeList.item(nodeNo);
            String LISTENERName = extensionNode.getNodeName();
            NodeList extensionsList = extensionNode.getChildNodes().item(0).getChildNodes();
            for (int extNo = 0; extNo < extensionsList.getLength(); extNo++) {
                NodeList classList = extensionsList.item(extNo).getChildNodes();
                for (int classNo = 0; classNo < classList.getLength(); classNo++) {
                    String className;
                    if (classList.item(classNo).getNodeName().equals(ExtensionConstants.CLASS_NAME)) {
                        className = classList.item(classNo).getChildNodes().item(0).getNodeValue().trim();
                        if (!className.isEmpty()) {
                            Class cls = Class.forName(className);
                            Object object = cls.newInstance();
                            Method initMethod = cls.getDeclaredMethod(FrameworkConstants.LISTENER_INIT_METHOD);
                            initMethod.invoke(object);
                            ExtensibleClass extension = new ExtensibleClass();
                            extension.setClassInstance(object);
                            extension.setInitiated(true);
                            extension.setClazz(cls);
                            extension.setListener(LISTENERName);
                            extension.setClassName(className);
                            extensionObjList.add(extension);
                        }
                    }
                }
            }
            extensionClassObjectMap.put(LISTENERName, extensionObjList);
        }
    }
}