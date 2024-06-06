package org.example.blps_lab3_monolit.app.service;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.discounts.DiscountDTO;
import org.example.blps_lab3_monolit.app.dto.discounts.DiscountInListDTO;
import org.example.blps_lab3_monolit.app.entity.Discount;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.DiscountRepository;
import org.example.blps_lab3_monolit.app.repository.ShopRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.Objects;

@Service
@AllArgsConstructor
public class ShopDiscountService {
    private final ShopRepository shopRepository;
    private final DiscountRepository discountRepository;
    private final ClientRepository clientRepository;

    public DiscountDTO getCurrent(Long shopId, Long discountId) {
        shopRepository.findById(shopId).orElseThrow(() -> new ObjectNotFoundException(shopId, "Магазин"));
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new ObjectNotFoundException(discountId, "Предложение"));

        return DiscountDTO.builder()
                .title(discount.getTitle())
                .description(discount.getDescription())
                .promoCode(discount.getPromoCode())
                .build();
    }

    public DiscountInListDTO create(Long shopId, DiscountDTO discountDTO) throws ObjectNotFoundException, IllegalAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        if (!checkClientRights(client.getId(), shopId)) {
            throw new IllegalAccessException("У пользователя недостаточно прав");
        }
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new ObjectNotFoundException(shopId, "Магазин"));
        Discount discount = Discount.builder()
                .title(discountDTO.getTitle())
                .description(discountDTO.getDescription())
                .promoCode(discountDTO.getPromoCode())
                .shop(shop).build();
        discount = discountRepository.save(discount);

        return DiscountInListDTO.builder()
                .id(discount.getId())
                .title(discount.getTitle())
                .description(discount.getDescription())
                .promoCode(discount.getPromoCode())
                .build();
    }

    public DiscountInListDTO update(Long shopId, Long discountId, DiscountDTO discountDTO) throws IllegalAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        if (!checkClientRights(client.getId(), shopId)) {
            throw new IllegalAccessException("У пользователя недостаточно прав");
        }
        shopRepository.findById(shopId).orElseThrow(() -> new ObjectNotFoundException(shopId, "Магазин"));
        Discount discount = discountRepository.findById(discountId).orElseThrow(() -> new ObjectNotFoundException(discountId, "Предложение"));

        discount.setTitle(discountDTO.getTitle());
        discount.setDescription(discountDTO.getDescription());
        discount.setPromoCode(discountDTO.getPromoCode());

        discount = discountRepository.save(discount);
        return DiscountInListDTO.builder()
                .id(discount.getId())
                .title(discount.getTitle())
                .description(discount.getDescription())
                .promoCode(discount.getPromoCode())
                .build();
    }

    public void delete(Long shopId, Long discountId) throws ObjectNotFoundException, IllegalAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        if (checkClientRights(client.getId(), shopId)) {
            shopRepository.findById(shopId).orElseThrow(() -> new ObjectNotFoundException(shopId, "Магазин"));
            discountRepository.findById(discountId).orElseThrow(() -> new ObjectNotFoundException(discountId, "Предложение"));
            discountRepository.deleteById(discountId);
        } else {
            throw new IllegalAccessException("У пользователя недостаточно прав");
        }
    }


    public boolean checkClientRights(Long clientId, Long shopId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new ObjectNotFoundException(clientId, "Пользователь"));
        shopRepository.findById(shopId).orElseThrow(() -> new ObjectNotFoundException(shopId, "Магазин"));

        return client.getShop() != null && Objects.equals(client.getShop().getId(), shopId);
    }

}
