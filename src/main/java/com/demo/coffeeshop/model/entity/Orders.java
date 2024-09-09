package com.demo.coffeeshop.model.entity;

import com.demo.coffeeshop.model.entity.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "BINARY(36)")
    private UUID orderId;

    @Column(length = 50)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String postcode;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItems> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(length = 6)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(length = 6)
    private LocalDateTime updatedAt;

    public Orders(String email, String address, String postcode) {
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.orderStatus = OrderStatus.PREPARING;
    }

    public static Orders createOrder(String email, String address, String postcode, List<OrderItems> orderItems) {
        Orders order = new Orders(email,address,postcode);
        for (OrderItems orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    public void addOrderItem(OrderItems orderItems){
        this.orderItems.add(orderItems);
        orderItems.setOrders(this);
    }

    public void changeInfo(String address, String postcode){
        this.address = address;
        this.postcode = postcode;
    }

    public void changeStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
    
    public void cancel(){
        if(this.orderStatus == OrderStatus.DELIVERY || this.orderStatus == OrderStatus.CANCEL)
            throw new IllegalStateException("Already Delivered");
        this.orderStatus = OrderStatus.CANCEL;
    }
}
