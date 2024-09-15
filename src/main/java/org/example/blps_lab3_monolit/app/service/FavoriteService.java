package org.example.blps_lab3_monolit.app.service;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.FavoriteDTO;
import org.example.blps_lab3_monolit.app.entity.Favorite;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.FavoriteRepository;
import org.example.blps_lab3_monolit.app.repository.ShopRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ClientRepository clientRepository;
    private final ShopRepository shopRepository;

    public FavoriteDTO add(Long shopId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Long clientId = client.getId();

        Favorite favorite = favoriteRepository.findByClientIdAndShopId(clientId, shopId);
        if (favorite == null) {
            Optional<Shop> optionalShop = shopRepository.findById(shopId);
            if (optionalShop.isEmpty()) {
                throw new Exception("Магазин: " + shopId + " не найден");
            }

            favorite = favoriteRepository.save(Favorite.builder()
                    .client(client)
                    .shop(optionalShop.get())
                    .build());
        }

        return FavoriteDTO.builder()
                .id(favorite.getId())
                .shopId(favorite.getShop().getId())
                .shopName(favorite.getShop().getName())
                .build();
    }


    public List<FavoriteDTO> getFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        List<Favorite> favoriteList = favoriteRepository.findAllByClientId(client.getId());
        List<FavoriteDTO> favoriteDTOList = new ArrayList<>();
        for (Favorite favorite : favoriteList) {
            favoriteDTOList.add(FavoriteDTO.builder()
                            .id(favorite.getId())
                            .shopId(favorite.getShop().getId())
                            .shopName(favorite.getShop().getName())
                            .build());
        }

        return favoriteDTOList;
    }

    public FavoriteDTO getFavoriteByShopId(Long shopId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        Favorite favorite = favoriteRepository.findByClientIdAndShopId(client.getId(), shopId);
        if (favorite == null) {
            throw new Exception("Favorite not found");
        }

        return FavoriteDTO.builder()
                .id(favorite.getId())
                .shopId(favorite.getShop().getId())
                .shopName(favorite.getShop().getName())
                .build();
    }


    public String remove(Long favoriteId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);

        Favorite favorite = favoriteRepository.findById(favoriteId).orElseThrow(() -> new ObjectNotFoundException(favoriteId, "Магазин"));
        List<Favorite> favoriteList = favoriteRepository.findAllByClientId(client.getId());
        if (!favoriteList.contains(favorite)) {
            throw new Exception("У вас нет прав взаимодействовать с данным магазином");
        }

        favoriteRepository.deleteById(favoriteId);
        return "Магазин удален из избранного";
    }


}
