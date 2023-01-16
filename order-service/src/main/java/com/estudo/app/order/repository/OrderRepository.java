package com.estudo.app.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.estudo.app.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
