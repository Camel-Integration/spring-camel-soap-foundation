package org.integration.camelfoundation.route;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import org.apache.camel.builder.RouteBuilder;
import org.integration.camelfoundation.model.NumberDto;
import org.integration.camelfoundation.util.GetNumberToWordsRequestBuilder;
import org.integration.camelfoundation.util.NumberConversionHeaderUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class SoapIntegrationRoute extends RouteBuilder implements NumberConversionRoutes {
    private static final String NUMBER_TO_WORDS_ROUTE = "direct:number-to-words";
    private static final String NUMBER_TO_WORDS_DOLLARS = "direct:number-to-dollars";

    @Override
    public void configure() throws Exception {

        from(NUMBER_TO_WORDS_ROUTE)
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
                .end();

        from(NUMBER_TO_WORDS_DOLLARS)
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
