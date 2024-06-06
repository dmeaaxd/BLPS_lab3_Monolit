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


    @Transactional(rollbackFor = Exception.class)
    public SubscriptionDTO subscribe(Long shopId, int duration) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        Bill bill = client.getAccountBill();

        int totalPrice = duration * 10;
        if (bill.getAccountBill() >= totalPrice) {
            bill.setAccountBill(bill.getAccountBill() - totalPrice);
            billRepository.save(bill);
        } else {
            return null;
        }


        Optional<Shop> optionalShop = shopRepository.findById(shopId);
        if (optionalShop.isEmpty()) {
            throw new Exception("Магазин " + shopId + " не найден");
        }

        Shop shop = optionalShop.get();


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

        return SubscriptionDTO.builder()
                .clientId(existingSubscription.getClient().getId())
                .shopId(existingSubscription.getShop().getId())
                .duration(existingSubscription.getDuration())
                .build();

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

