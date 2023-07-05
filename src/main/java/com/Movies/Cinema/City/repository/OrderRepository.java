package com.Movies.Cinema.City.repository;

import com.Movies.Cinema.City.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findOrderByUserId(Long user_id);
}