package org.integration.camelfoundation.controller;

import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import jakarta.validation.Valid;
import org.integration.camelfoundation.model.NumberDto;
import org.integration.camelfoundation.service.NumberConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/number-conversion")
public class NumberConversionController {

    private final NumberConversionService numberConversionService;

    public NumberConversionController(NumberConversionService numberConversionService) {
        this.numberConversionService = numberConversionService;
    }


    @PostMapping("/convertNumberToWords")
    public NumberToWordsResponse convertNumberToWords (@Valid @RequestBody NumberDto numberDto) {
        return numberConversionService.getNumberToWords(numberDto);
    }

    @PostMapping("/convertNumberToDollars")
    public NumberToDollarsResponse convertNumberToDollars (@Valid @RequestBody NumberDto numberDto) {
        return numberConversionService.getNumberToDollars(numberDto);
    }
}
