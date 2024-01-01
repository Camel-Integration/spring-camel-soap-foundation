package org.integration.camelfoundation.util;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;

import javax.xml.namespace.QName;

public class NumberConversionHeaderUtil {

    private static String OPERATION_NAMESPACE = "http://www.dataaccess.com/webservicesserver/";
    private static String SERVICE_NAME = "NumberConversion";

    public static void setCommonHeaders(Exchange exchange, String operationName) {
        QName serviceName = new QName(OPERATION_NAMESPACE, SERVICE_NAME);
        String operationNamespace = OPERATION_NAMESPACE;
        String contentType = "application/xml";
        exchange.getIn().removeHeaders("*");
        exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, operationName);
        exchange.getIn().setHeader(CxfConstants.SERVICE_NAME, serviceName);
        exchange.getIn().setHeader(CxfConstants.OPERATION_NAMESPACE, operationNamespace);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, contentType);
    }
    public static void setNumberToWordsHeader(Exchange exchange) {
        setCommonHeaders(exchange, "NumberToWords");
    }

    public static void setNumberToDollarsHeader(Exchange exchange) {
        setCommonHeaders(exchange, "NumberToDollars");
    }
}
