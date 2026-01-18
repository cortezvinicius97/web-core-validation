package com.example.controller;

import com.example.advice.ControllerAdvice;
import com.vcinsidedigital.webcore.http.HttpResponse;

/**
 * Controller base opcional que seus controllers podem estender
 * Fornece métodos utilitários para simplificar o código
 */
public abstract class BaseController {

    /**
     * Verifica validação automaticamente
     * Retorna a resposta de erro se houver, ou null se estiver válido
     */
    protected HttpResponse checkValidation() {
        return ControllerAdvice.checkValidation();
    }

    /**
     * Executa uma ação apenas se a validação passar
     *
     * @param action A ação a ser executada
     * @return HttpResponse (erro de validação ou resultado da ação)
     */
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
            e.printStackTrace(); // Log do erro
            return ControllerAdvice.handleGenericException(e);
        }
    }

    /**
     * Interface funcional para ações do controller
     */
    @FunctionalInterface
    protected interface ControllerAction {
        HttpResponse run() throws Exception;
    }

    /**
     * Cria uma resposta de sucesso padrão
     */
    protected HttpResponse success(Object data) {
        return ControllerAdvice.success(data);
    }

    /**
     * Cria uma resposta de criação (201)
     */
    protected HttpResponse created(Object data) {
        return ControllerAdvice.created(data);
    }

    /**
     * Trata erro de validação customizado
     */
    protected HttpResponse badRequest(String message) {
        return ControllerAdvice.handleBadRequest(
                new IllegalArgumentException(message)
        );
    }
}
