package org.example.blps_lab3_monolit.app.service;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.client.ClientDTO;
import org.example.blps_lab3_monolit.app.dto.client.RegisterDTO;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.entity.auth.Role;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.RoleRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class ClientService {

    private ClientRepository clientRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public ClientDTO register(RegisterDTO registerDTO) throws Exception {
        if (clientRepository.existsByUsername(registerDTO.getUsername())) {
            throw new Exception("Username is already taken!");
        }

        if (registerDTO.antiCheckerRegister()){
            throw new IllegalArgumentException("Данные введены некорректно, все поля должны быть заполнены");
        }

        if (clientRepository.existsByEmail(registerDTO.getEmail())) {
            throw new Exception("Email is already taken!");
        }

        Client client = Client.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword())).build();

        Role role = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        client.setRoles(roles);

        Client resultClient = clientRepository.save(client);
        return ClientDTO.builder()
                .username(resultClient.getUsername())
                .email(resultClient.getEmail())
                .roles(resultClient.getRoles())
                .build();
    }


    public ClientDTO setAdmin(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Пользователь"));
        Role role = roleRepository.findByName("SYSTEM_ADMIN");

        Set<Role> roles = client.getRoles();
        roles.add(role);
        client.setRoles(roles);

        client = clientRepository.save(client);
        return ClientDTO.builder()
                .username(client.getUsername())
                .email(client.getEmail())
                .roles(client.getRoles())
                .build();
    }
}
