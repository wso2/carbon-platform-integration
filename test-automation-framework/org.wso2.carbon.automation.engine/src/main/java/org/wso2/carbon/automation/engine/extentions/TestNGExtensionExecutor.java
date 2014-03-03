package org.wso2.carbon.automation.engine.extentions;

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
    static HashMap<String, List<ExtensibleClass>> extensionClassObjectMap = new HashMap<String, List<ExtensibleClass>>();

    public static void executeExtensible(String listener, String listenerMethod) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        List<ExtensibleClass> extentionObjList = extensionClassObjectMap.get(listener);
        for (ExtensibleClass extensibleClass : extentionObjList) {
            Method execMethod = null;
            if (listenerMethod.equals(ExtentionConstants.EXECUTION_LISTENER_ONSTAGE)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.EXECUTION_LISTENER_ONSTAGE);
            } else if (listenerMethod.equals(ExtentionConstants.EXECUTION_LISTENER_ONFINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.EXECUTION_LISTENER_ONFINISH);
            } else if (listenerMethod.equals(ExtentionConstants.REPORT_LISTNER_GRNRTATE_REPORT)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.REPORT_LISTNER_GRNRTATE_REPORT);
            } else if (listenerMethod.equals(ExtentionConstants.SUITE_LISTNER_ONSTART)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.SUITE_LISTNER_ONSTART);
            } else if (listenerMethod.equals(ExtentionConstants.SUITE_LISTNER_ONFINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.SUITE_LISTNER_ONFINISH);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_TEST_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_TEST_START);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_SUCCESS)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_SUCCESS);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_FAILIURE)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_FAILIURE);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_SKIPPED)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_SKIPPED);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_FAILED_BUT_PASSED)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_FAILED_BUT_PASSED);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_START)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_START);
            } else if (listenerMethod.equals(ExtentionConstants.TEST_LISTNER_ON_FINISH)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TEST_LISTNER_ON_FINISH);
            } else if (listenerMethod.equals(ExtentionConstants.TRANSFORM_LISTNER_TRANSFORM)) {
                execMethod = extensibleClass.getClazz()
                        .getDeclaredMethod(ExtentionConstants.TRANSFORM_LISTNER_TRANSFORM);
            } else {
                break;
            }
            execMethod.invoke(extensibleClass.getClassInstanace());
        }
    }


    public void initiate() throws XPathExpressionException, ClassNotFoundException,
            InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {

        NodeList extensionNodeList = AutomationConfiguration
                .getConfigurationNodeList("//listenerExtensions").item(0).getChildNodes();
        for (int nodeNo = 1; nodeNo <= extensionNodeList.getLength() - 1; nodeNo = nodeNo + 2) {
            List<ExtensibleClass> extensionObjList = new ArrayList<ExtensibleClass>();
            Node extensionNode = extensionNodeList.item(nodeNo);
            String listenerName = extensionNode.getNodeName();
            NodeList extensionsList = extensionNode.getChildNodes();
            for (int extNo = 1; extNo <= extensionsList.getLength() - 1; extNo = extNo + 2) {
                NodeList classNodeList = extensionsList.item(extNo).getChildNodes();
                if (extensionsList.item(extNo).getNodeName().equals("extentionClasses")) {
                    for (int classNo = 1; classNo <= classNodeList.getLength() - 1; classNo = classNo + 2) {
                        String className = classNodeList.item(classNo).getTextContent().toString().replace("\n", "").trim();
                        if (!className.isEmpty()) {
                            Class cls = Class.forName(className);
                            Object object = cls.newInstance();
                            Method initMethod = cls.getDeclaredMethod(FrameworkConstants.LISTENER_INIT_METHOD);
                            initMethod.invoke(object);
                            ExtensibleClass extension = new ExtensibleClass();
                            extension.setClassInstanace(object);
                            extension.setInitiated(true);
                            extension.setClazz(cls);
                            extension.setListner(listenerName);
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