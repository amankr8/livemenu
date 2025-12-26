package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.model.Auditable;
import com.flykraft.livemenu.model.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FilterDef(name = "kitchenFilter", parameters = @ParamDef(name = "kitchenId", type = Long.class))
@Filter(name = "kitchenFilter", condition = "k_id = :kitchenId")
@Entity
@Table(name = "orders")
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "o_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "k_id", nullable = false)
    private Kitchen kitchen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_status")
    private OrderStatus status;

    @Column(name = "o_total_price")
    private BigDecimal totalPrice;

    @Column(name = "o_guest_name")
    private String guestName;

    @Column(name = "o_guest_phone")
    private String guestPhone;

    @Column(name = "o_guest_address")
    private String guestAddress;
}
