package org.example.blps_lab3_monolit.app.service;

import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTORequest;
import org.example.blps_lab3_monolit.app.dto.discounts.DiscountInListDTO;
import org.example.blps_lab3_monolit.app.dto.shop.ShopDTO;
import org.example.blps_lab3_monolit.app.dto.shop.ShopGetAllViewDTO;
import org.example.blps_lab3_monolit.app.dto.shop.ShopGetCurrentViewDTO;
import org.example.blps_lab3_monolit.app.entity.Category;
import org.example.blps_lab3_monolit.app.entity.Discount;
import org.example.blps_lab3_monolit.app.entity.Shop;
import org.example.blps_lab3_monolit.app.repository.*;
import org.example.blps_lab3_monolit.app.utils.ShopComparator;
import org.example.blps_lab3_monolit.app.utils.Sort;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ClientRepository clientRepository;
    private final DiscountRepository discountRepository;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final SubscriptionRepository subscriptionRepository;


    public List<ShopGetAllViewDTO> getAll(List<Integer> categories, String sortString, Integer page) {
        List<ShopGetAllViewDTO> shopGetAllViewDTOList = new ArrayList<>();
        List<Shop> shops = shopRepository.findAll();

        // Фильтрация магазинов
        List<Category> allowCategories = new ArrayList<>();
        if (categories != null) {
            for (int categoryId : categories) {
                allowCategories.add(categoryRepository.findById((long) categoryId).orElseThrow(() -> new ObjectNotFoundException(categoryId, "Категория")));
            }
        }

        List<Shop> filteredShops = new ArrayList<>();
        for (Shop shop : shops) {
            if (allowCategories.isEmpty() || allowCategories.contains(shop.getCategory())) {
                filteredShops.add(shop);
            }
        }

        // Сортировка магазинов
        if (sortString != null) {
            Sort sort = Sort.parseSort(sortString);
            if (sort == Sort.ASC) {
                filteredShops.sort(new ShopComparator());
            } else {
                if (sort == Sort.DESC) {
                    filteredShops.sort(new ShopComparator());
                    Collections.reverse(filteredShops);
                }
            }
        }


        // Пагинация
        int min_index = 0;
        int max_index = filteredShops.size();
        if (page != null) {
            min_index = shopRepository.PAGE_SIZE * (page - 1);
            max_index = Math.min(shopRepository.PAGE_SIZE * page, filteredShops.size());
        }

        List<Shop> resultShops = new ArrayList<>();
        for (int i = min_index; i < max_index; i++) {
            resultShops.add(filteredShops.get(i));
        }


        // Вывод в виде DTO
        for (Shop shop : resultShops) {
            Category category = shop.getCategory();

            CategoryDTORequest categoryDTORequest = CategoryDTORequest.builder()
                    .name(category.getName())
                    .build();

            shopGetAllViewDTOList.add(ShopGetAllViewDTO.builder()
                    .id(shop.getId())
                    .name(shop.getName())
                    .description(shop.getDescription())
                    .category(categoryDTORequest)
                    .build());
        }
        return shopGetAllViewDTOList;
    }

    @Transactional
    public ShopGetCurrentViewDTO getCurrent(Long id) throws ObjectNotFoundException {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Магазин"));

        List<Discount> discounts = shop.getDiscounts();
        List<DiscountInListDTO> discountDTOList = new ArrayList<>();
        for (Discount discount : discounts) {
            discountDTOList.add(DiscountInListDTO.builder()
                    .id(discount.getId())
                    .title(discount.getTitle())
                    .description(discount.getDescription())
                    .promoCode(discount.getPromoCode())
                    .build());
        }

        Category category = shop.getCategory();
        CategoryDTORequest categoryDTORequest = CategoryDTORequest.builder()
                .name(category.getName())
                .build();

        return ShopGetCurrentViewDTO.builder()
                .id(shop.getId())
                .name(shop.getName())
                .description(shop.getDescription())
                .category(categoryDTORequest)
                .discounts(discountDTOList)
                .build();
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, ObjectNotFoundException.class})
    public Shop create(ShopDTO shopDTO) throws ObjectNotFoundException, IllegalArgumentException {

        if (shopRepository.existsByName(shopDTO.getName())) {
            throw new IllegalArgumentException("Магазин с таким именем уже существует");
        }

        Category category = categoryRepository.findById(shopDTO.getCategoryId())
                .orElseThrow(() -> new ObjectNotFoundException(shopDTO.getCategoryId(), "Категория"));

        Shop newShop = Shop.builder()
                .name(shopDTO.getName())
                .description(shopDTO.getDescription())
                .category(category)
                .build();

        return shopRepository.save(newShop);
    }

    @Transactional(rollbackFor = ObjectNotFoundException.class)
    public Shop update(Long id, ShopDTO shopDTO) throws ObjectNotFoundException {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Магазин"));

        if (shopRepository.existsByNameAndIdNot(shopDTO.getName(), id)) {
            throw new IllegalArgumentException("Магазин с таким именем уже существует");
        }

        shop.setName(shopDTO.getName());
        shop.setDescription(shopDTO.getDescription());
        shop.setCategory(categoryRepository.findById(shopDTO.getCategoryId()).orElseThrow(() -> new ObjectNotFoundException(shopDTO.getCategoryId(), "Категория")));

        return shopRepository.save(shop);
    }

    @Transactional(rollbackFor = ObjectNotFoundException.class)
    public String delete(Long id) throws Exception {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Магазин"));
        try {
            clientRepository.deleteAll(shop.getAdmins());
            discountRepository.deleteAll(shop.getDiscounts());
            favoriteRepository.deleteAll(favoriteRepository.findAllByShopId(shop.getId()));
            subscriptionRepository.deleteAll(subscriptionRepository.findAllByShopId(shop.getId()));
        } catch (Exception e) {
            throw new Exception("Админы или Предложения не найдены");
        }

        shopRepository.deleteById(id);
        return "deleted";
    }
}
