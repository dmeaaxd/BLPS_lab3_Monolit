package org.example.blps_lab3_monolit.app.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangePasswordDTO {
    private String username;
    private String pass1;
    private String pass2;

    public boolean antiChecker(){
        return username == null || username.isEmpty();
    }
}
