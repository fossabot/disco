/*
 * Copyright 2013, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Originally from UpdatedComponentTests/JMXServiceWebsite/Secure_JmxServiceTest_ClickOnLink.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.jmxservicewebsite;

import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpPageBean;
import com.betfair.testing.utils.disco.manager.HttpPageManager;
import org.testng.annotations.Test;

/**
 * Connects to the JMX admin page. Clicks on a link and checks the expected link page is loaded
 */
public class SecureJmxServiceTestClickOnLinkTest {
    @Test
    public void doTest() throws Exception {
        // Set the parts of the URL to load (setting the protocol to the https)
        HttpPageBean httpPageBean1 = new HttpPageBean();
        httpPageBean1.setURLParts("https", "localhost", (int) 9999, "/");
        HttpPageBean BeanyBaby = httpPageBean1;
        // Set the auth details needed to load it
        BeanyBaby.setAuthusername("jmxadmin");
        
        BeanyBaby.setAuthpassword("password");
        // Attempt to load the page and check an OK status is returned
        HttpPageManager httpPageManager2 = new HttpPageManager();
        int status = httpPageManager2.getPage(BeanyBaby);
        AssertionUtils.multiAssertEquals((int) 200, status);
        // Check the page was loaded
        HttpPageBean response3 = BeanyBaby.getMe();
        AssertionUtils.multiAssertEquals(true, response3.getPageLoaded());
        // Check the correct page is loaded
        HttpPageManager httpPageManager4 = new HttpPageManager();
        boolean result = httpPageManager4.stringExistsOnPage(BeanyBaby, "Agent View");
        AssertionUtils.multiAssertEquals(true, result);

        // Click on the testSimpleGet operation link
        HttpPageManager httpPageManager6 = new HttpPageManager();
        result = httpPageManager6.clickOnLink(BeanyBaby, "operation", "testSimpleGet");
        AssertionUtils.multiAssertEquals(true, result);
        // Check the clicked link is loaded
        HttpPageBean response7 = BeanyBaby.getMe();
        AssertionUtils.multiAssertEquals(true, response7.getPageLoaded());
    }

}
