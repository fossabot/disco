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

// Originally from UpdatedComponentTests/StandardTesting/RPC/RPC_Post_RequestTypes_SimpleType_Integers.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.standardtesting.rpc;

import uk.co.exemel.testing.utils.disco.assertions.AssertionUtils;
import uk.co.exemel.testing.utils.disco.beans.HttpCallBean;
import uk.co.exemel.testing.utils.disco.beans.HttpResponseBean;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import uk.co.exemel.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import uk.co.exemel.testing.utils.disco.helpers.DiscoHelpers;
import uk.co.exemel.testing.utils.disco.manager.AccessLogRequirement;
import uk.co.exemel.testing.utils.disco.manager.DiscoManager;
import uk.co.exemel.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensure that Disco can handle SimpleType Integers in the post body, header params and query params of an RPC request
 */
public class RPCPostRequestTypesSimpleTypeIntegersTest {
    @Test
    public void doTest() throws Exception {
        // Set up the Http Call Bean to make the request
        DiscoManager discoManager1 = DiscoManager.getInstance();
        HttpCallBean callBean = discoManager1.getNewHttpCallBean("87.248.113.14");
        DiscoManager discoManager = discoManager1;
        
        discoManager.setDiscoFaultControllerJMXMBeanAttrbiute("DetailedFaults", "false");
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map[] mapArray2 = new Map[3];
        mapArray2[0] = new HashMap();
        mapArray2[0].put("method","i32SimpleTypeOperation");
        mapArray2[0].put("params","[1, 50,{\"bodyParameter\":4321234}]");
        mapArray2[0].put("id","\"SimpleInts\"");
        mapArray2[1] = new HashMap();
        mapArray2[1].put("method","i32SimpleTypeOperation");
        mapArray2[1].put("params","[-12, -23,{\"bodyParameter\":-5678765}]");
        mapArray2[1].put("id","\"SimpleIntsNeg\"");
        mapArray2[2] = new HashMap();
        mapArray2[2].put("method","i32SimpleTypeOperation");
        mapArray2[2].put("params","[0, 0,{\"bodyParameter\":0}]");
        mapArray2[2].put("id","\"SimpleIntsZero\"");
        callBean.setBatchedRequests(mapArray2);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean actualResponseJSON = callBean.getResponseObjectsByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers4 = new DiscoHelpers();
        Map<String, Object> map5 = discoHelpers4.convertBatchedResponseToMap(actualResponseJSON);
        AssertionUtils.multiAssertEquals("{\"id\":\"SimpleInts\",\"result\":{\"headerParameter\":1,\"queryParameter\":50,\"bodyParameter\":4321234},\"jsonrpc\":\"2.0\"}", map5.get("responseSimpleInts"));
        AssertionUtils.multiAssertEquals("{\"id\":\"SimpleIntsNeg\",\"result\":{\"headerParameter\":-12,\"queryParameter\":-23,\"bodyParameter\":-5678765},\"jsonrpc\":\"2.0\"}", map5.get("responseSimpleIntsNeg"));
        AssertionUtils.multiAssertEquals("{\"id\":\"SimpleIntsZero\",\"result\":{\"headerParameter\":0,\"queryParameter\":0,\"bodyParameter\":0},\"jsonrpc\":\"2.0\"}", map5.get("responseSimpleIntsZero"));
        AssertionUtils.multiAssertEquals(200, map5.get("httpStatusCode"));
        AssertionUtils.multiAssertEquals("OK", map5.get("httpStatusText"));
        // Pause the test to allow the logs to be filled
        // generalHelpers.pauseTest(500L);
        // Check the log entries are as expected
        
        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "i32SimpleTypeOperation"),new RequestLogRequirement("2.8", "i32SimpleTypeOperation"),new RequestLogRequirement("2.8", "i32SimpleTypeOperation") );
        
        DiscoManager discoManager9 = DiscoManager.getInstance();
        discoManager9.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14", "/json-rpc", "Ok") );
    }

}
