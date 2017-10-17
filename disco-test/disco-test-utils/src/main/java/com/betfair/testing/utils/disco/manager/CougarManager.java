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

package com.betfair.testing.utils.disco.manager;

import uk.co.exemel.disco.util.configuration.PropertyConfigurer;
import com.betfair.testing.utils.JSONHelpers;
import com.betfair.testing.utils.disco.beans.HttpCallBean;
import com.betfair.testing.utils.disco.callmaker.AbstractCallMaker;
import com.betfair.testing.utils.disco.callmaker.CallMakerFactory;
import com.betfair.testing.utils.disco.enums.DiscoMessageContentTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolRequestTypeEnum;
import com.betfair.testing.utils.disco.enums.DiscoMessageProtocolResponseTypeEnum;
import com.betfair.testing.utils.disco.helpers.DiscoHelpers;
import com.betfair.testing.utils.disco.misc.XMLHelpers;
import org.json.JSONObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DiscoManager {

	private DiscoHelpers discoHelpers;
	private JSONHelpers jHelpers = new JSONHelpers();
	private static final Map<String, String> OPERATION_PATHS = new HashMap<String, String>(){
		{
			put("simple","testSimpleGet");
			put("noparams","testNoParams");
			put("styles","TestParameterStyles");
			put("large","testLargePost");
			put("complex","testComplexMutator");
			put("largeGet","testLargeGet");
			put("secure","testSecureService");
			put("simple/async","testSimpleAsyncGet");
			put("simple/async,timeout","testAsyncGetTimeout");
		}
	};
    private String baseLogDirectory;
    private String accessLogFileName;
    private AccessLogTailer accessLogTailer;
    private RequestLogTailer requestLogTailer;
    private ServiceLogTailer serviceLogTailer;
    private TraceLogTailer traceLogTailer;
    private long logTimeoutMs = 5000L;
    private String requestLogFileName;
    private String serviceLogFileName;
    private String traceLogFileName;

    private static DiscoManager instance = new DiscoManager();

    public static DiscoManager getInstance() {
        return instance;
    }

    private DiscoManager() {
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("/conf/com.betfair.jett.utils.disco.xml");
		discoHelpers = (DiscoHelpers)springContext.getBean("discoHelpers");

		HashMap<DiscoMessageProtocolRequestTypeEnum, AbstractCallMaker> requestBuilderMap = (HashMap<DiscoMessageProtocolRequestTypeEnum, AbstractCallMaker>)springContext.getBean("requestBuilderMap");
		CallMakerFactory.setRequestBuilderMap(requestBuilderMap);
	}

	/**
	 *
	 * Get a new DiscoHttpCallBean
	 *
	 * @return
	 */
	public HttpCallBean getNewHttpCallBean() {
		return new HttpCallBean();
	}

	/**
	 * Get a new DiscoHttpCallBean setting the ipAddress as passed
	 *
	 * @param ipAddress
	 * @return
	 */
	public HttpCallBean getNewHttpCallBean(String ipAddress) {
		HttpCallBean callBean = new HttpCallBean();
		callBean.setIpAddress(ipAddress);
		return callBean;
	}

	/**
	 * Method will make REST JSON and XML calls to the baseline-app service specified in
	 * the HttpCallBean, running locally, and returns the responses in the HTTPCallBean.
	 *
	 * @param httpCallBean
	 * @return
	 */
	public void makeRestDiscoHTTPCalls(HttpCallBean httpCallBean) {
			AbstractCallMaker callMaker;

			callMaker =  CallMakerFactory.resolveRequestBuilderForDiscoService(DiscoMessageProtocolRequestTypeEnum.RESTJSON);
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONJSON, callMaker.makeCall(httpCallBean, DiscoMessageContentTypeEnum.JSON));
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.RESTJSONXML, callMaker.makeCall(httpCallBean, DiscoMessageContentTypeEnum.XML));

			callMaker =  CallMakerFactory.resolveRequestBuilderForDiscoService(DiscoMessageProtocolRequestTypeEnum.RESTXML);
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLXML, callMaker.makeCall(httpCallBean, DiscoMessageContentTypeEnum.XML));
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.RESTXMLJSON, callMaker.makeCall(httpCallBean, DiscoMessageContentTypeEnum.JSON));
	}

	/**
	 * Method will make SOAP call to the baseline-app service specified in
	 * the HttpCallBean, running locally, and returns the responses in the HTTPCallBean.
	 *
	 * @return
	 */
	public void makeSoapDiscoHTTPCalls(HttpCallBean httpCallBean) {

			AbstractCallMaker callMaker;
			callMaker =  CallMakerFactory.resolveRequestBuilderForDiscoService(DiscoMessageProtocolRequestTypeEnum.SOAP);
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.SOAP, callMaker.makeCall(httpCallBean, DiscoMessageContentTypeEnum.XML));
	}



	public DiscoHelpers getDiscoHelpers() {
		return discoHelpers;
	}

	public void setDiscoHelpers(DiscoHelpers discoHelpers) {
		this.discoHelpers = discoHelpers;
	}


	/**
	 * Method returns a Map holding the response converted to each of the Rest message/content types
	 * supported by the disco baseline app
	 *
	 * @param document
	 * @param httpCallBean
	 * @return
	 */
	public Map<DiscoMessageProtocolRequestTypeEnum, Object> convertResponseToRestTypes(Document document, HttpCallBean httpCallBean) {

		HashMap<DiscoMessageProtocolRequestTypeEnum, Object> returnMap = new HashMap<DiscoMessageProtocolRequestTypeEnum, Object>();

		JSONObject jObject = jHelpers.convertXMLDocumentToJSONObjectRemoveRootElement(document);
		jHelpers.removeJSONObjectHoldingSameTypeList(jObject);
		returnMap.put(DiscoMessageProtocolRequestTypeEnum.RESTJSON, jObject);

		Document newDocument = null;

		if ((document != null) && (!document.getDocumentElement().getNodeName().equalsIgnoreCase("fault"))){
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				newDocument = builder.newDocument();

				/* Operation name may not equal the pathname. Have to use the operation name
				 * to create a REST wrapper.
				 */
				String operationName = OPERATION_PATHS.get(httpCallBean.getOperationName());
				if(operationName == null){
					operationName = httpCallBean.getOperationName();
				}
				String serviceName = operationName.substring(0,1).toUpperCase(Locale.ENGLISH) + operationName.substring(1) + "Response";

				Element root = (Element)newDocument.createElement(serviceName);
				newDocument.appendChild(root);
			    root.appendChild( newDocument.importNode(document.getDocumentElement(), true)  );

			}catch (ParserConfigurationException e) {
				throw new RuntimeException("Error occured while wrapping existing REST request with a new root element ResponseOperationResponse.", e);
			}
			newDocument.getDocumentElement().setAttribute("xmlns", httpCallBean.getNameSpace());
			XMLHelpers helper = new XMLHelpers();
//			LogHelper.log(helper.getXMLAsString(newDocument));
		}

		if(newDocument == null){ // Return original document (for null messages & faults)
			returnMap.put(DiscoMessageProtocolRequestTypeEnum.RESTXML, document);
		}
		else{ // Return new Document with correct root
			returnMap.put(DiscoMessageProtocolRequestTypeEnum.RESTXML, newDocument);
		}

		return returnMap;
	}


	/**
	 * Method will make REST JSON and XML calls to the baseline-app service specified in
	 * the HttpCallBean, running locally, and returns the responses in the HTTPCallBean.
	 *
	 * @return
	 */
	public void makeRestDiscoHTTPCall(HttpCallBean httpCallBean, DiscoMessageProtocolRequestTypeEnum requestProtocolType) {
			AbstractCallMaker callMaker;

			callMaker =  CallMakerFactory.resolveRequestBuilderForDiscoService(requestProtocolType);
			httpCallBean.setResponseByEnum(DiscoMessageProtocolResponseTypeEnum.REST, callMaker.makeCall(httpCallBean, null));

	}

	/**
	 *
	 * Sorts the passed array of maps by the ServiceVersion field
	 *
	 * @param inputMap
	 * @return
	 * */
	public Map<String, Object>[] sortRequestLogEntriesByServiceVersion(Map<String,Object>[] inputMap) {

		boolean swapped;
		Map<String,Object> temp;
		do{
			swapped = false;

			for(int i = 1; i < inputMap.length; i++){

				if(((Double) inputMap[i-1].get("ServiceVersion")) > ((Double) inputMap[i].get("ServiceVersion"))){
					temp = inputMap[i];
					inputMap[i] = inputMap[i-1];
					inputMap[i-1] = temp;
					swapped = true;
				}
			}
		}while(swapped);

		return inputMap;
	}

	/**
	 * Sets the value of the specified attribute for the Fault Controller JMX
	 * MBean.
	 *
	 * @param attributeName
	 * @param value
	 */
	public void setDiscoFaultControllerJMXMBeanAttrbiute(String attributeName, String value) {
		discoHelpers.setJMXFaultControllerAttribute(attributeName, value);
	}

	/**
	 *
	 * Gets the value of the passed attribute from Disco JMX Logging Manager
	 *
	 * @param attributeName
	 * @return
	 */
	public String getDiscoLogManagerJMXAttributeValue(String attributeName) {
		return discoHelpers.getJMXLoggingManagerAttributeValue(attributeName);
	}

	public Integer getDiscoUpTimeInMins(String attributeName){
		String value = discoHelpers.getRuntimeAttributeValue(attributeName);
		Integer timeInMS = Integer.valueOf(value);
		Integer timeInMins = (timeInMS / 60000);
		return timeInMins;
	}


	/**
	 *
	 * Method will make REST JSON and XML calls to the baseline-app service specified in
	 * the HttpCallBean, running locally, and returns the responses in the HTTPCallBean.
	 *
	 * @param httpCallBean
	 * @param requestProtocolType
	 * @param responseContentType
	 */
	public void makeRestDiscoHTTPCall(HttpCallBean httpCallBean, DiscoMessageProtocolRequestTypeEnum requestProtocolType, DiscoMessageContentTypeEnum responseContentType) {
			AbstractCallMaker callMaker;
			callMaker =  CallMakerFactory.resolveRequestBuilderForDiscoService(requestProtocolType);
			DiscoMessageProtocolResponseTypeEnum messageProtocolResponseTypeEnum = DiscoMessageProtocolResponseTypeEnum.valueOf(requestProtocolType.toString()+responseContentType.toString());
			httpCallBean.setResponseByEnum(messageProtocolResponseTypeEnum, callMaker.makeCall(httpCallBean, responseContentType));
	}


    public String getBaseLogDirectory() {
        if (baseLogDirectory == null) {
            baseLogDirectory = System.getProperty("disco.base.log.dir");
            if (baseLogDirectory == null) {
                String logDir = discoHelpers.getJMXApplicationPropertyValue("disco.log.dir");
                String workingDir = discoHelpers.getJMXSystemPropertyValue("user.dir");
                baseLogDirectory = workingDir + "/" + logDir;
            }
        }
        return baseLogDirectory;
    }

    public String getAccessLogFileName() {
        if (accessLogFileName == null) {
            String machineName = PropertyConfigurer.HOSTNAME;
            String logName = machineName + "-disco-baseline-access.log";
            accessLogFileName = "dw/"+logName;
        }
        return accessLogFileName;
    }

    public String getRequestLogFileName() {
        if (requestLogFileName == null) {
            String machineName = PropertyConfigurer.HOSTNAME;
            String logName = machineName + "-disco-baseline-request-Baseline.log";
            requestLogFileName = "dw/"+logName;
        }
        return requestLogFileName;
    }

    public String getServiceLogFileName() {
        if (serviceLogFileName == null) {
            String machineName = PropertyConfigurer.HOSTNAME;
            String logName = machineName + "-disco-baseline-server.log";
            serviceLogFileName = logName;
        }
        return serviceLogFileName;
    }

    public String getTraceLogFileName() {
        if (traceLogFileName == null) {
            String machineName = PropertyConfigurer.HOSTNAME;
            String logName = machineName + "-disco-baseline-trace.log";
            traceLogFileName = logName;
        }
        return traceLogFileName;
    }

    public void verifyTraceLogEntriesAfterDate(Timestamp fromDate, TraceLogRequirement... requirements) throws IOException, InterruptedException {
        verifyTraceLogEntriesAfterDate(fromDate, getLogTimeoutMs(), requirements);
    }
    public void verifyTraceLogEntriesAfterDate(Timestamp fromDate, long timeoutMs, TraceLogRequirement... requirements) throws IOException, InterruptedException {
        if (traceLogTailer == null) {
            traceLogTailer = new TraceLogTailer(new File(getBaseLogDirectory(), getTraceLogFileName()));
            traceLogTailer.awaitStart();
        }

        traceLogTailer.lookForLogLines(fromDate, timeoutMs, requirements);
    }

    public void verifyServiceLogEntriesAfterDate(Timestamp fromDate, ServiceLogRequirement... requirements) throws IOException, InterruptedException {
        verifyServiceLogEntriesAfterDate(fromDate, getLogTimeoutMs(), requirements);
    }
    public void verifyServiceLogEntriesAfterDate(Timestamp fromDate, long timeoutMs, ServiceLogRequirement... requirements) throws IOException, InterruptedException {
        if (serviceLogTailer == null) {
            serviceLogTailer = new ServiceLogTailer(new File(getBaseLogDirectory(), getServiceLogFileName()));
            serviceLogTailer.awaitStart();
        }

        serviceLogTailer.lookForLogLines(fromDate, timeoutMs, requirements);
    }
    public void verifyNoServiceLogEntriesAfterDate(Timestamp fromDate, long timeoutMs, ServiceLogRequirement... requirements) throws IOException, InterruptedException {
        if (serviceLogTailer == null) {
            serviceLogTailer = new ServiceLogTailer(new File(getBaseLogDirectory(), getServiceLogFileName()));
            serviceLogTailer.awaitStart();
        }

        serviceLogTailer.lookForNoLogLines(fromDate, timeoutMs, requirements);
    }

    public void verifyAccessLogEntriesAfterDate(Timestamp fromDate, AccessLogRequirement... requirements) throws IOException, InterruptedException {
        verifyAccessLogEntriesAfterDate(fromDate, getLogTimeoutMs(), requirements);
    }
    public void verifyAccessLogEntriesAfterDate(Timestamp fromDate, long timeoutMs, AccessLogRequirement... requirements) throws IOException, InterruptedException {
        if (accessLogTailer == null) {
            accessLogTailer = new AccessLogTailer(new File(getBaseLogDirectory(), getAccessLogFileName()));
            accessLogTailer.awaitStart();
        }

        accessLogTailer.lookForLogLines(fromDate, timeoutMs, requirements);
    }

    public LogTailer.LogLine[] verifyRequestLogEntriesAfterDate(Timestamp fromDate, RequestLogRequirement... requirements) throws IOException, InterruptedException {
        return verifyRequestLogEntriesAfterDate(fromDate, getLogTimeoutMs(), requirements);
    }
    public LogTailer.LogLine[] verifyRequestLogEntriesAfterDate(Timestamp fromDate, long timeoutMs, RequestLogRequirement... requirements) throws IOException, InterruptedException {
        if (requestLogTailer == null) {
            requestLogTailer = new RequestLogTailer(new File(getBaseLogDirectory(), getRequestLogFileName()));
            requestLogTailer.awaitStart();
        }

        return requestLogTailer.lookForLogLines(fromDate, timeoutMs, requirements);
    }

    public long getLogTimeoutMs() {
        return logTimeoutMs;
    }

    public void setLogTimeoutMs(long logTimeoutMs) {
        this.logTimeoutMs = logTimeoutMs;
    }
}
