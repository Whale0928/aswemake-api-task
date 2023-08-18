package com.aswemake.api.aswemakeapitask.domain.item;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemHistoryRepository extends JpaRepository<PriceHistory, Long> {
}
