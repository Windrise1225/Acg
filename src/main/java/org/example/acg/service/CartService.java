package org.example.acg.service;


import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.Resource;
import org.example.acg.entity.Cart;
import org.example.acg.entity.Product;
import org.example.acg.entity.User;
import org.example.acg.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CartService {

    @Resource
    private CartMapper cartMapper;
    @Autowired
    private ProductService productService;

    public List<Cart> list(){
        return cartMapper.list();
    }

    public Cart getByUserIdAndProductId(Integer userId, Integer productId) {
        return cartMapper.getByUserIdAndProductId(userId, productId);
    }

    public void insert(Cart cart){
        cartMapper.insert(cart);
    }

    public void update(Cart cart){
        cartMapper.update(cart);
    }

    public List<Cart> listByUserId(Integer userId){
        return cartMapper.listByUserId(userId);
    }

    public void deleteById(Integer id) {
        cartMapper.deleteById(id);
    }

    public Cart getById(Integer id){
        return cartMapper.getById(id);
    }

    @Transactional
    public String changeQuantity(Integer cartId, int delta) {
        Cart cart = getById(cartId);

        if (cart == null) {
            return "商品记录不存在";
        }

        int newQuantity = cart.getProductQuantity() + delta;

        if (newQuantity <= 0) {
            // 数量小于等于 0，执行删除
            deleteById(cart.getId());
            return "deleted";
        } else {
            // 校验库存 (可选，防止恶意修改)
            Product product = productService.getProductById(cart.getProductId());
            if (newQuantity > product.getQuantity()) {
                return "库存不足，最大可购买：" + product.getQuantity();
            }

            // 更新数量
            cart.setProductQuantity(newQuantity);

            // 更新审计信息
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null) {
                    cart.setLastModifiedUserName(currentUser.getName());
                }
            }
            cart.setLastModifiedTime(ZonedDateTime.now());

            update(cart);
            return "success";
        }
    }


    public String addToCart(User user, Integer productId){

        Product product = productService.getProductById(productId);

        Cart cart = getByUserIdAndProductId(user.getId(), productId);
        if (cart != null){
            if (cart.getProductQuantity() + 1 > product.getQuantity()){
                return "商品库存不足";
            }
            cart.setProductQuantity(cart.getProductQuantity() + 1);
            User currentUser = (User)VaadinSession.getCurrent().getAttribute("user");
            cart.setLastModifiedUserName(currentUser.getName());
            cart.setLastModifiedTime(ZonedDateTime.now());
            update( cart);
        }else {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart.setProductId(productId);
            cart.setProductQuantity(1);
            cart.setProductPrice(product.getPrice());
            cart.setCreateTime(ZonedDateTime.now());
            insert( cart);
        }

        return "success";
    }
}
