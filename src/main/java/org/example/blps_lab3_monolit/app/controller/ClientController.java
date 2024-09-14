package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.client.ChangePasswordDTO;
import org.example.blps_lab3_monolit.app.dto.client.RegisterDTO;
import org.example.blps_lab3_monolit.app.dto.client.RequestChangePasswordDTO;
import org.example.blps_lab3_monolit.app.service.ClientService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientController {
    private ClientService clientService;

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestParam String username,
                                  @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        try{
            return new ResponseEntity<>(clientService.auth(username, password), HttpStatus.OK);
        } catch (Exception e){
            if (e instanceof IllegalAccessException){
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            else {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
    }

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

    @PostMapping("/request-change-password")
    public ResponseEntity<?> requestChangePassword(@RequestBody RequestChangePasswordDTO requestChangePasswordDTO){
        Map<String, String> response = new HashMap<>();
        try{
            clientService.requestChangePassword(requestChangePasswordDTO.getUsername());
            response.put("message", "Код для восстановления пароля отправлен на вашу почту");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        Map<String, String> response = new HashMap<>();
        try{
            clientService.changePassword(changePasswordDTO.getUsername(),
                    changePasswordDTO.getRestorePassword(),
                    changePasswordDTO.getNewPassword());
            response.put("message", "Доступ восстановлен, можете использовать новый пароль");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
