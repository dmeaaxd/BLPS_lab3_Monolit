package org.example.blps_lab3_monolit.app.service;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.FavoriteDTO;
import org.example.blps_lab3_monolit.app.dto.SubscriptionDTO;
import org.example.blps_lab3_monolit.app.entity.Bill;
import org.example.blps_lab3_monolit.app.entity.Favorite;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.entity.Subscription;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.BillRepository;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.ShopRepository;
import org.example.blps_lab3_monolit.app.repository.SubscriptionRepository;
import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.example.blps_lab3_monolit.jms.message.WriteOffJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsNotificationSender;
import org.example.blps_lab3_monolit.jms.sender.JmsPaymentSender;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ClientRepository clientRepository;
    private final ShopRepository shopRepository;
    private final BillRepository billRepository;

    private final JmsPaymentSender jmsPaymentSender;
    private final JmsNotificationSender jmsNotificationSender;

    private final int ONE_DAY_SUBSCRIPTION_PRICE = 10;

    public void startSubscribe(Long shopId, int duration) throws Exception {
        Optional<Shop> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            throw new Exception("Магазин " + shopId + " не найден");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();

        if (bill == null) {
            bill = new Bill();
            bill.setAccountBill(0);
            bill.setClient(client);
            bill = billRepository.save(bill);
        }

        jmsPaymentSender.sendWriteOff(WriteOffJmsMessage.builder()
                .email(client.getEmail())
                .billId(bill.getId())
                .shopId(shopId)
                .duration(duration)
                .amount(duration * ONE_DAY_SUBSCRIPTION_PRICE).build());

    }


    public void finishSubscribe(String email, Long shopId, int duration) throws NoSuchElementException {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        Client client = clientRepository.findByEmail(email);

        if (client == null) {
            return;
        }

        Subscription existingSubscription = subscriptionRepository.findByClientAndShop(client, shop);
        if (existingSubscription != null) {
            existingSubscription.setDuration(existingSubscription.getDuration() + duration);
        } else {
            existingSubscription = Subscription.builder()
                    .client(client)
                    .shop(shop)
                    .startDate(LocalDate.now())
                    .duration(duration)
                    .build();
        }

        subscriptionRepository.save(existingSubscription);

        jmsNotificationSender.sendNotification(NotificationJmsMessage.builder()
                .to(email)
                .theme("Оформление подписки")
                .text("Подписка на магазин " + shop.getName() + " оформлена/продлена на " + duration + " дней").build());
    }


    public List<SubscriptionDTO> getSubscriptions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        List<Subscription> subscriptionList = subscriptionRepository.findAllByClientId(client.getId());
        List<SubscriptionDTO> subscriptionDTOList = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            subscriptionDTOList.add(SubscriptionDTO.builder()
                    .id(subscription.getId())
                    .shopId(subscription.getShop().getId())
                    .shopName(subscription.getShop().getName())
                    .startDate(String.valueOf(subscription.getStartDate()))
                    .duration(subscription.getDuration())
                    .build());
        }

        return subscriptionDTOList;
    }

    public SubscriptionDTO getSubscriptionByShopId(Long shopId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new Exception("Shop not found"));

        Subscription subscription = subscriptionRepository.findByClientAndShop(client, shop);
        if (subscription == null) {
            throw new Exception("Subscription not found");
        }

        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .shopId(subscription.getShop().getId())
                .shopName(subscription.getShop().getName())
                .startDate(String.valueOf(subscription.getStartDate()))
                .duration(subscription.getDuration())
                .build();
    }
}

