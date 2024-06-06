package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
