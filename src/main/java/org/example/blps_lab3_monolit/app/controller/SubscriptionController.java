package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.SubscriptionDTO;
import org.example.blps_lab3_monolit.app.dto.SubscriptionRequestDTO;
import org.example.blps_lab3_monolit.app.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subscriptions")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptions() {
        return new ResponseEntity<>(subscriptionService.getSubscriptions(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/by-shop-id/{shopId}")
    public ResponseEntity<?> getSubscriptionByShopId(@PathVariable Long shopId) {
        try{
            return new ResponseEntity<>(subscriptionService.getSubscriptionByShopId(shopId), HttpStatus.OK);
        } catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", "Subscription not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        Long shopId = subscriptionRequestDTO.getShopId();
        int duration = subscriptionRequestDTO.getDuration();

        Map<String, String> response = new HashMap<>();
        if (subscriptionRequestDTO.antiChecker()) {
            response.put("error", "Переданы неверные параметры");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            subscriptionService.startSubscribe(shopId, duration);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("message", "Запрос на подписку на магазин с id=" + shopId + " на " + duration + " дней отправлен. Ожидайте подтверждения на почту");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}