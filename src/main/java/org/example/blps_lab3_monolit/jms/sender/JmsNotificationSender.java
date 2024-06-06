package org.example.blps_lab3_monolit.jms.sender;

import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JmsNotificationSender {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendNotification(NotificationJmsMessage notificationJmsMessage) {
        jmsTemplate.convertAndSend("notificationQueue", notificationJmsMessage);
    }
}
