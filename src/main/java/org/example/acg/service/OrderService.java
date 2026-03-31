package org.example.acg.service;

import jakarta.annotation.Resource;
import org.example.acg.config.enums.OrderStatusEnum;
import org.example.acg.entity.Cart;
import org.example.acg.entity.DailySequence;
import org.example.acg.entity.Order;
import org.example.acg.entity.User;
import org.example.acg.mapper.DailySequenceMapper;
import org.example.acg.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private DailySequenceMapper sequenceMapper;

    @Resource
    private ProductService productService;

    @Resource
    private CartService cartService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<Order> list() {
        return orderMapper.list();
    }

    /**
     * 处理结算全流程
     * @param user 用户
     * @param cartList 购物车列表
     * @param totalAmount 总金额
     * @return 生成的订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public String processCheckout(User user, List<Cart> cartList, java.math.BigDecimal totalAmount) {
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("购物车为空，无法结算");
        }

        String orderNo = generateOrderNo();

        Order order = new Order();
        order.setOrderNumber(orderNo);
        order.setUserId(user.getId());
        order.setSumPrice(totalAmount);
        order.setStatus(OrderStatusEnum.PENDING_DELIVERY.getCode());
        order.setRemark("系统自动生成");

        ZonedDateTime now = ZonedDateTime.now();
        order.setCreateTime(now);
        order.setLastModifiedTime(now);
        order.setCreateUserName(user.getName());
        order.setLastModifiedUserName(user.getName());

        orderMapper.insert(order);

        for (Cart cart : cartList) {
            productService.decreaseStock(cart.getProductId(), cart.getProductQuantity());
        }

        cartService.clearCart(user.getId());

        return orderNo;
    }

    /**
     * 生成订单号：yyyyMMdd + 4位递增序号 (每日重置)
     * 使用 CAS (Compare And Swap) 机制保证并发安全
     */
    private String generateOrderNo() {
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DATE_FORMAT);

        int maxRetries = 10;
        int retries = 0;

        while (retries < maxRetries) {
            DailySequence seq = sequenceMapper.findByDateKey(today);

            int nextSeq;
            if (seq == null) {
                nextSeq = 1;
                try {
                    DailySequence newSeq = new DailySequence();
                    newSeq.setDateKey(today);
                    newSeq.setCurrentSeq(1);
                    sequenceMapper.insert(newSeq);
                    return dateStr + String.format("%04d", nextSeq);
                } catch (Exception e) {
                    retries++;
                    continue;
                }
            } else {
                nextSeq = seq.getCurrentSeq() + 1;

                int rowsAffected = sequenceMapper.updateSeq(today, seq.getCurrentSeq(), nextSeq);

                if (rowsAffected > 0) {
                    return dateStr + String.format("%04d", nextSeq);
                } else {
                    retries++;
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }
            }
        }
        throw new RuntimeException("生成订单号失败，并发冲突过多");
    }

    public List<Order> listByUserId(Integer userId) {
        return orderMapper.listByUserId(userId);
    }

    public List<Order> likeByOrderNumber(String orderNumber) {
        return orderMapper.likeByOrderNumber(orderNumber);
    }

    public void updateStatus(Order order) {
        orderMapper.updateStatus(order);
    }
}