package me.jinjjahalgae.global.notification;

import java.util.List;

public interface PushNotificationService {
    void sendNotification(String token, String title, String body);

    void sendMulticastNotification(List<String> tokens, String title, String body);
}