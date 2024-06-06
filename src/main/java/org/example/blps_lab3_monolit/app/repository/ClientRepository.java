package org.example.blps_lab3_monolit.app.repository;


import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByUsername(String username);
    Client findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
