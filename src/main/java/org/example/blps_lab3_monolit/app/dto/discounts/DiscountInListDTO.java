package org.example.blps_lab3_monolit.app.dto.discounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountInListDTO {
    Long id;
    String title;
    String description;
    String promoCode;
}
