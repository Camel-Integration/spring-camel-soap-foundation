package org.integration.camelfoundation.route;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.integration.camelfoundation.model.NumberDto;
import org.integration.camelfoundation.util.GetNumberToWordsRequestBuilder;
import org.integration.camelfoundation.util.NumberConversionHeaderUtil;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class SoapIntegrationRoute extends RouteBuilder {
    JacksonDataFormat jsonDataFormat = new JacksonDataFormat();

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("netty-http")
                .host("localhost")
                .port("8081")
                .bindingMode(RestBindingMode.auto);

        rest("/api")
                .post("/convert-number-to-words").type(NumberDto.class).to("direct:number-to-words").produces(MediaType.APPLICATION_JSON_VALUE)
                .post("/convert-number-to-dollars").type(NumberDto.class).to("direct:number-to-dollars").produces(MediaType.APPLICATION_JSON_VALUE);

        from("direct:number-to-words")
                .process(exchange -> {
                    NumberDto numberDto = exchange.getIn().getBody(NumberDto.class);
                    BigInteger number = new BigInteger(numberDto.getNumber());
                    exchange.getIn().setBody(number);
                })
                .bean(GetNumberToWordsRequestBuilder.class, "getNumberToWords")
                .marshal().jaxb()   // marshal the request to XML
                .process(NumberConversionHeaderUtil::setNumberToWordsHeader)    // Set headers for the SOAP request
                .to("cxf:bean:numberConversionEndpoint")    // call the SOAP service
                .process(exchange -> {
                    NumberToWordsResponse response = exchange.getIn().getBody(NumberToWordsResponse.class);
                    // map to a DTO Response object or set it to the body etc.
                })
                .unmarshal().jaxb("com.dataaccess.webservicesserver")
                .marshal(jsonDataFormat)
                .end();

        from("direct:number-to-dollars")
                .process(exchange -> {
                    NumberDto numberDto = exchange.getIn().getBody(NumberDto.class);
                    BigDecimal number = new BigDecimal(numberDto.getNumber());
                    // set the value of the number in the body
                    exchange.getIn().setBody(number);
                })
                .bean(GetNumberToWordsRequestBuilder.class, "getNumberToDollars")
                .marshal().jaxb() // marshal the request to XML
                .process(NumberConversionHeaderUtil::setNumberToDollarsHeader)    // Set headers for the SOAP request
                .to("cxf:bean:numberConversionEndpoint")    // call the SOAP service
                .process(exchange -> {
                    NumberToDollarsResponse response = exchange.getIn().getBody(NumberToDollarsResponse.class);
                })
                .unmarshal().jaxb("com.dataaccess.webservicesserver")
                .marshal(jsonDataFormat)
                .end();
    }
}
