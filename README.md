# Things can be done in a different way

### REST API

This implementation brings a variation on how to expose a REST API. Instead of using the Camel REST DSL, 
it uses the spring's @RestController annotation. This way, the REST API is implemented as a regular Spring
Controller, and the Camel route call is wrapped in a regular Spring Service.

```java
@RestController
@RequestMapping("/api/number-conversion")
public class NumberConversionController {
    // ...
    @PostMapping("/convertNumberToWords")
    public NumberToWordsResponse convertNumberToWords (@RequestBody NumberDto numberDto) {
        return numberConversionService.getNumberToWords(numberDto);
    }

    @PostMapping("/convertNumberToDollars")
    public NumberToDollarsResponse convertNumberToDollars (@RequestBody NumberDto numberDto) {
        return numberConversionService.getNumberToDollars(numberDto);
    }
}
```
The <code>@Service</code> implementation uses the <code>ProducerTemplate</code> to send the message to the
<code>direct</code> endpoint. This way, the <code>NumberConversionController</code> is not aware of the
implementation details of the Camel route.

```java
@Service
public class NumberConversionService {

    private final ProducerTemplate producerTemplate;
    // ...

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
```

### Camel Routes

The Camel direct routes change slightly to no longer process the message body and transform it to JSON.

```java

from(NUMBER_TO_WORDS_DOLLARS)
    // ...
    //.unmarshal().jaxb("com.dataaccess.webservicesserver")
    //.marshal(jsonDataFormat)
    .end();
```

### Swagger UI and OpenAPI

Swagger UI enabled to easily test the REST API. The OpenAPI specification is also available.<br>
Some configuration added for some customization.

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Exception Handling
There are multiple ways to handle exceptions in Camel which multiplies by other multiple ways to handle exceptions
in Spring for REST APIs. <br>
This implementation give you some ideas on how to handle exceptions in Camel and Spring.

#### REST API Exception Handling
For a consistent client response, the REST API exception handling is implemented using the 
<code>@RestControllerAdvice</code>. This way, all the exceptions thrown by the REST API are handled in a single
place. <br>
```java 
@RestControllerAdvice
public class RestExceptionHandler {
    ...

}
```

If you want to validate the request body, you can use the <code>@Valid</code> on the request body parameter.

```java
@PostMapping("/convertNumberToWords")
public NumberToWordsResponse convertNumberToWords (@Valid @RequestBody NumberDto numberDto) {
    return numberConversionService.getNumberToWords(numberDto);
}
```

Also, you can fail when the request body contains unknown properties. This is done by adding the below on the 
<code>application.yml</code> file.

```yaml
spring:
  jackson:
    deserialization:
      fail-on-unknown-properties: true
```

#### Camel Exception Handling
Camel has various ways to handle exceptions. This implementation uses the <code>onException</code> clause to
handle the exceptions thrown by the Camel route. <br>
```java
onException(Exception.class)
    .handled(true)
    .process(exchange -> {
        String step = exchange.getProperty("step", String.class);
        log.error("Exception occurred at step: " + step + " - " + exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
        throw new Exception("Error occurred while "+ step);
    })
.end();
```
I thought it would be a good idea to add a property to the exchange to keep track of the step where the exception
occurred. This way, the exception handler can log the step and the exception message. <br>
```java
from(NUMBER_TO_WORDS_ROUTE)
    .process(exchange -> exchange.setProperty("step", "processing-input-dto"))
    .process(exchange -> {
        ...
    })
    ...
```

### Conclusion

You have to evaluate your alternatives and choose the one that best fits your needs. The Camel REST DSL is a great
tool to expose REST APIs, but it is not the only one. <br>
I find this approach much easier and cleaner, but it is up to you to decide which one to use.
