package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.client.RegisterDTO;
import org.example.blps_lab3_monolit.app.service.ClientService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class ClientController {
    private ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        Map<String, String> response = new HashMap<>();
        try{
            return new ResponseEntity<>(clientService.register(registerDTO), HttpStatus.OK);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PostMapping("set_system_admin")
    public ResponseEntity<?> setAdmin(@RequestParam Long id) {
        Map<String, String> response = new HashMap<>();
        try{
            return new ResponseEntity<>(clientService.setAdmin(id), HttpStatus.OK);
        } catch (ObjectNotFoundException e){
            response.put("error", "Пользователь не найден");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
