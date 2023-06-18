package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.model.Notification;
import com.SIEBS.ITCompany.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> getAll(){
        List<Notification> not = notificationRepository.findAll();
        return not;
    }
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    public Notification markNotificationAsOpened(Integer notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setOpened(true);
            notificationRepository.save(notification);
            return notification;
        }
        return null;
    }
    public void markAllNotificationsAsOpened() {
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification notification : notifications) {
            notification.setOpened(true);
            // Implementacija logike za spremanje promjene u bazu podataka
            notificationRepository.save(notification);
        }
    }
}
