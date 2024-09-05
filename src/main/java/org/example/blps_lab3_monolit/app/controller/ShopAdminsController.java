package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.ShopAdminsDTO;
import org.example.blps_lab3_monolit.app.service.ShopAdminsService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/shops/{id}/admins")
@AllArgsConstructor
public class ShopAdminsController {
    private final ShopAdminsService shopAdminsService;

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getShopAdmins(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            return new ResponseEntity<>(shopAdminsService.getShopAdmins(id), HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            response.put("error", "Такого магазина нет");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PutMapping
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ShopAdminsDTO adminsDTO) {
        List<Long> admins = adminsDTO.getAdmins();

        Map<String, String> response = new HashMap<>();
        try {
            return new ResponseEntity<>(shopAdminsService.updateShopAdmins(id, admins), HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            if (Objects.equals(exception.getEntityName(), "Магазин")) {
                response.put("error", "Такого магазина нет");
            } else {
                response.put("error", "Такого пользователя нет (id=" + exception.getIdentifier() + ")");
            }
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
