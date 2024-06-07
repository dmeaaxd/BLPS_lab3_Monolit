package org.example.blps_lab3_monolit.jms.sender;

import org.example.blps_lab3_monolit.jms.message.RollbackPaymentJmsMessage;
import org.example.blps_lab3_monolit.jms.message.TopUpJmsMessage;
import org.example.blps_lab3_monolit.jms.message.WriteOffJmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JmsPaymentSender {
    // Очереди
    private final String TOP_UP_QUEUE = "topUpQueue";
    private final String WRITE_OFF_QUEUE = "writeOffQueue";

    private final String ROLLBACK_PAYMENT_QUEUE = "rollbackPaymentQueue";

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendTopUp(TopUpJmsMessage topUpJmsMessage) {
        jmsTemplate.convertAndSend(TOP_UP_QUEUE, topUpJmsMessage);
    }

    public void sendWriteOff(WriteOffJmsMessage writeOffJmsMessage) {
        jmsTemplate.convertAndSend(WRITE_OFF_QUEUE, writeOffJmsMessage);
    }

    public void sendRollbackPayment(RollbackPaymentJmsMessage rollbackPaymentJmsMessage){
        jmsTemplate.convertAndSend(ROLLBACK_PAYMENT_QUEUE, rollbackPaymentJmsMessage);
    }
}
