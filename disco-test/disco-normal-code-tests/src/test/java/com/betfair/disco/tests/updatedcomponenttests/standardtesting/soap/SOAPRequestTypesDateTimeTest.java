/*
 * Copyright 2013, The Sporting Exchange Limited
 * Copyright 2014, Simon Matić Langford
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

// Originally from UpdatedComponentTests/StandardTesting/SOAP/SOAP_RequestTypes_DateTime.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.soap;

import com.betfair.testing.utils.disco.misc.TimingHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when a SOAP request is received, Disco can handle the dateTime datatype
 */
public class SOAPRequestTypesDateTimeTest {
    @Test
    public void doTest() throws Exception {
        // Create the SOAP request as an XML Document (with a dateTime parameter)
        XMLHelpers xMLHelpers1 = new XMLHelpers();
        Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<DateTimeOperationRequest><bodyParamDateTimeObject><dateTimeParameter>2009-06-01T13:50:00.0Z</dateTimeParameter></bodyParamDateTimeObject></DateTimeOperationRequest>");
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = discoManager2.getNewHttpCallBean("87.248.113.14");
        discoManager2 = discoManager2;

        getNewHttpCallBean2.setServiceName("Baseline");

        getNewHttpCallBean2.setVersion("v2");
        // Create the date object expected to be returned in the response

        String convertUTCDateTimeToDiscoFormat6 = TimingHelpers.convertUTCDateTimeToDiscoFormat((int) 2009, (int) 6, (int) 1, (int) 13, (int) 50, (int) 0, (int) 0);
        // Set the created SOAP request as the PostObject
        getNewHttpCallBean2.setPostObjectForRequestType(createAsDocument1, "SOAP");
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp8 = new Timestamp(System.currentTimeMillis());
        // Make the SOAP call to the operation
        discoManager2.makeSoapDiscoHTTPCalls(getNewHttpCallBean2);
        // Create the expected response object as an XML document (using the date object created earlier)
        XMLHelpers xMLHelpers5 = new XMLHelpers();
        Document createAsDocument10 = xMLHelpers5.createAsDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(("<response><localTime>"+convertUTCDateTimeToDiscoFormat6+"</localTime><localTime2>"+convertUTCDateTimeToDiscoFormat6+"</localTime2></response>").getBytes())));
        // Check the response is as expected
        HttpResponseBean response6 = getNewHttpCallBean2.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.SOAP);
        AssertionUtils.multiAssertEquals(createAsDocument10, response6.getResponseObject());

        // generalHelpers.pauseTest(3000L);
        // Check the log entries are as expected

        discoManager2.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp8, new RequestLogRequirement("2.8", "dateTimeOperation") );
    }

}
