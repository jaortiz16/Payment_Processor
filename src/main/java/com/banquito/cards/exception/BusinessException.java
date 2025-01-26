package com.banquito.cards.exception;

public class BusinessException extends RuntimeException {
    private final String data;
    private final String entity;
    private final String action;

    public BusinessException(String data, String entity, String action) {
        super();
        this.data = data;
        this.entity = entity;
        this.action = action;
    }

    @Override
    public String getMessage() {
        return "Error de negocio al " + this.action + " en: " + this.entity + ", con el dato: " + this.data;
    }
} 