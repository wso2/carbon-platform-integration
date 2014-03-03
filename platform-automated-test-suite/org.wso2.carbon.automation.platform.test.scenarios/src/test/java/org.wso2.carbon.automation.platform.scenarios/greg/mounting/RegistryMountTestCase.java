package org.wso2.carbon.automation.platform.scenarios.greg.mounting;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.platform.scenarios.greg.TestSetup;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.ResourceImpl;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

import java.util.UUID;

/**
 * This test case consists of test for mounted environments
 * Here /_system/governance/mounted is a mounted collection from other registry
 *
 */
public class RegistryMountTestCase extends TestSetup {
    private static final Log log = LogFactory.getLog(RegistryMountTestCase.class);

    @BeforeClass(groups = "wso2.greg", alwaysRun = true, description = "initialize test environment")
    public void init() {
        try {
            super.init();
            Resource resource = registry.newResource();
            resource.setUUID(String.valueOf(UUID.randomUUID()));
            resource.setContent("This is a sample content");
            registry.put("/_system/governance/trunk/ABC.txt", resource);
            Collection collection = registry.newCollection();
            collection.setUUID(String.valueOf(UUID.randomUUID()));
            registry.put("/_system/governance/trunk/store", collection);
            Resource resource2 = registry.newResource();
            resource2.setUUID(String.valueOf(UUID.randomUUID()));
            resource2.setContent("This is a sample content2");
            registry.put("/_system/governance/trunk/store/BCD.txt", resource2);
        } catch (Exception e) {
            log.error("Registry mount tests initialization failed");
            e.printStackTrace();
        }
    }

    /**
     * Add resource to the mounted collection
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg", description = "Add resource to the mounted collection")
    public void testAddResource() throws RegistryException {
        Resource resource = new ResourceImpl();
        resource.setUUID(String.valueOf(UUID.randomUUID()));
        resource.setContent("This is a sample content");
        registry.put("/_system/governance/mounted/ABC.txt", resource);

        Assert.assertTrue(registry.resourceExists("/_system/governance/mounted/ABC.txt"),
                "Resource not saved in the mounted registry");
        Resource resource1 = registry.get("/_system/governance/mounted/ABC.txt");
        Assert.assertEquals(new String((byte [])resource1.getContent()), "This is a sample content");

    }

    /**
     * Delete a resource from the mounted collection
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg",  dependsOnMethods = "testAddResource",
            description = "Delete a resource from the mounted collection")
    public void testDeleteResource() throws RegistryException {
        registry.delete("/_system/governance/mounted/ABC.txt");
        Assert.assertFalse(registry.resourceExists("/_system/governance/mounted/ABC.txt"),
                "Resource not deleted in the mounted registry");

    }

    /**
     * Copy a resource to the mounted collection
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg",  dependsOnMethods = "testDeleteResource",
            description = "Copy a resource to the mounted collection")
    public void testCopyResource() throws RegistryException {
        registry.copy("/_system/governance/trunk/ABC.txt", "/_system/governance/mounted/ABC.txt");
        Assert.assertTrue(registry.resourceExists("/_system/governance/mounted/ABC.txt"),
                "Resource not deleted in the mounted registry");
        Resource resource1 = registry.get("/_system/governance/mounted/ABC.txt");
        Assert.assertEquals(new String((byte [])resource1.getContent()),
                "This is a sample content", "Different resource content expected");
    }

    /**
     * Add a collection to the mounted collection
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg",  dependsOnMethods = "testCopyResource",
            description = "Add a collection to the mounted collection")
    public void testAddCollection() throws RegistryException {
        Collection collection = registry.newCollection();
        collection.setUUID(String.valueOf(UUID.randomUUID()));
        registry.put("/_system/governance/mounted/store", collection);

        Assert.assertTrue(registry.resourceExists(
                "/_system/governance/mounted/store"), "Collection not added");

        Resource resource2 = registry.newResource();
        resource2.setUUID(String.valueOf(UUID.randomUUID()));
        resource2.setContent("This is a sample content2");
        registry.put("/_system/governance/mounted/store/BCD.txt", resource2);

        Assert.assertTrue(registry.resourceExists(
                "/_system/governance/mounted/store/BCD.txt"), "Resource not added to the collection");
    }

    /**
     * Delete a collection from the mounted collection
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg",  dependsOnMethods = "testAddCollection",
            description = "Delete collection from the mounted collection")
    public void testDeleteCollection() throws RegistryException {
        registry.delete("/_system/governance/mounted/store");
        Assert.assertFalse(registry.resourceExists("/_system/governance/mounted/store"),
                "Resource not deleted in the mounted registry");

    }

    /**
     * Copy a collection to the mounted environment
     *
     * @throws RegistryException
     */
    @Test(groups = "wso2.greg",  dependsOnMethods = "testDeleteCollection",
            description = "Copy a collection to the mounted collection")
    public void testCopyCollection() throws RegistryException {
        registry.copy("/_system/governance/trunk/store", "/_system/governance/mounted/store");
        Assert.assertTrue(registry.resourceExists("/_system/governance/mounted/store"),
                "Collection not added to the mounted registry");
        Assert.assertTrue(registry.resourceExists("/_system/governance/mounted/store/BCD.txt"),
                        "Collection not added to the mounted registry");
        Resource resource1 = registry.get("/_system/governance/mounted/store/BCD.txt");
        Assert.assertEquals(new String((byte [])resource1.getContent()),
                "This is a sample content2", "Different resource content expected");
    }



}
