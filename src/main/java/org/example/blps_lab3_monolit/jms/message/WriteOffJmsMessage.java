package org.example.blps_lab3_monolit.jms.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffJmsMessage {
    private String email;
    private Long billId;
    private Long shopId;
    private int duration;
    private int amount;
}
