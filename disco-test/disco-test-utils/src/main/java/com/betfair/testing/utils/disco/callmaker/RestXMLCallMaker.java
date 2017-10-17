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

package com.betfair.testing.utils.disco.callmaker;

import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.beans.*;
import com.betfair.testing.utils.disco.enums.*;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import org.apache.http.client.methods.HttpUriRequest;

public class RestXMLCallMaker extends AbstractCallMaker{
	
	private XMLHelpers xmlHelpers = new XMLHelpers();
	private DiscoHelpers discoHelpers = new DiscoHelpers();

	/*
	 * Method will make a REST XML call to the specified baseline-app service, running
	 * locally, and returns the response body as a Document.
	 * 
	 * (non-Javadoc)
	 * @see com.betfair.testing.utils.disco.callmaker.AbstractCallMaker#makeCall(com.betfair.testing.utils.disco.beans.HttpCallBean)
	 */
	public HttpResponseBean makeCall(HttpCallBean httpCallBean, DiscoMessageContentTypeEnum responseContentTypeEnum) {
	
		DiscoMessageProtocolRequestTypeEnum protocolRequestType = DiscoMessageProtocolRequestTypeEnum.RESTXML;
		DiscoMessageContentTypeEnum requestContentTypeEnum = DiscoMessageContentTypeEnum.XML;
				
		HttpUriRequest method = discoHelpers.getRestMethod(httpCallBean, protocolRequestType);
		HttpResponseBean responseBean = discoHelpers.makeRestDiscoHTTPCall(httpCallBean, method, protocolRequestType, responseContentTypeEnum, requestContentTypeEnum);
		
		return responseBean;
	}

	public DiscoHelpers getDiscoHelpers() {
		return discoHelpers;
	}

	public void setDiscoHelpers(DiscoHelpers discoHelpers) {
		this.discoHelpers = discoHelpers;
	}
	
	public XMLHelpers getXmlHelpers() {
		return xmlHelpers;
	}

	public void setXmlHelpers(XMLHelpers xmlHelpers) {
		this.xmlHelpers = xmlHelpers;
	}
	
}
