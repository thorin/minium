/*
 * Copyright (C) 2013 The Minium Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vilt.minium.webconsole;

import static com.vilt.minium.Minium.$;
import static com.vilt.minium.actions.Interactions.checkEmpty;
import static com.vilt.minium.actions.Interactions.checkNotEmpty;
import static com.vilt.minium.actions.Interactions.click;
import static com.vilt.minium.actions.Interactions.fill;
import static com.vilt.minium.actions.Interactions.get;
import static com.vilt.minium.actions.Interactions.sendKeys;
import static com.vilt.minium.actions.Interactions.waitWhileNotEmpty;
import static com.vilt.minium.actions.Interactions.withWaitingPreset;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.vilt.minium.DefaultWebElements;
import com.vilt.minium.DefaultWebElementsDriver;

@ContextConfiguration(classes = TestConfiguration.class)
@Test(singleThreaded = true)
public class WebConsoleIT extends AbstractTestNGSpringContextTests {

    private static final String RUN_CODE_SHORTCUT = Keys.chord(Keys.CONTROL, Keys.ENTER);

    @Autowired
    @Qualifier("remoteWebDriverUrl")
    private URL remoteWebDriverUrl;

    protected DefaultWebElementsDriver wd;

    @BeforeClass
    public void before() throws IOException {
        wd = new DefaultWebElementsDriver(createNativeWebDriver(), AceEditorWebElements.class);
        wd.manage().window().setSize(new Dimension(1024, 768));

        wd.configure()
            .waitingPreset("slow")
                .timeout(120, TimeUnit.SECONDS)
                .interval(1, TimeUnit.SECONDS);

        get(wd, "http://localhost:8080/minium-webconsole");
    }

    @AfterClass
    public void after() {
        wd.quit();
    }

    @BeforeMethod
    public void goToHomePage() throws IOException {
        get(wd, "http://localhost:8080/minium-webconsole");
    }

    @Test(groups = "webdriver-creation")
    public void testCreatePhantomDriver() {
        // when
        click($(wd, ".dropdown-toggle").withText("Web Drivers"));

        click($(wd, "#browsers a").containingText("PhantomJS"));
        fill($(wd, "#webdriver-varname"), "wd");

        click($(wd, "#webdriver-create-dialog .btn").withText("Create"));

        waitProgressBar();

        click($(wd, ".dropdown-toggle").withText("Web Drivers"));
        click($(wd, "#browsers a").withText("List..."));

        DefaultWebElements varCol = $(wd, "#webdriver-list-dialog th").withText("Variable");
        DefaultWebElements webDrivers = $(wd, "#webdriver-list-dialog td").below(varCol);

        // then
        assertTrue(checkNotEmpty(webDrivers.withText("wd")), "Could not find wd web driver in the list");
    }
    
    @Test(groups = "actions-with-webdrivers", dependsOnGroups = "webdriver-creation")
    public void testRunSingleLine() {
        // when
        aceEditor().writeCode("var a ='Hello world';");
        runCode();
        aceEditor().writeCode("\n");
        aceEditor().writeCode("a");
        runCode();
        
        waitProgressBar();

        DefaultWebElements notification = notificationWithText("Hello world");
        assertTrue(checkNotEmpty(notification));
    }

    @Test(groups = "actions-with-webdrivers", dependsOnGroups = "webdriver-creation")
    public void testRunMultipleLines() {
        String code = Joiner.on("\n").join(
                "get(wd, \"http://www.google.com/ncr\");",
                "var searchbox = $(wd, \":text\").withName(\"q\");",
                "fill(searchbox, \"Minion site:wikipedia.org\");",
                "sendKeys(searchbox, Keys.ENTER);",
                "var firstResult = $(wd, \"h3 a\").first();",
                "click(firstResult);",
                "var firstParagraph = $(wd, \"#mw-content-text p\").first();");
        aceEditor().writeCode(code, true);
        runCode();
        
        waitProgressBar();
        
        aceEditor().writeCode("\n");
        aceEditor().writeCode("firstParagraph.text()");
        runCode();

        // then
        DefaultWebElements notification = notificationWithText("A minion is a follower devoted to serve his/her master/mistress relentlessly.");
        
        assertTrue(checkNotEmpty(notification));
    }

    @Test(dependsOnGroups = "actions-with-webdrivers")
    public void testRemovePhantomDriver() {
        // given
        // testCreatePhantomDriver

        // when
        DefaultWebElements webDriversDropdown = $(wd, ".dropdown-toggle").withText("Web Drivers");
        DefaultWebElements webDriversListOption = $(wd, "#browsers a").withText("List...");
        DefaultWebElements varCol = $(wd, "#webdriver-list-dialog th").withText("Variable");
        DefaultWebElements webDriver = $(wd, "#webdriver-list-dialog td").below(varCol).withText("wd");
        DefaultWebElements webDriverRemoveBtn = $(wd, "#webdriver-list-dialog .btn-danger").has(".fontello-cancel").rightOf(webDriver);
        DefaultWebElements progressBar = $(wd, ".progress-bar");
        DefaultWebElements successAlertMsg = $(wd, ".alert-success");
        
        click(webDriversDropdown);
        click(webDriversListOption);
        click(webDriverRemoveBtn);
        
        withWaitingPreset("slow").waitWhileNotEmpty(progressBar);
        waitWhileNotEmpty(successAlertMsg.containingText("removed!"));

        click(webDriversDropdown);
        click(webDriversListOption);
        
        // then
        assertTrue(checkEmpty(webDriver));
    }

    protected WebDriver createNativeWebDriver() {
        RemoteWebDriver webDriver = new RemoteWebDriver(remoteWebDriverUrl, new DesiredCapabilities());
        return new Augmenter().augment(webDriver);
    }

    private void runCode() {
        sendKeys($(wd), RUN_CODE_SHORTCUT);
        waitProgressBar();
    }

    private AceEditorWebElements aceEditor() {
        return $(wd).cast(AceEditorWebElements.class);
    }

    private void waitProgressBar() {
        withWaitingPreset("slow").waitWhileNotEmpty($(wd, ".progress-bar"));
    }

    private DefaultWebElements notificationWithText(String text) {
        DefaultWebElements notification = $(wd, ".bootstrap-growl.alert-success");
        return notification.has(notification.contents().slice(1).withText(text));
    }
}
