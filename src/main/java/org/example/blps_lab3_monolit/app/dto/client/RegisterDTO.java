package org.example.blps_lab3_monolit.app.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String username;
    private String email;
    private String password;

    public boolean antiCheckerRegister() {
        if (username == null || username.isEmpty()) return true;
        if (email == null || email.isEmpty()) return true;
        if (password == null || password.isEmpty()) return true;
        return false;
    }
}
