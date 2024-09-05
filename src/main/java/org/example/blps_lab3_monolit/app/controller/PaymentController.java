package org.example.blps_lab3_monolit.app.controller;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.dto.payment.PaymentDTO;
import org.example.blps_lab3_monolit.app.service.PaymentService;
import org.example.blps_lab3_monolit.app.validators.ValidationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<?> getBill() {
        Map<String, String> response = new HashMap<>();
        response.put("bill", String.valueOf(paymentService.getBill()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PutMapping
    public ResponseEntity<?> topUp(@RequestBody PaymentDTO paymentDTO) {
        Map<String, String> response = new HashMap<>();

        ValidationResult validationResult = paymentDTO.validate();
        if (!validationResult.isCorrect()){
            response.put("error", validationResult.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            paymentService.topUp(paymentDTO.getAmount());
            response.put("message", "Запрос на пополнение отправлен. Ожидайте подтверждения на почту");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
