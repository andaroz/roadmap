package com.roadmap.repositories;

import com.roadmap.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM ITEM WHERE type = :type")
    List<Item> getAllItemsByType(@Param("type") String type);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE Item i SET i.amount_available = :amount WHERE i.id = :id")
    void updateAvailableAmount(@Param("amount") double amount, @Param("id") Long id);
}
