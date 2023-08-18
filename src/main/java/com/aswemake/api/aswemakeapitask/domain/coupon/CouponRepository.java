package com.aswemake.api.aswemakeapitask.domain.coupon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query("SELECT c FROM Coupon c LEFT JOIN FETCH c.item i LEFT JOIN FETCH c.orders o WHERE c.id = :id")
    Optional<Coupon> findByIdWithItemAndOrders(Long id);
}
