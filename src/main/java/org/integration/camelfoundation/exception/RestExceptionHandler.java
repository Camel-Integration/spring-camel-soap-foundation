package org.integration.camelfoundation.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    /*
        ConversionFailedException is a Spring exception that is typically thrown when there is a failure during type
        conversion. This can happen when Spring tries to bind request parameters or path variables to a method
        parameter in your controller and the types do not match.
        <code>
            @GetMapping("/users/{id}")
            public ResponseEntity<?> getUser(@PathVariable Integer id) {
                // ...
            }
        </code>
        In the above example, if the id path variable is not an integer, Spring will throw a ConversionFailedException.
     */
    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConversion(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /*
        MethodArgumentNotValidException is typically thrown when validation on an argument annotated with @Valid fails.
        This usually happens when you use the @Valid annotation on a method parameter, typically a form backing
        bean or a request body object, and binding and validation errors are found.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return new ObjectError("Validation error", fieldError.getField() + ": " + fieldError.getDefaultMessage());
                    } else {
                        return new ObjectError("Validation error", error.getObjectName() + ": " + error.getDefaultMessage());
                    }
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /*
        HttpMessageNotReadableException is a Spring exception that is typically thrown when there is an error while
        trying to convert the body of a request to a Java object. This usually happens when you use the @RequestBody
        annotation on a method parameter in your controller and the JSON sent in the request body cannot be converted
        to the Java object.<br>
        When fail-on-unknown-properties: true is set in the application.yml file, Spring will throw a
        HttpMessageNotReadableException if the JSON sent in the request body contains unrecognized fields.
        This method also handles cases where the JSON format is bad or malformed.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonParseExceptions(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMessage();
        Optional<String> unrecognizedFieldOptional = Optional.empty();
        String[] splitErrorMessage = errorMessage.split("\"");
        if (splitErrorMessage.length > 1) {
            unrecognizedFieldOptional = Optional.of(splitErrorMessage[1]);
        }
        ObjectError objectError = unrecognizedFieldOptional
                .map(unrecognizedField -> new ObjectError("JSON parse error", "Unrecognized field: " + unrecognizedField))
                .orElseGet(() -> new ObjectError("JSON parse error", errorMessage));
        return new ResponseEntity<>(objectError, HttpStatus.BAD_REQUEST);
    }

    /*
        CamelRequestException is a Camel exception that is typically thrown when there is an error while trying to
        send a request to an external service.
     */
    @ExceptionHandler(CamelRequestException.class)
    public ResponseEntity<?> handleCamelRequestException(CamelRequestException ex) {
        ObjectError objectError = new ObjectError("Integration request error", ex.getMessage());
        return new ResponseEntity<>(objectError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
