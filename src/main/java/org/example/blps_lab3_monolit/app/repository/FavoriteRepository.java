package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.dto.FavoriteDTO;
import org.example.blps_lab3_monolit.app.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByClientIdAndShopId(Long clientId, Long shopId);

    List<Favorite> findAllByClientId(Long clientId);

    List<Favorite> findAllByShopId(Long shopId);

    Favorite findByClientIdAndShopId(Long clientId, Long shopId);
}
