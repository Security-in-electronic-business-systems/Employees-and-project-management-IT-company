package com.SIEBS.ITCompany.controller;


import com.SIEBS.ITCompany.model.Logs;
import com.SIEBS.ITCompany.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Controller
public class WebSocketHandler{
    @Autowired
    private final LogService logService;

    @MessageMapping("/logs")
    @SendTo("/logs/front")
    public ArrayList<Logs> logging(String message) {
        ArrayList<Logs> logs = logService.getLast24HoursLogs();
        return logs;
    }


}