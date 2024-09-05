package org.example.blps_lab3_monolit.app.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTORequest;
import org.example.blps_lab3_monolit.app.dto.discounts.DiscountInListDTO;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopGetCurrentViewDTO {
    private Long id;
    private String name;
    private String description;
    private CategoryDTORequest category;
    private List<DiscountInListDTO> discounts;
}
