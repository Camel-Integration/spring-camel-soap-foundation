package org.integration.camelfoundation.route;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.integration.camelfoundation.model.NumberDto;
import org.integration.camelfoundation.util.GetNumberToWordsRequestBuilder;
import org.integration.camelfoundation.util.NumberConversionHeaderUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
@Slf4j
public class SoapIntegrationRoute extends RouteBuilder implements NumberConversionRoutes {
    private static final String NUMBER_TO_WORDS_ROUTE = "direct:number-to-words";
    private static final String NUMBER_TO_WORDS_DOLLARS = "direct:number-to-dollars";

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .handled(true)
            .process(exchange -> {
                String step = exchange.getProperty("step", String.class);
                log.error("Exception occurred at step: " + step + " - " + exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
                throw new Exception("Error occurred while "+ step);
            })
        .end();

        from(NUMBER_TO_WORDS_ROUTE)
                .process(exchange -> exchange.setProperty("step", "processing-input-dto"))
                .process(exchange -> {
                    NumberDto numberDto = exchange.getIn().getBody(NumberDto.class);
                    BigInteger number = new BigInteger(numberDto.getNumber());
                    exchange.getIn().setBody(number);
                })
                .bean(GetNumberToWordsRequestBuilder.class, "getNumberToWords")
                .marshal().jaxb()   // marshal the request to XML
                .process(NumberConversionHeaderUtil::setNumberToWordsHeader)    // Set headers for the SOAP request
                .process(exchange -> exchange.setProperty("step", "calling-soap-service"))
                .to("cxf:bean:numberConversionEndpoint")    // call the SOAP service
                .process(exchange -> {
                    NumberToWordsResponse response = exchange.getIn().getBody(NumberToWordsResponse.class);
                    // map to a DTO Response object or set it to the body etc.
                })
                .end();

        from(NUMBER_TO_WORDS_DOLLARS)
                .process(exchange -> exchange.setProperty("step", "processing-input-dto"))
                .process(exchange -> {
                    NumberDto numberDto = exchange.getIn().getBody(NumberDto.class);
                    BigDecimal number = new BigDecimal(numberDto.getNumber());
                    exchange.getIn().setBody(number);
                })
                .bean(GetNumberToWordsRequestBuilder.class, "getNumberToDollars")
                .marshal().jaxb() // marshal the request to XML
                .process(NumberConversionHeaderUtil::setNumberToDollarsHeader)    // Set headers for the SOAP request
                .process(exchange -> exchange.setProperty("step", "calling-soap-service"))
                .to("cxf:bean:numberConversionEndpoint")    // call the SOAP service
                .process(exchange -> {
                    NumberToDollarsResponse response = exchange.getIn().getBody(NumberToDollarsResponse.class);
                })
                .end();
    }

    @Override
    public String getNumberToWordsRoute() {
        return NUMBER_TO_WORDS_ROUTE;
    }

    @Override
    public String getNumberToDollarsRoute() {
        return NUMBER_TO_WORDS_DOLLARS;
    }
}
