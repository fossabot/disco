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

// Originally from UpdatedComponentTests/Concurrency/Rest/Rest_Post_ConcurrentRequests_JSONXML.xls;
package uk.co.exemel.disco.tests.updatedcomponenttests.concurrency.rest;

import uk.co.exemel.disco.testing.concurrency.RestConcurrentPostRequestsJETTTest;
import com.betfair.testing.utils.disco.assertions.AssertionUtils;
import com.betfair.testing.utils.disco.beans.HttpResponseBean;
import com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.manager.DiscoManager;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Ensure that when concurrent Rest JSON post requests are performed against Disco, each request is successfully sent and the XML response to each is correctly handled
 */
public class RestPostConcurrentRequestsJSONXMLTest {
    @Test
    public void doTest() throws Exception {
        DiscoManager discoManager1 = DiscoManager.getInstance();
        // Get current time for getting log entries later

        Timestamp getTimeAsTimeStamp2 = new Timestamp(System.currentTimeMillis());
        // Execute the test, creating the given number of threads and making the given number of JSON calls per thread
        RestConcurrentPostRequestsJETTTest.RestConcurrentPostRequestsTestsResultBean executeTest3 = new RestConcurrentPostRequestsJETTTest().executeTest(10, 400, DiscoMessageProtocolRequestTypeEnum.RESTJSON, DiscoMessageContentTypeEnum.XML);
        // Get the expected responses to the requests made
        Map<String, HttpResponseBean> getExpectedResponses4 = executeTest3.getExpectedResponses();
        // Check the actual responses against the expected ones (with a date tolerance of 2000ms)
        long oldTolerance = AssertionUtils.setDateTolerance(2000L);
        try {
            AssertionUtils.multiAssertEquals(getExpectedResponses4, executeTest3.getActualResponses());
            // Check the log entries are as expected
            // todo: original test didn't assert anything here...
        }
        finally {
            AssertionUtils.setDateTolerance(oldTolerance);
        }
    }

}
