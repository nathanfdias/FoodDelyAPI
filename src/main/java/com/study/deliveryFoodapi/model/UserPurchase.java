package com.study.deliveryFoodapi.model;

import java.time.LocalDateTime;
import java.util.List;

import com.study.deliveryFoodapi.Enums.EStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_purchase")
public class UserPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // @ManyToOne
    // @JoinColumn(name = "purchase_id", referencedColumnName = "id")
    @OneToMany(mappedBy= "userPurchase",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Purchase> purchases;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_status", nullable = false)
    private EStatus purchaseStatus;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
}
