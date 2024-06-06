package org.example.blps_lab3_monolit.app.service;


import lombok.AllArgsConstructor;
import org.example.blps_lab3_monolit.app.entity.Bill;
import org.example.blps_lab3_monolit.app.entity.auth.Client;
import org.example.blps_lab3_monolit.app.repository.BillRepository;
import org.example.blps_lab3_monolit.app.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final ClientRepository clientRepository;

    public int getBill() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();
        if (bill == null){
            throw new Exception("Для данного пользователя нет созданного счета");
        }
        return bill.getAccountBill();
    }


    public int topUp(int amount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Client client = clientRepository.findByUsername(username);
        Bill bill = client.getAccountBill();
        // Если счет не существует, создаем новую запись
        if (bill == null) {
            bill = new Bill();
            client.setAccountBill(bill);
            bill.setAccountBill(amount);
        } else {
            int currentBalance = bill.getAccountBill();
            bill.setAccountBill(currentBalance + amount);
        }
        billRepository.save(bill);
        return bill.getAccountBill();
    }


}
