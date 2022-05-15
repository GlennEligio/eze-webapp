package com.eze.itemservice.repository;

import com.eze.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i " +
            "WHERE i.itemCode=?1 " +
            "AND i.deleteFlag=false")
    List<Item> findByItemCode(String itemCode);

    List<Item> findByDeleteFlagFalse();

    @Query("UPDATE Item i SET i.deleteFlag=true WHERE i.itemCode=?1")
    @Modifying
    void softDelete(String itemCode);
}
