package org.wso2.carbon.automation.test.api.selenium.notififcations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pulasthi
 * Date: 7/30/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegistrySubscriptionPage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public RegistrySubscriptionPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("notification.adding.link"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("notification.add.edit.link.text"))).click();

        log.info("Registry Subscription Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("registry.subscription.middle.text"))).
                getText().contains("Registry Subscription")) {
            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public ManageNotificationPage addEmailSubscription(String event, String email,
                                        String digestFrequency, String hierarchicalSubscriptionMethod)
            throws InterruptedException, IOException {

        Select eventType = new Select(driver.findElement(By.id(uiElementMapper.getElement("registry.subscription" +
                ".event.id"))));
        eventType.selectByVisibleText(event);

        Select notificationType = new Select(driver.findElement(By.id(uiElementMapper.getElement("registry" +
                ".subscription.notification.id"))));
        notificationType.selectByVisibleText("E-mail");

        WebElement subEmail = driver.findElement(By.id(uiElementMapper.getElement("registry.subscription.email.id")
        ));
        subEmail.sendKeys(email);

        Select digestType = new Select(driver.findElement(By.id(uiElementMapper.getElement("registry.subscription" +
                ".digest.id"))));
        digestType.selectByVisibleText(digestFrequency);

        Select hierarchicalSubscriptionMethodType = new Select(driver.findElement(By.id(uiElementMapper.getElement
                ("registry.subscription.hsmethod.id"))));
        hierarchicalSubscriptionMethodType.selectByVisibleText(hierarchicalSubscriptionMethod);

        driver.findElement(By.id(uiElementMapper.getElement("registry.subscription.subscribe.button.id"))).click();
        return new ManageNotificationPage(driver);

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
