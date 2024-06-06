package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BillRepository extends JpaRepository<Bill, Long> {
}
