package org.example.blps_lab3_monolit.app.entity.auth;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.blps_lab3_monolit.app.entity.Bill;
import org.example.blps_lab3_monolit.app.entity.Shop;


import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String restorePassword;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billId")
    private Bill accountBill;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // Обозначает магазин, администратором которого он является
}
