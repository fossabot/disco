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

// Originally from UpdatedComponentTests/StandardValidation/REST/Rest_Post_RequestTypes_DateTimeMap_60Seconds.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.rest;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.JSONHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;

import org.json.JSONObject;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

/**
 * Ensure that Disco returns the correct fault, when a REST request has a body parameter that contains a date map with dates with the seconds set to 60 (should be rolled to the next minute)
 */
public class RestPostRequestTypesDateTimeMap60SecondsTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean hbean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager hinstance = discoManager1;
        discoManager1.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");

        hbean.setOperationName("dateTimeMapOperation");

        hbean.setServiceName("baseline", "discoBaseline");

        hbean.setVersion("v2");
        // Set the body parameter to a date list containing date objects with seconds incorrectly set to 60
        hbean.setRestPostQueryObjects(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream("<message><dateTimeMap><entry key=\"date1\"><Date>2009-12-01T00:00:60.000Z</Date></entry><entry key=\"date2\"><Date>2009-12-02T00:00:60.000Z</Date></entry></dateTimeMap></message>".getBytes())));
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp11 = new Timestamp(System.currentTimeMillis());
        // Make the REST JSON call to the operation requesting an XML response
        hinstance.makeRestDiscoHTTPCall(hbean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.XML);
        // Make the REST JSON call to the operation requesting a JSON response
        hinstance.makeRestDiscoHTTPCall(hbean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
        // Create the expected response as an XML document (Fault)
        XMLHelpers xMLHelpers3 = new XMLHelpers();
        Document expectedXML = xMLHelpers3.getXMLObjectFromString("<fault><faultcode>Client</faultcode><faultstring>DSC-0044</faultstring><detail/></fault>");
        // Convert the expected response to a JSON object for comparison with the actual response
        JSONHelpers jSONHelpers4 = new JSONHelpers();
        JSONObject expectedJSON = jSONHelpers4.convertXMLDocumentToJSONObjectRemoveRootElement(expectedXML);
        // Check the 2 responses are as expected (Bad Request)
        HttpResponseBean response5 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML);
        AssertionUtils.multiAssertEquals(expectedXML, response5.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response5.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response5.getHttpStatusText());

        HttpResponseBean response6 = hbean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        AssertionUtils.multiAssertEquals(expectedJSON, response6.getResponseObject());
        AssertionUtils.multiAssertEquals((int) 400, response6.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("Bad Request", response6.getHttpStatusText());

        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected

        DiscoManager discoManager9 = DiscoManager.getInstance();
        discoManager9.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp11, new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/dateTimeMapOperation", "BadRequest"),new AccessLogRequirement("87.248.113.14", "/discoBaseline/v2/dateTimeMapOperation", "BadRequest") );
    }

}
