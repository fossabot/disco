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

// Originally from UpdatedComponentTests/ResponseTypes/PrimitiveResponseTypes/collections_of_primitives/SOAP/SOAP_MapResponse_Int.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.responsetypes.primitiveresponsetypes.collections_of_primitives.soap;

import uk.co.exemel.testing.utils.disco.misc.XMLHelpers;
import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.sql.Timestamp;

/**
 * Test that when SOAP request operation is performed against Disco, passing in a Map of primitive integers , it is correctly  processed and the correct response map is returned.
 */
public class SOAPMapResponseIntTest {
    @Test
    public void doTest() throws Exception {

        XMLHelpers xMLHelpers1 = new XMLHelpers();
        Document createAsDocument1 = xMLHelpers1.getXMLObjectFromString("<I32MapSimpleTypeEchoRequest><msg><entry key=\"1\"><Integer>11</Integer></entry><entry key=\"2\"><Integer>22</Integer></entry></msg></I32MapSimpleTypeEchoRequest>");

        DiscoManager discoManager2 = DiscoManager.getInstance();
        HttpCallBean getNewHttpCallBean2 = discoManager2.getNewHttpCallBean("87.248.113.14");
        discoManager2 = discoManager2;


        getNewHttpCallBean2.setServiceName("Baseline", "discoBaseline");

        getNewHttpCallBean2.setVersion("v2");

        getNewHttpCallBean2.setPostObjectForRequestType(createAsDocument1, "SOAP");


        Timestamp getTimeAsTimeStamp7 = new Timestamp(System.currentTimeMillis());

        discoManager2.makeSoapDiscoHTTPCalls(getNewHttpCallBean2);

        XMLHelpers xMLHelpers4 = new XMLHelpers();
        Document createAsDocument9 = xMLHelpers4.getXMLObjectFromString("<response><entry key=\"1\"><Integer>11</Integer></entry><entry key=\"2\"><Integer>22</Integer></entry></response>");



        HttpResponseBean response5 = getNewHttpCallBean2.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.SOAP);
        AssertionUtils.multiAssertEquals(createAsDocument9, response5.getResponseObject());

        // generalHelpers.pauseTest(2000L);


        discoManager2.verifyRequestLogEntriesAfterDate(getTimeAsTimeStamp7, new RequestLogRequirement("2.8", "i32MapSimpleTypeEcho") );
    }

}
