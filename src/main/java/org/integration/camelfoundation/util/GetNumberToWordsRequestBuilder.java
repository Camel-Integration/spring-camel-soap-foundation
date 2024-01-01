package org.integration.camelfoundation.util;

import com.dataaccess.webservicesserver.NumberToDollars;
import com.dataaccess.webservicesserver.NumberToWords;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class GetNumberToWordsRequestBuilder {
    public NumberToWords getNumberToWords(BigInteger number) {
        NumberToWords request = new NumberToWords();
        request.setUbiNum(number);
        return request;
    }

    public NumberToDollars getNumberToDollars(BigDecimal number) {
        NumberToDollars request = new NumberToDollars();
        request.setDNum(number);
        return request;
    }
}
