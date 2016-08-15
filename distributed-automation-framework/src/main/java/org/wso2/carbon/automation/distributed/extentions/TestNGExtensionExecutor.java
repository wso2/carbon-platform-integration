package org.wso2.carbon.automation.distributed.extentions;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.distributed.FrameworkConstants;
import org.wso2.carbon.automation.distributed.configurations.AutomationConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;

/**
 * TestNg Listener executor class.
 */
public class TestNGExtensionExecutor {
    private static HashMap<String, List<ExtensibleClass>> extensionClassObjectMap =
            new HashMap<String, List<ExtensibleClass>>();
    public static void executeExtensible(String listener, String listenerMethod,
                                         boolean reverseOrder) throws InvocationTargetException,
                                                                      IllegalAccessException,
                                                                      NoSuchMethodException {
        List<ExtensibleClass> extensionObjList = extensionClassObjectMap.get(listener);
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
            if (listenerMethod.equals(ExtensionConstants.EXECUTION_LISTENER_ON_START)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.EXECUTION_LISTENER_ON_START);
            } else if (listenerMethod.equals(ExtensionConstants.EXECUTION_LISTENER_ON_FINISH)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.EXECUTION_LISTENER_ON_FINISH);
            } else if (listenerMethod.equals(ExtensionConstants.REPORT_LISTENER_GENERATE_REPORT)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.REPORT_LISTENER_GENERATE_REPORT);
            } else if (listenerMethod.equals(ExtensionConstants.SUITE_LISTENER_ON_START)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.SUITE_LISTENER_ON_START);
            } else if (listenerMethod.equals(ExtensionConstants.SUITE_LISTENER_ON_FINISH)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.SUITE_LISTENER_ON_FINISH);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_TEST_START)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_TEST_START);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_SUCCESS)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_SUCCESS);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FAILURE)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FAILURE);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_SKIPPED)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_SKIPPED);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FAILED_BUT_PASSED);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_START)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_START);
            } else if (listenerMethod.equals(ExtensionConstants.TEST_LISTENER_ON_FINISH)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TEST_LISTENER_ON_FINISH);
            } else if (listenerMethod.equals(ExtensionConstants.TRANSFORM_LISTENER_TRANSFORM)) {
                Class<?> clazz = extensibleClass.getClazz();
                execMethod = clazz.getDeclaredMethod(ExtensionConstants.TRANSFORM_LISTENER_TRANSFORM);
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
            String listenerName = extensionNode.getNodeName();
            NodeList extensionsList = extensionNode.getChildNodes().item(0).getChildNodes();
            for (int extNo = 0; extNo < extensionsList.getLength(); extNo++) {
                NodeList classList = extensionsList.item(extNo).getChildNodes();
                for (int classNo = 0; classNo < classList.getLength(); classNo++) {
                    String className;
                    if (classList.item(classNo).getNodeName().equals(ExtensionConstants.CLASS_NAME)) {
                        className = classList.item(classNo).getChildNodes().item(0).getNodeValue().trim();
                        if (!className.isEmpty()) {
                            Class<?> cls = Class.forName(className);
                            Object object = cls.newInstance();
                            Method initMethod = cls.getDeclaredMethod(FrameworkConstants.LISTENER_INIT_METHOD);
                            initMethod.invoke(object);
                            ExtensibleClass extension = new ExtensibleClass();
                            extension.setClassInstance(object);
                            extension.setInitiated(true);
                            extension.setClazz(cls);
                            extension.setListener(listenerName);
                            extension.setClassName(className);
                            extensionObjList.add(extension);
                        }
                    }
                }
            }
            extensionClassObjectMap.put(listenerName, extensionObjList);
        }
    }
}
