package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.shop.ShopDTO;
import org.example.blps_lab3_monolit.app.service.ShopService;
import org.example.blps_lab3_monolit.app.validators.ShopValidator;
import org.example.blps_lab3_monolit.app.validators.ValidationResult;
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
@RequestMapping("/shops")
@AllArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopValidator shopValidator;

    // URL запроса: shop?categories=1,2,3&sort=asc|desc&page=10
    // page нумеруется, начиная с 1. Если page не задан, выведутся все значения
    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) List<Integer> categories,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false) String page) {

        Map<String, String> response = new HashMap<>();
        Integer intPage;
        try {
            intPage = (page == null) ? null : Integer.parseInt(page);
        } catch (NumberFormatException numberFormatException){
            response.put("error", "Параметр page задан неверно");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        ValidationResult validationResult = shopValidator.validateGetAllRequest(categories, sort, intPage);
        if (!validationResult.isCorrect()){
            if (Objects.equals(validationResult.getMessage(), HttpStatus.NOT_FOUND.toString())) {
                response.put("error", "Данная страница не найдена");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            else {
                response.put("error", validationResult.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(shopService.getAll(categories, sort, intPage), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCurrent(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            return new ResponseEntity<>(shopService.getCurrent(id), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            response.put("error", "Такого магазина нет");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShopDTO shopDTO) {
        Map<String, String> response = new HashMap<>();
        if (shopDTO.antiCheckerRegister()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            response.put("id", String.valueOf(shopService.create(shopDTO).getId()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            response.put("error", "Такой категории нет (id=" + exception.getIdentifier() + ")");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException){
            response.put("error", illegalArgumentException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ShopDTO shopDTO) {

        Map<String, String> response = new HashMap<>();

        if (shopDTO.antiCheckerRegister()) {
            response.put("error", "Переданы неверные параметры в запросе");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            response.put("id", String.valueOf(shopService.update(id, shopDTO).getId()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            response.put("error", "Объекта " + exception.getEntityName() + "с идентификатором " + exception.getIdentifier() + " нет");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException){
            response.put("error", illegalArgumentException.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            shopService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            response.put("error", "Такого магазина нет");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


}