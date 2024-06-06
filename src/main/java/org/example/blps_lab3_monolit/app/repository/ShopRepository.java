package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    public final int PAGE_SIZE = 10;
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
