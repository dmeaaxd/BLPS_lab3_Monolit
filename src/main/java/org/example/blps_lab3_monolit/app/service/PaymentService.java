package org.example.blps_lab3_monolit.app.service;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.entity.Bill;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.BillRepository;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.example.blps_lab3_monolit.jms.message.TopUpJmsMessage;
import org.example.blps_lab3_monolit.jms.sender.JmsPaymentSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PaymentService {

    private final BillRepository billRepository;
    private final ClientRepository clientRepository;
    private final JmsPaymentSender jmsPaymentSender;

    public int getBill() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();
        if (bill == null){
            bill = new Bill();
            client.setAccountBill(bill);
            bill = billRepository.save(bill);
        }
        return bill.getAccountBill();
    }

    public int getBillByEmail(String email) {
        Client client = clientRepository.findByEmail(email);
        Bill bill = client.getAccountBill();
        return bill.getAccountBill();
    }


    public void topUp(int amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();
        // Если счет не существует, создаем новую запись
        if (bill == null) {
            bill = new Bill();
            client.setAccountBill(bill);
            bill = billRepository.save(bill);
        }

        jmsPaymentSender.sendTopUp(TopUpJmsMessage.builder()
                .email(client.getEmail())
                .billId(bill.getId())
                .amount(amount)
                .build());
    }
}
