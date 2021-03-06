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

// Originally from UpdatedComponentTests/StaticHTML/HTMLPageServed_Basic.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.statichtml;

import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import uk.co.exemel.testing.utils.disco.misc.HttpService;
import uk.co.exemel.testing.utils.disco.misc.HttptestPageBean;
import uk.co.exemel.testing.utils.disco.misc.InputStreamHelpers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Ensure that Disco can ship static html files: handle basic html
 */
public class WSDLExposedTest {
    @Test
    public void doTest() throws Exception {
        // Create the HttpCallBean
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean httpCallBeanBaseline = discoManager1.getNewHttpCallBean();
        DiscoManager discoManagerBaseline = discoManager1;
        // Get the disco logging attribute for getting log entries later
        // Point the created HttpCallBean at the correct service
        httpCallBeanBaseline.setServiceName("baseline", "discoBaseline");
        
        httpCallBeanBaseline.setVersion("v2");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp1 = new Timestamp(System.currentTimeMillis());
        DiscoManager discoManager3 = DiscoManager.getInstance();
        DiscoManager discoManager2 = discoManager3;
        // Get Expected HTML Response as Input Stream from the given file
        InputStream inputStream = InputStreamHelpers.getInputStreamForResource("wsdl/Baseline_v2.8.wsdl");
        // Transfrom the input stream into a Document (XML) for assertion

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document expectedDocResponse = documentBuilder.parse(inputStream);
        // Load the Static Page shipped from Disco
        HttptestPageBean loadedPage = HttpService.loadPage("http://localhost:8080/wsdl/Baseline_v2.8.wsdl");
        // Get the loaded page as a document
        Document actualDocument = documentBuilder.parse(new ByteArrayInputStream(loadedPage.getPageText().getBytes()));
        AssertionUtils.multiAssertEquals(expectedDocResponse, actualDocument);
        // Check the log entries are as expected
        discoManager2.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp1, new AccessLogRequirement(null, "/wsdl/Baseline_v2.8.wsdl", "Ok") );
    }

}
