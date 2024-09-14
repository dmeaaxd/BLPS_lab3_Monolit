package org.example.blps_lab3_monolit.app.service;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.client.ClientDTO;
import org.example.blps_lab3_monolit.app.dto.client.RegisterDTO;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.entity.auth.Role;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.app.repository.RoleRepository;
import org.example.blps_lab3_monolit.jms.message.NotificationJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsNotificationSender;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ClientService {
    private JmsNotificationSender jmsNotificationSender;

    private ClientRepository clientRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public ClientDTO auth(String username, String password) throws Exception{
        if (username == null || username.isEmpty() || password == null || password.isEmpty()){
            throw new Exception("Fields can't be empty");
        }

        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            throw new NoSuchElementException("User " + username + " not found");
        }

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new IllegalAccessException("Incorrect user data");
        }

        return ClientDTO.builder()
                .id(client.getId())
                .username(client.getUsername())
                .email(client.getEmail())
                .roles(client.getRoles())
                .build();
    }

    public ClientDTO register(RegisterDTO registerDTO) throws Exception {
        if (clientRepository.existsByUsername(registerDTO.getUsername())) {
            throw new Exception("Username is already taken!");
        }

        if (registerDTO.antiCheckerRegister()){
            throw new Exception("Fields can't be empty");
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
                .id(resultClient.getId())
                .username(resultClient.getUsername())
                .email(resultClient.getEmail())
                .roles(resultClient.getRoles())
                .build();
    }

    public void requestChangePassword(String username) throws NoSuchElementException{
        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            throw new NoSuchElementException("User " + username + " not found");
        }

        String restorePassword = generateRestorePassword();
        client.setRestorePassword(passwordEncoder.encode(restorePassword));
        clientRepository.save(client);

        jmsNotificationSender.sendNotification(NotificationJmsMessage.builder()
                .to(client.getEmail())
                .theme("Восстановление пароля")
                .text("Код восстановления: " + restorePassword).build());
    }

    private String generateRestorePassword(){
        final int LEN = 10;

        String alphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder s = new StringBuilder(LEN);

        for (int i = 0; i < LEN; i++) {
            int ch = (int)(alphaNumericStr.length() * Math.random());
            s.append(alphaNumericStr.charAt(ch));
        }

        return s.toString();
    }


    public void changePassword(String username, String restorePassword, String newPassword) throws Exception{
        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            throw new NoSuchElementException("User " + username + " not found");
        }

        if (!passwordEncoder.matches(restorePassword, client.getRestorePassword())){
            throw new Exception("Incorrect restore password");
        }

        client.setRestorePassword(null);
        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);
    }


}
