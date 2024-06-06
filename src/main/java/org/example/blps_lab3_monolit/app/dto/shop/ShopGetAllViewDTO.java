package org.example.blps_lab3_monolit.app.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.category.CategoryDTORequest;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopGetAllViewDTO {
    private Long id;
    private String name;
    private String description;
    private CategoryDTORequest category;
}
