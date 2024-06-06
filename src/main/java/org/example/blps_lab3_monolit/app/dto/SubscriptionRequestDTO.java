package org.example.blps_lab3_monolit.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {
    private Long shopId;
    private int duration;

    public boolean antiChecker() {
        if (shopId == null ) return true;
        if (duration <= 0) return true;
        return false;
    }

}
