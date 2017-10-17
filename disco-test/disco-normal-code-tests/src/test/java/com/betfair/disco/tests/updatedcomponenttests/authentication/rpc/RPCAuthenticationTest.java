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

// Originally from UpdatedComponentTests/Authentication/RPC/RPC_Authentication.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.authentication.rpc;

import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.manager.AccessLogRequirement;
import com.betfair.testing.utils.disco.manager.DiscoManager;
import com.betfair.testing.utils.disco.manager.RequestLogRequirement;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Verify that when the auth credentials are provided in a batched JSON RPC request, Disco correctly reads headers X-Token-Username & X-Token-Password (and rewrites them)
 */
public class RPCAuthenticationTest {
    @Test
    public void doTest() throws Exception {
        DiscoManager discoManager = DiscoManager.getInstance();
        // Set up the Http Call Bean to make the request
        HttpCallBean callBean = discoManager.getNewHttpCallBean("87.248.113.14");
        // Set the auth credentials
        Map<String, String> authCredentials = new HashMap<String, String>();
        authCredentials.put("Username", "foo");
        authCredentials.put("Password", "bar");
        callBean.setAuthCredentials(authCredentials);
        // Set the call bean to use JSON batching
        callBean.setJSONRPC(true);
        // Set the list of requests to make a batched call to
        Map<String, String>[] batchedRequests = new Map[2];
        batchedRequests[0] = new HashMap<String, String>();
        batchedRequests[0].put("method","testIdentityChain");
        batchedRequests[0].put("params","[]");
        batchedRequests[0].put("id","1");
        batchedRequests[1] = new HashMap<String, String>();
        batchedRequests[1].put("method","testIdentityChain");
        batchedRequests[1].put("params","[]");
        batchedRequests[1].put("id","2");
        callBean.setBatchedRequests(batchedRequests);
        // Get current time for getting log entries later

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        // Make JSON call to the operation requesting a JSON response
        discoManager.makeRestDiscoHTTPCall(callBean, com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum.RESTJSON, com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum.JSON);
        // Get the response to the batched query (store the response for further comparison as order of batched responses cannot be relied on)
        HttpResponseBean response = callBean.getResponseObjectsByEnum(com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON);
        // Convert the returned json object to a map for comparison
        DiscoHelpers discoHelpers5 = new DiscoHelpers();
        Map<String, Object> batchedResponses = discoHelpers5.convertBatchedResponseToMap(response);

        AssertionUtils.multiAssertEquals("{\"id\":1,\"result\":{\"identities\":[{\"credentialName\":\"CREDENTIAL: Username\",\"principal\":\"PRINCIPAL: Username\",\"credentialValue\":\"foo\"},{\"credentialName\":\"CREDENTIAL: Password\",\"principal\":\"PRINCIPAL: Password\",\"credentialValue\":\"bar\"}]},\"jsonrpc\":\"2.0\"}", batchedResponses.get("response1"));
        AssertionUtils.multiAssertEquals("{\"id\":2,\"result\":{\"identities\":[{\"credentialName\":\"CREDENTIAL: Username\",\"principal\":\"PRINCIPAL: Username\",\"credentialValue\":\"foo\"},{\"credentialName\":\"CREDENTIAL: Password\",\"principal\":\"PRINCIPAL: Password\",\"credentialValue\":\"bar\"}]},\"jsonrpc\":\"2.0\"}", batchedResponses.get("response2"));
        AssertionUtils.multiAssertEquals("200", response.getHttpStatusCode());
        AssertionUtils.multiAssertEquals("OK", response.getHttpStatusText());
        // Check the authentication headers are set correctly in the response
        Map<String, String> responseHeaders = response.getFlattenedResponseHeaders();
        AssertionUtils.multiAssertEquals("foo", responseHeaders.get("X-Token-Username"));
        AssertionUtils.multiAssertEquals("bar", responseHeaders.get("X-Token-Password"));
        // Check the log entries are as expected
        discoManager.verifyRequestLogEntriesAfterDate(timeStamp, new RequestLogRequirement("2.8", "testIdentityChain")
                                                                , new RequestLogRequirement("2.8", "testIdentityChain"));
        discoManager.verifyAccessLogEntriesAfterDate(timeStamp, new AccessLogRequirement("87.248.113.14","/json-rpc","Ok"));
    }

}
