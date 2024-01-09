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
        NumberToWordsResponse response = producerTemplate.requestBody(numberConversionRoutes.getNumberToWordsRoute(), numberDto, NumberToWordsResponse.class);
        return response;
    }

    public NumberToDollarsResponse getNumberToDollars(NumberDto numberDto) {
        NumberToDollarsResponse response = producerTemplate.requestBody(numberConversionRoutes.getNumberToDollarsRoute(), numberDto, NumberToDollarsResponse.class);
        return response;
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

### Conclusion

You have to evaluate your alternatives and choose the one that best fits your needs. The Camel REST DSL is a great
tool to expose REST APIs, but it is not the only one. <br>
I find this approach much easier and cleaner, but it is up to you to decide which one to use.
