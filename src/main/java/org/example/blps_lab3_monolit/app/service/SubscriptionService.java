package org.example.blps_lab3_monolit.app.service;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.SubscriptionDTO;
import org.example.blps_lab3_monolit.app.entity.Bill;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

//    @Transactional(rollbackFor = Exception.class)
    public void start_subscribe(Long shopId, int duration) throws Exception {
        Optional<Shop> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            throw new Exception("Магазин " + shopId + " не найден");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();

        if (bill == null){
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


    public void finish_subscribe(String email, Long shopId, int duration){
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        Client client = clientRepository.findByEmail(email);

        Subscription existingSubscription = subscriptionRepository.findByClientAndShop(client, shop);
        if (existingSubscription != null) {
            existingSubscription.setDuration(existingSubscription.getDuration() + duration);
        } else {
            existingSubscription = Subscription.builder()
                    .client(client)
                    .shop(shop)
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
                    .clientId(subscription.getClient().getId())
                    .duration(subscription.getDuration())
                    .shopId(subscription.getShop().getId())
                    .build());
        }

        return subscriptionDTOList;
    }
}

