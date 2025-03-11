package dev.kelvin.ecommercesite.service;

import dev.kelvin.ecommercesite.dto.CartDTO;
import dev.kelvin.ecommercesite.dto.OrderDTO;
import dev.kelvin.ecommercesite.exception.InsufficientStockException;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.mapper.CartMapper;
import dev.kelvin.ecommercesite.mapper.OrderMapper;
import dev.kelvin.ecommercesite.model.*;
import dev.kelvin.ecommercesite.repositories.OrderRepository;
import dev.kelvin.ecommercesite.repositories.ProductRepository;
import dev.kelvin.ecommercesite.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;

    @Transactional
    public OrderDTO createOrder(Long userId,String address,String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        if (!user.getEmailConfirmed()){
            throw new IllegalArgumentException("Email not confirmed");
        }

        CartDTO cartDTO = cartService.getCart(userId);
        Cart cart = cartMapper.toEntity(cartDTO);
        if (cart.getItems().isEmpty()){
            throw new ResourceNotFoundException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreateAt(LocalDateTime.now());

        List<OrderItem> orderItems = createOrderItem(cart,order);
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        try {
            emailService.sendOrderConfirmationEmail(savedOrder);
        } catch (Exception e) {
            log.info("fail to send order confirmation email for orderID "+savedOrder.getId(),e);
        }
        return orderMapper.toDTO(order);
    }

    private List<OrderItem> createOrderItem(Cart cart,Order order) {
        return cart.getItems().stream().map(cartItem -> {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(()-> new EntityNotFoundException("Product not found"));
            if (product.getQuantity()==null){
                throw new IllegalArgumentException("Product quantity is null");
            }
            if(product.getQuantity()<cartItem.getQuantity()){
                throw new InsufficientStockException("not enough stock "+product.getQuantity());
            }

            product.setQuantity(product.getQuantity()-cartItem.getQuantity());
            productRepository.save(product);
            return new OrderItem(null,order,product,cartItem.getQuantity(),product.getPrice());
        }).toList();
    }


    public List<OrderDTO> getAllOrders(){
        return orderMapper.toDTOs(orderRepository.findAll());
    }

    public List<OrderDTO> getOrdersByUserId(Long userId){
        return orderMapper.toDTOs(orderRepository.findByUserId(userId));
    }
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus orderStatus){
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        order.setStatus(orderStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }
}
