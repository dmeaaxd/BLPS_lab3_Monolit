package org.example.blps_lab3_monolit.app.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab3_monolit.app.entity.auth.Role;


import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}

