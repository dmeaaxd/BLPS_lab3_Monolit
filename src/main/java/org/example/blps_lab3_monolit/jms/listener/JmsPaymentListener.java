package org.example.blps_lab3_monolit.jms.listener;

import jakarta.jms.TextMessage;
import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.service.SubscriptionService;
import org.example.blps_lab3_monolit.jms.listener.converter.TextMessageToObjectConverter;
import org.example.blps_lab3_monolit.jms.message.FinishSubscriptionJmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JmsPaymentListener {
    @Autowired
    private final SubscriptionService subscriptionService;

    private final String FINISH_SUBSCRIPTION_QUEUE = "finishSubscriptionQueue";

    @JmsListener(destination = FINISH_SUBSCRIPTION_QUEUE)
    public void receiveFinishSubscriptionMessage(TextMessage textMessage) throws Exception {
        FinishSubscriptionJmsMessage message = TextMessageToObjectConverter.convert(textMessage.getText(), FinishSubscriptionJmsMessage.class);
        subscriptionService.finish_subscribe(message.getEmail(), message.getShopId(), message.getDuration());
    }
}
