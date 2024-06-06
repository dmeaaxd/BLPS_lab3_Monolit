package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.entity.Subscription;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription findByClientAndShop(Client client, Shop shop);

    List<Subscription> findAllByClientId(Long clientId);
    List<Subscription> findAllByShopId(Long shopId);
}
