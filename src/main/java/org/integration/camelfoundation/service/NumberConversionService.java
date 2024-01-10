package org.integration.camelfoundation.service;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import org.apache.camel.ProducerTemplate;
import org.integration.camelfoundation.exception.CamelRequestException;
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
        try {
            return producerTemplate.requestBody(numberConversionRoutes.getNumberToWordsRoute(), numberDto, NumberToWordsResponse.class);
        } catch (Exception e) {
            throw new CamelRequestException(e.getCause().getMessage());
        }
    }

    public NumberToDollarsResponse getNumberToDollars (NumberDto numberDto) {
        try {
            return producerTemplate.requestBody(numberConversionRoutes.getNumberToDollarsRoute(), numberDto, NumberToDollarsResponse.class);
        } catch (Exception e) {
            throw new CamelRequestException(e.getCause().getMessage());
        }
    }
}
