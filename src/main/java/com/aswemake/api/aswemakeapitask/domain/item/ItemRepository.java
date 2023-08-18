package com.aswemake.api.aswemakeapitask.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i left join fetch i.orderItems oi where i.id = :id")
    Optional<Item> findByIdWithOrderItem(Long id);

    Optional<Item> findByName(String name);
}
