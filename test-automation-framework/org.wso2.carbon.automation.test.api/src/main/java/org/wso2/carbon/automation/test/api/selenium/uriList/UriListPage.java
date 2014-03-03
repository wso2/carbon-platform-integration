package org.wso2.carbon.automation.test.api.selenium.uriList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class UriListPage {

    private static final Log log = LogFactory.getLog(UriListPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public UriListPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("carbon.Main.tab"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("uri.list.link"))).click();
        if (!(driver.getCurrentUrl().contains("generic"))) {
            // Alternatively, we could navigate to the login page, perhaps logging out first
            throw new IllegalStateException("This is not the URI List page");
        }
    }

    public boolean checkOnUploadUri(String UriName) throws InterruptedException {

        driver.findElement(By.linkText(uiElementMapper.getElement("uri.add.list.id"))).click();

        Thread.sleep(5000);
        driver.navigate().refresh();
        String uriNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]" +
                                                             "/table/tbody/tr[2]/td/div/div/form[4]/table/tbody/tr/td")).getText();
        log.info(uriNameOnServer);
        if (UriName.equals(uriNameOnServer)) {
            log.info("Uploaded Uri exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div" +
                                   "/form[4]/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String uriNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUriName = driver.findElement(By.xpath(uriNameOnAppServer)).getText();
                log.info("val on app is -------> " + actualUriName);
                log.info("Correct is    -------> " + UriName);

                try {

                    if (UriName.contains(actualUriName)) {
                        log.info("Uploaded URI exists");
                        return true;

                    }  else {
                        return false;
                    }


                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the Uploaded Element");

                }

            }

        }

        return false;
    }


    public void lifeCyclePromotion(String lifeCycleName) throws InterruptedException {
        driver.findElement(By.id(uiElementMapper.getElement("life.cycle.expand.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("life.cycle.add"))).click();
        new Select(driver.findElement(By.id("aspect"))).selectByVisibleText(lifeCycleName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("addAspect()");

        //checking the checkList
        String lifeCycleStage= driver.findElement(By.xpath(uiElementMapper.getElement("life.cycle.stage"))).getText();

        if(lifeCycleStage.contains("Development")){
            log.info("lifecycle is at the Testing stage");

            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option"))).click();
            Thread.sleep(1000);
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option1"))).click();
            Thread.sleep(2000);
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option2"))).click();
            Thread.sleep(1000);

            //promoting the lifecycle
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.promote"))).click();


            driver.findElement(By.cssSelector(uiElementMapper.getElement("life.cycle.promote.ok.button"))).click();

            String nextLifeCycleStage= driver.findElement(By.xpath(uiElementMapper.getElement("life.cycle.stage"))).getText();

            if(nextLifeCycleStage.contains("Testing")){
                log.info("lifecycle is at the Testing stage");


            }  else {
                log.info("lifecycle is not  at the Testing stage");
                throw new NoSuchElementException();
            }

        } else {
            log.info("lifecycle is not  at the Development stage");
            throw new NoSuchElementException();
        }


        String lifeCycleStage2= driver.findElement(By.xpath(uiElementMapper.getElement("life.cycle.stage"))).getText();


        if(lifeCycleStage2.contains("Testing")){
            log.info("lifecycle is promoting from  Testing stage");

            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option"))).click();
            Thread.sleep(1000);
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option1"))).click();
            Thread.sleep(1000);
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.add.option2"))).click();

            Thread.sleep(1000);
            //promoting the lifecycle
            driver.findElement(By.id(uiElementMapper.getElement("life.cycle.promote"))).click();
            driver.findElement(By.cssSelector(uiElementMapper.getElement("life.cycle.promote.ok.button"))).click();
            Thread.sleep(1000);

            String FinalLifeCycleStage= driver.findElement(By.xpath(uiElementMapper.getElement("life.cycle.stage"))).getText();

            if(FinalLifeCycleStage.contains("production")){
                log.info("lifecycle is at the production stage");

                driver.findElement(By.id(uiElementMapper.getElement("life.cycle.publish"))).click();
                driver.findElement(By.cssSelector(uiElementMapper.getElement("life.cycle.promote.ok.button"))).click();


            }
            else {
                log.info("lifecycle is not at the production stage");
                throw new NoSuchElementException();

            }

        }

        else {
            log.info("cannot promote the lifecycle its not at the Testing stage");
            throw new NoSuchElementException();
        }

    }




    public boolean promoteUriLifecycle(String UriName,String lifeCycleName) throws InterruptedException {

        log.info(UriName);
        Thread.sleep(5000);

        String firstElementXpath ="/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/" +
                "form[4]/table/tbody/tr/td/a";
        String uriNameOnServer = driver.findElement(By.xpath(firstElementXpath)).getText();
        log.info(uriNameOnServer);
        if (UriName.equals(uriNameOnServer)) {
            log.info("Uploaded URI exists");
            driver.findElement(By.xpath(firstElementXpath)).click();
            lifeCyclePromotion(lifeCycleName);
            return true;
        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/" +
                    "form[4]/table/tbody/tr[";
            String resourceXpath2 = "]/td/a";
            for (int i = 2; i < 10; i++) {
                String uriNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUriName = driver.findElement(By.xpath(uriNameOnAppServer)).getText();
                log.info("val on app is -------> " + actualUriName);
                log.info("Correct is    -------> " + UriName);

                try {
                    if (UriName.contains(actualUriName)) {
                        log.info("Uploaded URI exists");
                        driver.findElement(By.xpath(uriNameOnAppServer)).click();
                        lifeCyclePromotion(lifeCycleName);
                        return true;
                    } else {
                        return false;
                    }
                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the Uploaded URI");

                }
            }
        }
        return false;
    }











}
