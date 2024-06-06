package org.example.blps_lab3_monolit.app.validators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationResult {
    private boolean correct;
    private String message;
}
