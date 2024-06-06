package org.example.blps_lab3_monolit.security.jaas;

import lombok.Setter;
import org.example.blps_lab3_monolit.app.entity.auth.Role;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.springframework.security.authentication.jaas.AuthorityGranter;


import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Setter
public class MyAuthorityGranter implements AuthorityGranter {
    private ClientRepository userRepository;
    @Override
    public Set<String> grant(Principal principal) {
        Set<String> stringRoles = new HashSet<>();
        for (Role role : userRepository.findByUsername(principal.getName()).getRoles()){
            stringRoles.add(role.getName());
        }
        return stringRoles;
    }
}
