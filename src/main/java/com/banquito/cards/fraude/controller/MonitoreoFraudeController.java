package com.banquito.cards.fraude.controller;

import com.banquito.cards.fraude.model.MonitoreoFraude;
import com.banquito.cards.fraude.service.MonitoreoFraudeService;
import com.banquito.cards.transaccion.model.Transaccion;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoreo-fraude")
public class MonitoreoFraudeController {

    private final MonitoreoFraudeService monitoreoFraudeService;

    public MonitoreoFraudeController(MonitoreoFraudeService monitoreoFraudeService) {
        this.monitoreoFraudeService = monitoreoFraudeService;
    }
} 