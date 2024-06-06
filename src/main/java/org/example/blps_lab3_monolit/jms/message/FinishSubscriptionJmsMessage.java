package org.example.blps_lab3_monolit.jms.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinishSubscriptionJmsMessage {
    private String email;
    private Long shopId;
    private int duration;
}
