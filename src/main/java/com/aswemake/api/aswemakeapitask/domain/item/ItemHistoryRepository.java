package com.aswemake.api.aswemakeapitask.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ItemHistoryRepository extends JpaRepository<PriceHistory, Long> {
    Optional<PriceHistory> findTopByItemIdAndChangedDateBeforeOrderByChangedDateDesc(Long itemId, LocalDateTime changedDate);
}
