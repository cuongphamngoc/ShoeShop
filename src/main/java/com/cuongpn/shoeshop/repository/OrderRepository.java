package com.cuongpn.shoeshop.repository;

import com.cuongpn.shoeshop.entity.Order;
import com.cuongpn.shoeshop.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    @NonNull
    @EntityGraph(value = "order.user",type = EntityGraph.EntityGraphType.FETCH)
    Optional<Order> findById(@NonNull Long id);
    @EntityGraph(value = "order.detail",type = EntityGraph.EntityGraphType.FETCH)

    List<Order> findByUser(User user);
}
