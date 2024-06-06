package org.example.blps_lab3_monolit.app.repository;

import org.example.blps_lab3_monolit.app.entity.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleUser);
}

