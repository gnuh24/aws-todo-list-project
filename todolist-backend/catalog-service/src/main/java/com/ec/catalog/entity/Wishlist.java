package com.ec.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @EmbeddedId
    private WishlistId id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "accountId", referencedColumnName = "id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Product product;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistId implements Serializable {

        @Column(name = "accountId", nullable = false, length = 10)
        private String accountId;

        @Column(name = "productId", nullable = false, length = 10)
        private String productId;
    }
}
