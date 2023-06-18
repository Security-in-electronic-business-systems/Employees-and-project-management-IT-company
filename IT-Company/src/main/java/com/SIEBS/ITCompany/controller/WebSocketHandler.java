package com.SIEBS.ITCompany.controller;


import com.SIEBS.ITCompany.model.Logs;
import com.SIEBS.ITCompany.model.Notification;
import com.SIEBS.ITCompany.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Controller
public class WebSocketHandler{
    @Autowired
    private final LogService logService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/logs")
    @SendTo("/logs/front")
    public ArrayList<Logs> logging(String message) {
        ArrayList<Logs> logs = logService.getLast24HoursLogs();
        return logs;
    }

    public void sendNotification(Notification notification) {
        messagingTemplate.convertAndSend("/logs/notif", notification);
    }


}