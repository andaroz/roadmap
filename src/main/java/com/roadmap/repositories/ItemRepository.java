package com.roadmap.repositories;

import com.roadmap.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM ITEM WHERE type = :type")
    List<Item> getAllItemsByType(@Param("type") String type);
}
