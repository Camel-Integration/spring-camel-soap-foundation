package org.integration.camelfoundation.service;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import org.apache.camel.ProducerTemplate;
import org.integration.camelfoundation.model.NumberDto;
import org.integration.camelfoundation.route.NumberConversionRoutes;
import org.integration.camelfoundation.route.SoapIntegrationRoute;
import org.springframework.stereotype.Service;

@Service
public class NumberConversionService {

    private final ProducerTemplate producerTemplate;
    private final NumberConversionRoutes numberConversionRoutes;


    public NumberConversionService(ProducerTemplate producerTemplate, SoapIntegrationRoute soapIntegrationRoute) {
        this.producerTemplate = producerTemplate;
        this.numberConversionRoutes = soapIntegrationRoute;
    }

    public NumberToWordsResponse getNumberToWords(NumberDto numberDto) {
        NumberToWordsResponse response = producerTemplate.requestBody(numberConversionRoutes.getNumberToWordsRoute(), numberDto, NumberToWordsResponse.class);
        return response;
    }

    public NumberToDollarsResponse getNumberToDollars (NumberDto numberDto) {
        NumberToDollarsResponse response = producerTemplate.requestBody(numberConversionRoutes.getNumberToDollarsRoute(), numberDto, NumberToDollarsResponse.class);
        return response;
    }
}
