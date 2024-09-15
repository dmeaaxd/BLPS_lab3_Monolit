package org.example.blps_lab3_monolit.app.controller;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.FavoriteDTO;
import org.example.blps_lab3_monolit.app.dto.FavoritesRequestDTO;
import org.example.blps_lab3_monolit.app.entity.Favorite;
import org.example.blps_lab3_monolit.app.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAll() {
        return new ResponseEntity<>(favoriteService.getFavorites(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/by-shop-id/{shopId}")
    public ResponseEntity<?> getByShopId(@PathVariable Long shopId) {
        try{
            return new ResponseEntity<>(favoriteService.getFavoriteByShopId(shopId), HttpStatus.OK);
        } catch (Exception e){
            Map<String, String> response = new HashMap<>();
            response.put("message", "Favorite not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    public ResponseEntity<?> addToFavorite(@RequestBody FavoritesRequestDTO favoritesRequestDTO) {
        Long shopId = favoritesRequestDTO.getShopId();

        try {
            return new ResponseEntity<>(favoriteService.add(shopId), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Магазин " + shopId + " не найден");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }


    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeToFavorite(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        try {
            response.put("favoriteId", String.valueOf(favoriteService.remove(id)));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", "Объекта Избранное с данным ID не существует: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}