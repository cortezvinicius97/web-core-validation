package com.example.advice;

import com.vcinsidedigital.webcore.validation.handlers.ValidHandler;
import com.vcinsidedigital.webcore.validation.exception.ValidationException;
import com.vcinsidedigital.webcore.http.HttpResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe utilitária para tratar exceções e validações nos controllers
 */
public class ControllerAdvice {

    private static final Gson gson = new Gson();

    /**
     * Verifica se há erros de validação e retorna a resposta apropriada
     * Retorna null se não houver erros
     */
    public static HttpResponse checkValidation() {
        if (ValidHandler.hasErrors()) {
            return ValidHandler.getErrorResponse();
        }
        return null;
    }

    /**
     * Trata ValidationException e retorna HttpResponse formatado
     */
    public static HttpResponse handleValidationException(ValidationException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 400);
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", "Os dados enviados são inválidos");

        List<Map<String, Object>> errors = e.getErrors().stream()
                .map(error -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("campo", error.getField());
                    errorMap.put("mensagem", error.getMessage());
                    if (error.getRejectedValue() != null) {
                        errorMap.put("valorRecebido", error.getRejectedValue());
                    }
                    return errorMap;
                })
                .collect(Collectors.toList());

        errorResponse.put("erros", errors);

        return new HttpResponse()
                .status(400)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(errorResponse));
    }

    /**
     * Trata IllegalArgumentException (Bad Request)
     */
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

    /**
     * Trata exceções genéricas (Internal Server Error)
     */
    public static HttpResponse handleGenericException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", 500);
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "Ocorreu um erro ao processar sua requisição");

        // Em desenvolvimento, você pode adicionar mais detalhes:
        // errorResponse.put("details", e.getMessage());

        return new HttpResponse()
                .status(500)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(errorResponse));
    }

    /**
     * Método auxiliar para criar resposta de sucesso
     */
    public static HttpResponse success(Object data) {
        return new HttpResponse()
                .status(200)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(data));
    }

    /**
     * Método auxiliar para criar resposta de criação (201)
     */
    public static HttpResponse created(Object data) {
        return new HttpResponse()
                .status(201)
                .header("Content-Type", "application/json; charset=UTF-8")
                .body(gson.toJson(data));
    }
}
