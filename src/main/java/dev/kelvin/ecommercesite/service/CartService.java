package dev.kelvin.ecommercesite.service;

import dev.kelvin.ecommercesite.dto.CartDTO;
import dev.kelvin.ecommercesite.exception.InsufficientResourceException;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.mapper.CartMapper;
import dev.kelvin.ecommercesite.model.Cart;
import dev.kelvin.ecommercesite.model.CartItem;
import dev.kelvin.ecommercesite.model.Product;
import dev.kelvin.ecommercesite.model.User;
import dev.kelvin.ecommercesite.repositories.CartRepository;
import dev.kelvin.ecommercesite.repositories.ProductRepository;
import dev.kelvin.ecommercesite.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartDTO addCart(Long userId,Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(product.getQuantity() < quantity) {
            throw new InsufficientResourceException("Not enough available");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null,user,new ArrayList<>()));

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item ->item.getProduct().getId().equals(productId))
                .findFirst();

        // the product is already in the cart
        if(existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }else {
            CartItem cartItem = new CartItem(null,cart,product,quantity);
            cart.getItems().add(cartItem);
        }
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);

    }

    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartMapper.toDTO(cart);
    }

    public void clearCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }


}
