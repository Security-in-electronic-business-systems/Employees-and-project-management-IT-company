package com.SIEBS.ITCompany.controller;

import com.SIEBS.ITCompany.dto.MessageResponse;
import com.SIEBS.ITCompany.model.Notification;
import com.SIEBS.ITCompany.service.AuthenticationService;
import com.SIEBS.ITCompany.service.EmailService;
import com.SIEBS.ITCompany.service.NotificationService;
import com.SIEBS.ITCompany.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notif")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    @Autowired
    private final NotificationService notificationService;
    @Autowired
    private final AuthenticationService authenticationService;
    @Autowired
    private final WebSocketHandler webSocketHandler;
    @Autowired
    private final EmailService emailService;


    @GetMapping("/getAll")
    public ResponseEntity<List<Notification>> getAllNotification(){
    List<Notification> notifications = notificationService.getAll();
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(notifications);
        }
    }
    @GetMapping("/readAll")
    public ResponseEntity<List<Notification>>  readAllNotification(){
        notificationService.markAllNotificationsAsOpened();
        List<Notification> notifications = notificationService.getAll();
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(notifications);
        }
    }


    public Notification createNotification(String notification) {
        Notification not = new Notification(new Date(), notification, false);
        if (notificationService.getLoggedUser()!= null) {
            if (notificationService.getLoggedUser().getRole().getName().equals("ADMINISTRATOR")) {
                webSocketHandler.sendNotification(not);
                not.setOpened(true);
            }
        }
        emailService.sendMail(authenticationService.getLoggedUser().getEmail(), "IT-COMPANY-WARNING", notification );
        notificationService.createNotification(not);
        return not;
    }
}
