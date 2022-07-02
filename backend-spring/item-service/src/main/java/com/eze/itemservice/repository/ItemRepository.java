package com.eze.itemservice.repository;

import com.eze.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByItemCodeAndDeleteFlagFalse(String itemCode);

    List<Item> findByDeleteFlagFalse();

    @Query("SELECT i from Item i " +
            "LEFT JOIN i.category c WHERE c.categoryCode=?1")
    List<Item> findItemByCategory(String category);

    @Query("UPDATE Item i SET i.deleteFlag=true WHERE i.itemCode=?1")
    @Modifying
    void softDelete(String itemCode);
}
