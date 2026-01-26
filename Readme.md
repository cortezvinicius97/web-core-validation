# Web-Core Validation Plugin

Automatic validation plugin for the [web-core](https://github.com/cortezvinicius97/web-core) library, providing annotations and validators for DTOs in Java applications.

## Usage


## 🚀 Installation

### Gradle (Groovy)

```groovy
implementation 'com.vcinsidedigital:web-core-validation:1.0.4'
```

### Gradle (Kotlin)

```kotlin
implementation("com.vcinsidedigital:web-core-validation:1.0.4")
```

### Maven

```xml
<dependency>
    <groupId>com.vcinsidedigital</groupId>
    <artifactId>web-core-validation</artifactId>
    <version>1.0.4</version>
</dependency>
```

## 📋 Table of Contents

- [Installation](#installation)
- [Setup](#setup)
- [Available Annotations](#available-annotations)
- [Basic Usage](#basic-usage)
- [Error Handling](#error-handling)
- [Complete Examples](#complete-examples)
- [API Reference](#api-reference)
- [Best Practices](#best-practices)

---

## 🚀 Installation

Add the web-core dependency to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.vcinsidedigital</groupId>
        <artifactId>web-core</artifactId>
        <version>1.0.8</version>
    </dependency>
</dependencies>
```

---

## ⚙️ Setup

Register the ValidationPlugin when starting your application:

```java
package com.example;

import com.vcinsidedigital.webcore.WebServerApplication;
import com.vcinsidedigital.webcore.annotations.WebApplication;
import com.vcinsidedigital.webcore.validation.ValidationPlugin;

@WebApplication
public class Application extends WebServerApplication {
    public static void main(String[] args) {
        registerPlugin(new ValidationPlugin());
        WebServerApplication.run(Application.class, args);
    }
}
```

---

## 📝 Available Annotations

### Field Annotations

#### `@NotNull`
Validates that the field is not null.

```java
@NotNull(message = "Name cannot be null")
private String name;
```

**Parameters:**
- `message` (optional): Custom error message. Default: "Field cannot be null"

---

#### `@NotEmpty`
Validates that the field is not null and not empty (for Strings and Collections).

```java
@NotEmpty(message = "List cannot be empty")
private List<String> items;
```

**Parameters:**
- `message` (optional): Custom error message. Default: "Field cannot be empty"

**Applies to:**
- String (empty = "")
- Collections (empty = size 0)

---

#### `@NotBlank`
Validates that the String is not null, not empty, and contains at least one non-whitespace character.

```java
@NotBlank(message = "Name cannot be blank")
private String name;
```

**Parameters:**
- `message` (optional): Custom error message. Default: "Field cannot be blank"

**Note:** Trims the string before validation.

---

#### `@Size`
Validates the size/length of Strings, Collections, or Arrays.

```java
@Size(min = 3, max = 50, message = "Name must be between {min} and {max} characters")
private String name;
```

**Parameters:**
- `min` (optional): Minimum size. Default: 0
- `max` (optional): Maximum size. Default: Integer.MAX_VALUE
- `message` (optional): Custom error message. Supports placeholders `{min}` and `{max}`

**Applies to:**
- Strings (character count)
- Collections (element count)
- Arrays (element count)

---

#### `@Min`
Validates that the numeric value is greater than or equal to the specified minimum.

```java
@Min(value = 18, message = "Age must be at least {value}")
private Integer age;
```

**Parameters:**
- `value` (required): Minimum value
- `message` (optional): Custom error message. Supports placeholder `{value}`

**Applies to:** Any Number type (Integer, Long, Double, etc.)

---

#### `@Max`
Validates that the numeric value is less than or equal to the specified maximum.

```java
@Max(value = 100, message = "Score cannot exceed {value}")
private Integer score;
```

**Parameters:**
- `value` (required): Maximum value
- `message` (optional): Custom error message. Supports placeholder `{value}`

**Applies to:** Any Number type (Integer, Long, Double, etc.)

---

#### `@Email`
Validates email format using regex pattern.

```java
@Email(message = "Invalid email format")
private String email;
```

**Parameters:**
- `message` (optional): Custom error message. Default: "Invalid email format"

**Regex pattern:** `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`

---

#### `@Pattern`
Validates that the String matches a custom regular expression.

```java
@Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "Invalid SSN format")
private String ssn;
```

**Parameters:**
- `regexp` (required): Regular expression pattern
- `message` (optional): Custom error message. Default: "Field does not match the required pattern"

---

### Parameter Annotation

#### `@Valid`
Marks a controller method parameter for automatic validation.

```java
@Post("/create")
public HttpResponse createUser(@Valid UserDto userDto) {
    // Validation happens automatically before this method executes
}
```

**Note:** Must be used on controller method parameters to trigger automatic validation.

---

## 🎯 Basic Usage

### 1. Create a DTO with validation annotations

```java
package com.example.dto;

import com.vcinsidedigital.webcore.validation.annotations.Annotations.*;

public class UserDto {
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 50, message = "Name must be between {min} and {max} characters")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @Min(value = 18, message = "Must be at least {value} years old")
    private Integer age;

    // Constructors, getters, and setters
    public UserDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

### 2. Use `@Valid` in your controller

```java
package com.example.controller;

import com.example.dto.UserDto;
import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.validation.annotations.Annotations.Valid;

@RestController
public class UserController extends BaseController {
    
    @Inject
    private UserService service;

    @Post("/users")
    public HttpResponse createUser(@Valid UserDto userDto) {
        return executeIfValid(() -> {
            User user = service.createUser(userDto);
            return created(user);
        });
    }
}
```

---

## 🛡️ Error Handling

### Using BaseController (Recommended)

Create a base controller with validation helpers:

```java
package com.example.controller;

import com.example.advice.ControllerAdvice;
import com.vcinsidedigital.webcore.http.HttpResponse;

public abstract class BaseController {

    protected HttpResponse checkValidation() {
        return ControllerAdvice.checkValidation();
    }

    protected HttpResponse executeIfValid(ControllerAction action) {
        HttpResponse validationError = checkValidation();
        if (validationError != null) {
            return validationError;
        }

        try {
            return action.run();
        } catch (IllegalArgumentException e) {
            return ControllerAdvice.handleBadRequest(e);
        } catch (Exception e) {
            e.printStackTrace();
            return ControllerAdvice.handleGenericException(e);
        }
    }

    @FunctionalInterface
    protected interface ControllerAction {
        HttpResponse run() throws Exception;
    }

    protected HttpResponse success(Object data) {
        return ControllerAdvice.success(data);
    }

    protected HttpResponse created(Object data) {
        return ControllerAdvice.created(data);
    }

    protected HttpResponse badRequest(String message) {
        return ControllerAdvice.handleBadRequest(
                new IllegalArgumentException(message)
        );
    }
}
```

### ControllerAdvice Utility

Create a centralized error handling class:

```java
package com.example.advice;

import com.vcinsidedigital.webcore.validation.handlers.ValidHandler;
import com.vcinsidedigital.webcore.validation.exception.ValidationException;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ControllerAdvice {

    private static final Gson gson = new Gson();

    public static HttpResponse checkValidation() {
        if (ValidHandler.hasErrors()) {
            return ValidHandler.getErrorResponse();
        }
        return null;
    }

    public static HttpResponse handleValidationException(ValidationException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", "The submitted data is invalid");

        List<Map<String, Object>> errors = e.getErrors().stream()
                .map(error -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("field", error.getField());
                    errorMap.put("message", error.getMessage());
                    if (error.getRejectedValue() != null) {
                        errorMap.put("rejectedValue", error.getRejectedValue());
                    }
                    return errorMap;
                })
                .collect(Collectors.toList());

        errorResponse.put("errors", errors);

        return new HttpResponse()
                .status(400)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(errorResponse));
    }

    public static HttpResponse handleBadRequest(IllegalArgumentException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", e.getMessage());

        return new HttpResponse()
                .status(400)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(errorResponse));
    }

    public static HttpResponse handleGenericException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 500);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An error occurred while processing your request");

        return new HttpResponse()
                .status(500)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(errorResponse));
    }

    public static HttpResponse success(Object data) {
        return new HttpResponse()
                .status(200)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(data));
    }

    public static HttpResponse created(Object data) {
        return new HttpResponse()
                .status(201)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(data));
    }
}
```

### Error Response Format

When validation fails, the response will be:

```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "Request validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Name cannot be blank",
      "rejectedValue": ""
    },
    {
      "field": "email",
      "message": "Invalid email format",
      "rejectedValue": "invalid-email"
    }
  ]
}
```

---

## 📚 Complete Examples

### Complete CRUD Example

#### Model
```java
package com.example.model;

public class User {
    private Long id;
    private String name;
    private String email;

    public User() {}

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

#### Repository
```java
package com.example.repository;

import com.example.model.User;
import com.vcinsidedigital.webcore.annotations.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        save(new User(null, "John Doe", "john@example.com"));
        save(new User(null, "Jane Smith", "jane@example.com"));
    }

    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        database.put(user.getId(), user);
        return user;
    }
}
```

#### Service
```java
package com.example.service;

import com.example.dto.UserDto;
import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.vcinsidedigital.webcore.annotations.Inject;
import com.vcinsidedigital.webcore.annotations.Service;

import java.util.List;

@Service
public class UserService {
    @Inject
    private UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(UserDto userDto) {
        User user = new User();
        user.setId(null);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return repository.save(user);
    }
}
```

#### Controller
```java
package com.example.controller;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.service.UserService;
import com.vcinsidedigital.webcore.annotations.*;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.vcinsidedigital.webcore.http.HttpStatus;
import com.vcinsidedigital.webcore.validation.annotations.Annotations.Valid;

import java.util.List;

@RestController
public class UserController extends BaseController {
    
    @Inject
    private UserService service;

    @Get("/users")
    public List<User> listAll() {
        return service.getAllUsers();
    }

    @Get("/users/{id}")
    public User getUser(@Path("id") Long id) {
        return service.getUserById(id);
    }

    @Post("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpResponse createUser(@Valid UserDto userDto) {
        return executeIfValid(() -> {
            User user = service.createUser(userDto);
            return created(user);
        });
    }
}
```

---

## 🔧 API Reference

### ValidHandler

Static utility class for validation checking within controllers.

#### Methods

**`boolean hasErrors()`**
- Returns: `true` if there are validation errors, `false` otherwise
- Usage: Check if the current request has validation errors

**`ValidationException getValidationException()`**
- Returns: The ValidationException if errors exist, null otherwise
- Usage: Get detailed validation error information

**`HttpResponse getErrorResponse()`**
- Returns: Formatted HttpResponse with validation errors (400 status)
- Usage: Generate standardized error response

**`void clear()`**
- Returns: void
- Usage: Clear validation results from ThreadLocal (cleanup)

### Validator

Core validation engine.

#### Methods

**`void validate(Object object) throws ValidationException`**
- Parameters: Any object with validation annotations
- Throws: ValidationException if validation fails
- Usage: Manually validate any object

Example:
```java
UserDto dto = new UserDto();
dto.setName("Jo"); // Too short
dto.setEmail("invalid");

try {
    Validator.validate(dto);
} catch (ValidationException e) {
    // Handle validation errors
    List<FieldError> errors = e.getErrors();
}
```

### ValidationException

Exception thrown when validation fails.

#### Methods

**`List<FieldError> getErrors()`**
- Returns: List of field validation errors

#### FieldError Class

**Properties:**
- `String field` - Name of the field that failed validation
- `String message` - Validation error message
- `Object rejectedValue` - The value that was rejected

---

## ✅ Best Practices

### 1. Use Custom Messages
Always provide meaningful error messages for better user experience:

```java
@Size(min = 8, max = 20, message = "Password must be between {min} and {max} characters")
private String password;
```

### 2. Combine Annotations
Use multiple annotations for comprehensive validation:

```java
@NotNull(message = "Username is required")
@NotBlank(message = "Username cannot be empty")
@Size(min = 3, max = 20, message = "Username must be {min}-{max} characters")
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
private String username;
```

### 3. Extend BaseController
Leverage the BaseController pattern for cleaner code:

```java
@RestController
public class ProductController extends BaseController {
    @Post("/products")
    public HttpResponse create(@Valid ProductDto dto) {
        return executeIfValid(() -> {
            // Your logic here
            return created(product);
        });
    }
}
```

### 4. Centralize Error Handling
Use ControllerAdvice for consistent error responses across your application.

### 5. Document Your DTOs
Add JavaDoc comments to your DTOs explaining validation rules:

```java
/**
 * User registration data transfer object.
 * All fields are required and must pass validation.
 */
public class UserRegistrationDto {
    /**
     * User's email address.
     * Must be a valid email format.
     */
    @NotNull
    @Email
    private String email;
}
```

---

## 📖 Additional Resources

- [Web-Core Documentation](https://github.com/cortezvinicius97/web-core/blob/main/Readme.md)
- [Web-Core GitHub Repository](https://github.com/cortezvinicius97/web-core)

---

## 🤝 Contributing

This plugin is part of the web-core library. For contributions, issues, or feature requests, please visit the [web-core repository](https://github.com/cortezvinicius97/web-core).

---

## 📄 License

This plugin follows the same license as the web-core library.