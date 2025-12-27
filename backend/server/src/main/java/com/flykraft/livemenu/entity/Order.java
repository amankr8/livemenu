package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.order.OrderResponseDto;
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
import java.util.List;

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
    @JoinColumn(name = "u_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_status", nullable = false)
    private OrderStatus status;

    @Column(name = "o_total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public OrderResponseDto toResponseDto() {
        return OrderResponseDto.builder()
                .id(this.id)
                .kitchenId(this.kitchen.getId())
                .customerDetails(this.user.toResponseDto())
                .status(this.status)
                .totalPrice(this.totalPrice)
                .orderItems(this.orderItems.stream()
                        .map(OrderItem::toResponseDto)
                        .toList())
                .build();
    }
}
