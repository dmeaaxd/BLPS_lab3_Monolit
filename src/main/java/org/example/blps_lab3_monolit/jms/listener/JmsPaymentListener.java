package org.example.blps_lab3_monolit.jms.listener;

import jakarta.jms.TextMessage;
import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.service.SubscriptionService;
import org.example.blps_lab3_monolit.jms.listener.converter.TextMessageToObjectConverter;
import org.example.blps_lab3_monolit.jms.message.FinishSubscriptionJmsMessage;
import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.example.blps_lab3_monolit.jms.message.RollbackPaymentJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsNotificationSender;
import org.example.blps_lab3_monolit.jms.sender.JmsPaymentSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class JmsPaymentListener {
    @Autowired
    private final SubscriptionService subscriptionService;
    @Autowired
    private final JmsNotificationSender jmsNotificationSender;
    @Autowired
    private final JmsPaymentSender jmsPaymentSender;

    private final String FINISH_SUBSCRIPTION_QUEUE = "finishSubscriptionQueue";

    @JmsListener(destination = FINISH_SUBSCRIPTION_QUEUE)
    public void receiveFinishSubscriptionMessage(TextMessage textMessage) throws Exception {
        FinishSubscriptionJmsMessage message = TextMessageToObjectConverter.convert(textMessage.getText(), FinishSubscriptionJmsMessage.class);
        try {
            subscriptionService.finishSubscribe(message.getEmail(), message.getShopId(), message.getDuration());
        } catch (NoSuchElementException e) {
            jmsNotificationSender.sendNotification(NotificationJmsMessage
                    .builder()
                    .to(message.getEmail())
                    .theme("Оформление подписки")
                    .text("В процессе оформления подписки произошла ошибка, к сожалению мы не смогли найти указанный магазин. \n" +
                            "Попробуйте ещё раз позже")
                    .build());

            jmsPaymentSender.sendRollbackPayment(RollbackPaymentJmsMessage
                    .builder()
                    .email(message.getEmail())
                    .billId(message.getBillId())
                    .amount(message.getAmount())
                    .build());
        }
    }
}
