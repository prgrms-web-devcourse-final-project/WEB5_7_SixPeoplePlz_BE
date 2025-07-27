package me.jinjjahalgae.global.fcm;

import java.util.List;

public interface FcmService {
    void sendNotification(String token, String title, String body);

    void sendMulticastNotification(List<String> tokens, String title, String body);
}