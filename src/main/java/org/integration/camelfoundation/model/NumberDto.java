package org.integration.camelfoundation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NumberDto {
    @NotBlank
    @Size(min = 1, max = 10)
    private String number;

}
