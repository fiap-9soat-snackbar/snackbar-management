# Product Domain Configuration Improvements

This document outlines the configuration improvements needed in the Product domain module to align with the consolidation plan. These changes should be implemented after completing the current SQS configuration improvements.

## Identified Hardcoded Values

### 1. Product.java

The `Product` class contains hardcoded validation constants that should be externalized:

```java
private static final List<String> VALID_CATEGORIES = Arrays.asList("Lanche", "Acompanhamento", "Bebida", "Sobremesa");
private static final int MIN_NAME_LENGTH = 3;
private static final int MIN_DESCRIPTION_LENGTH = 10;
```

**Recommended Changes:**
- Move these validation constants to configuration properties:
  ```java
  @Value("${product.validation.min-name-length}")
  private static int MIN_NAME_LENGTH;

  @Value("${product.validation.min-description-length}")
  private static int MIN_DESCRIPTION_LENGTH;
  
  @Value("${product.validation.valid-categories}")
  private static String[] validCategoriesArray;
  
  private static List<String> VALID_CATEGORIES;
  
  @PostConstruct
  public void init() {
      VALID_CATEGORIES = Arrays.asList(validCategoriesArray);
  }
  ```

- Add corresponding properties to application.properties:
  ```properties
  product.validation.min-name-length=3
  product.validation.min-description-length=10
  product.validation.valid-categories=Lanche,Acompanhamento,Bebida,Sobremesa
  ```

- Add environment variables to .env file:
  ```
  PRODUCT_VALIDATION_MIN_NAME_LENGTH=3
  PRODUCT_VALIDATION_MIN_DESCRIPTION_LENGTH=10
  PRODUCT_VALIDATION_VALID_CATEGORIES=Lanche,Acompanhamento,Bebida,Sobremesa
  ```

**Note:** Since `Product` is a record and uses static fields, the implementation approach will need to be adjusted. One option is to create a separate `ProductValidationConfig` class that holds these values and is injected where needed.

## Additional Considerations

1. **Domain Event Publishers**
   - `SQSDomainEventPublisher.java` already uses environment variables for queue URL
   - Ensure any retry logic or error handling thresholds are configurable

2. **Repository Configurations**
   - Check for any hardcoded query limits, page sizes, or timeouts in repository implementations

3. **Business Logic Constants**
   - Review all business logic in use cases for hardcoded values that might need to be configurable

## Implementation Challenges

The main challenge with externalizing configuration in the domain layer is maintaining the domain's independence from infrastructure concerns. Some approaches to consider:

1. **Configuration Service**
   - Create a domain service that provides configuration values
   - Implement it in the infrastructure layer using Spring's configuration

2. **Constructor Injection**
   - Pass configuration values to domain objects during construction
   - Keep domain objects free of framework annotations

3. **Factory Pattern**
   - Use factories to create domain objects with the appropriate configuration

## Implementation Priority

These Product domain configuration improvements should be implemented after completing both the SQS configuration and IAM configuration tasks. They require more careful design consideration to maintain clean architecture principles.

## Benefits

- Configurable business rules without code changes
- Ability to adjust validation rules for different environments or business needs
- Consistent configuration approach across all modules
- Better testability of domain logic with different configuration values
