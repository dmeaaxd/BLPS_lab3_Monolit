package org.example.blps_lab3_monolit.quartz.jobs;

import org.example.blps_lab3_monolit.app.entity.Subscription;
import org.example.blps_lab3_monolit.app.repository.SubscriptionRepository;
import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsNotificationSender;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Period;


public class SubscriptionCheckJob implements Job {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private JmsNotificationSender jmsNotificationSender;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Проведена проверка на окончание подписок");
        for (Subscription subscription : subscriptionRepository.findAll()){
            if (subscription.getStartDate().plus(Period.ofDays(subscription.getDuration())).isBefore(LocalDate.now())){
                jmsNotificationSender.sendNotification(NotificationJmsMessage.builder()
                        .to(subscription.getClient().getEmail())
                        .theme("Окончание подписки")
                        .text("Ваша подписка на магазин " + subscription.getShop().getName() + " закончена. \n" +
                                "Продлите подписку)")
                        .build());
                subscriptionRepository.delete(subscription);
            }
        }
    }
}
