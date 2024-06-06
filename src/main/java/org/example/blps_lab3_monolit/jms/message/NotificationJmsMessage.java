package org.example.blps_lab3_monolit.jms.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJmsMessage {
    private String to; // Почта получателя
    private String theme; // Тема письма
    private String text; // Тело сообщения

    public boolean check(){
        return (to != null && !to.isEmpty()) &&
                (theme != null && !theme.isEmpty()) &&
                (text != null && !text.isEmpty());
    }
}
