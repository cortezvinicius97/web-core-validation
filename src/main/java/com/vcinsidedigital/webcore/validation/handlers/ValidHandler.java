package com.vcinsidedigital.webcore.validation.handlers;

import com.vcinsidedigital.webcore.validation.annotations.Annotations.*;
import com.vcinsidedigital.webcore.validation.validator.Validator;
import com.vcinsidedigital.webcore.validation.exception.ValidationException;
import com.vcinsidedigital.webcore.extensibility.ParameterAnnotationHandler;
import com.vcinsidedigital.webcore.extensibility.ParameterContext;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidHandler implements ParameterAnnotationHandler {

    private final Gson gson = new Gson();
    private static final ThreadLocal<ValidationResult> validationResult = new ThreadLocal<>();

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return Valid.class;
    }

    @Override
    public boolean canHandle(Parameter parameter) {
        return parameter.isAnnotationPresent(Valid.class);
    }

    @Override
    public Object resolveParameter(Parameter parameter, ParameterContext context) throws Exception {
        // Limpa o resultado anterior
        validationResult.remove();

        // Get the request body
        String body = context.getBody();

        if (body == null || body.trim().isEmpty()) {
            // Armazena erro de body vazio
            ValidationException emptyBodyException = new ValidationException(
                    List.of(new ValidationException.FieldError("body", "Request body is required", null))
            );
            validationResult.set(new ValidationResult(null, emptyBodyException));

            // Retorna null - o controller precisa verificar hasErrors()
            return null;
        }

        // Deserialize to the target type
        Class<?> parameterType = parameter.getType();
        Object dto;

        try {
            dto = gson.fromJson(body, parameterType);
        } catch (Exception e) {
            ValidationException parseException = new ValidationException(
                    List.of(new ValidationException.FieldError("body", "Invalid JSON format", body))
            );
            validationResult.set(new ValidationResult(null, parseException));
            return null;
        }

        // Validate the DTO
        try {
            Validator.validate(dto);
            // Validação passou - armazena sucesso
            validationResult.set(new ValidationResult(dto, null));
            return dto;

        } catch (ValidationException e) {
            // Validação falhou - armazena erro
            validationResult.set(new ValidationResult(dto, e));
            // Retorna o DTO mesmo com erros - o controller decide o que fazer
            return dto;
        }
    }

    /**
     * Verifica se há erros de validação no request atual
     */
    public static boolean hasErrors() {
        ValidationResult result = validationResult.get();
        return result != null && result.hasErrors();
    }

    /**
     * Retorna a exceção de validação se houver
     */
    public static ValidationException getValidationException() {
        ValidationResult result = validationResult.get();
        return result != null ? result.getException() : null;
    }

    /**
     * Retorna uma resposta HTTP formatada com os erros de validação
     */
    public static HttpResponse getErrorResponse() {
        ValidationException e = getValidationException();
        if (e == null) {
            return null;
        }

        Gson gson = new Gson();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", "Request validation failed");

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

    /**
     * Limpa o resultado da validação (deve ser chamado após processar o request)
     */
    public static void clear() {
        validationResult.remove();
    }

    /**
     * Classe interna para armazenar resultado da validação
     */
    private static class ValidationResult {
        private final Object dto;
        private final ValidationException exception;

        public ValidationResult(Object dto, ValidationException exception) {
            this.dto = dto;
            this.exception = exception;
        }

        public boolean hasErrors() {
            return exception != null;
        }

        public ValidationException getException() {
            return exception;
        }

        public Object getDto() {
            return dto;
        }
    }
}