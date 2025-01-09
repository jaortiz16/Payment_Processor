package com.banquito.cards.transaccion.model;

public class ProcesamientoFraudeRequest {
    private String codigoUnicoTransaccion;
    private String decision;

    public String getCodigoUnicoTransaccion() {
        return codigoUnicoTransaccion;
    }

    public void setCodigoUnicoTransaccion(String codigoUnicoTransaccion) {
        this.codigoUnicoTransaccion = codigoUnicoTransaccion;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
} 