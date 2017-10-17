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

// Originally from UpdatedComponentTests/StandardValidation/SOAP/SOAP_MissingMandatory_MapOfComplex.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardvalidation.soap;

import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that the correct fault is returned when, a SOAP Post operation is performed against Disco, passing a Map of complex object in the post body, where the complex object  contained in the map has a mandatory field missing
 */
public class SOAPMissingMandatoryMapOfComplexTest {
    @Test(dataProvider = "SchemaValidationEnabled")
    public void doTest(boolean schemaValidationEnabled) throws Exception {
        DiscoHelpers helpers = new DiscoHelpers();
        try {
            DiscoManager discoManager = DiscoManager.getInstance();
            helpers.setSOAPSchemaValidationEnabled(schemaValidationEnabled);
            // Create the SOAP request as an XML Document (with a complex map parameter with complex entry missing mandatory fields)
            XMLHelpers xMLHelpers1 = new XMLHelpers();
            Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<MapOfComplexOperationRequest><inputMap><entry key=\"aaa\"><ComplexObject/></entry></inputMap></MapOfComplexOperationRequest>");
            // Set up the Http Call Bean to make the request
            DiscoManager discoManager2 = DiscoManager.getInstance();
            HttpCallBean getNewHttpCallBean5 = discoManager2.getNewHttpCallBean("87.248.113.14");
            DiscoManager discoManager5 = discoManager2;

            getNewHttpCallBean5.setServiceName("Baseline");

            getNewHttpCallBean5.setVersion("v2");
            // Set the created SOAP request as the PostObject
            getNewHttpCallBean5.setPostObjectForRequestType(createAsDocument1, "SOAP");
            // Get current time for getting log entries later

            Timestamp getTimeAsTimeStamp10 = new Timestamp(System.currentTimeMillis());
            // Make the SOAP call to the operation
            discoManager5.makeSoapDiscoHTTPCalls(getNewHttpCallBean5);
            // Create the expected response object as an XML document (fault)
            XMLHelpers xMLHelpers4 = new XMLHelpers();
            Document createAsDocument11 = xMLHelpers4.getXMLObjectFromString("<soapenv:Fault><faultcode>soapenv:Client</faultcode><faultstring>DSC-0018</faultstring><detail/></soapenv:Fault>");
            // Check the response is as expected
            HttpResponseBean response5 = getNewHttpCallBean5.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.SOAP);
            AssertionUtils.multiAssertEquals(createAsDocument11, response5.getResponseObject());

            // generalHelpers.pauseTest(2000L);
            // Check the log entries are as expected

            discoManager5.verifyAccessLogEntriesAfterDate(getTimeAsTimeStamp10, new AccessLogRequirement("87.248.113.14", "/BaselineService/v2", "BadRequest"));
        } finally {
            helpers.setSOAPSchemaValidationEnabled(true);
        }
    }

    @DataProvider(name = "SchemaValidationEnabled")
    public Object[][] versions() {
        return new Object[][]{
                {true}
                , {false}
        };
    }

}
