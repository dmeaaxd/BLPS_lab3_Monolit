package org.example.blps_lab3_monolit.app.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDTO {
    private String name;
    private String description;
    private Long categoryId;

    public boolean antiCheckerRegister() {
        if (name == null || name.isEmpty()) return true;
        if (description == null || description.isEmpty()) return true;
        if (categoryId == null) return true;
        return false;
    }
}
