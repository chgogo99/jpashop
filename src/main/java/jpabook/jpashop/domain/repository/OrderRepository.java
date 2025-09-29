package jpabook.jpashop.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch ordersearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (ordersearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(ordersearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql+= " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (ordersearch.getOrderStatus() != null) {
            query = query.setParameter("status", ordersearch.getOrderStatus());
        }
        if (StringUtils.hasText(ordersearch.getMemberName())) {
            query = query.setParameter("name", ordersearch.getMemberName());
        }
        return query.getResultList();
    }
}
