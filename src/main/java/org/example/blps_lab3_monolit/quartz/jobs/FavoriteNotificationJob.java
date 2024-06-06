package org.example.blps_lab3_monolit.quartz.jobs;

import org.example.blps_lab3_monolit.app.entity.Favorite;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.entity.Subscription;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.FavoriteRepository;
import org.example.blps_lab3_monolit.app.repository.SubscriptionRepository;
import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsNotificationSender;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class FavoriteNotificationJob implements Job {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JmsNotificationSender jmsNotificationSender;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Client> clientList = clientRepository.findAll();
        for (Client client : clientList) {
            List<Favorite> favorites = favoriteRepository.findAllByClientId(client.getId());
            List<Subscription> subscriptions = subscriptionRepository.findAllByClientId(client.getId());

            List<Shop> favoriteShops = new ArrayList<>();
            for (Favorite favorite : favorites) {
                favoriteShops.add(favorite.getShop());
            }

            List<Shop> subscriptionShops = new ArrayList<>();
            for (Subscription subscription : subscriptions) {
                subscriptionShops.add(subscription.getShop());
            }

            List<Shop> toNotification = new ArrayList<>();
            for (Shop shop : favoriteShops) {
                if (!containsShop(subscriptionShops, shop)) {
                    toNotification.add(shop);
                }
            }

            if (!toNotification.isEmpty()){
                StringBuilder notificationText = new StringBuilder();
                notificationText.append("В вашем избранном есть магазины, на которые вы не подписаны. \nОформите подпску на следующие магазины: \n");
                for (Shop shop : toNotification) {
                    notificationText.append(shop.getName()).append("\n");
                }

                jmsNotificationSender.sendNotification(NotificationJmsMessage
                        .builder()
                        .to(client.getEmail())
                        .theme("Оформите подписку) ")
                        .text(notificationText.toString())
                        .build());
            }
        }
    }

    private boolean containsShop(final List<Shop> list, final Shop shop) {
        return list.stream().anyMatch(o -> o.getId().equals(shop.getId()));
    }
}
