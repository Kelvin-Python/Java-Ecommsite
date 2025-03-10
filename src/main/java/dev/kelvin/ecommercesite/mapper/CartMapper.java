package dev.kelvin.ecommercesite.mapper;

import dev.kelvin.ecommercesite.dto.CartDTO;
import dev.kelvin.ecommercesite.dto.CartItemDTO;
import dev.kelvin.ecommercesite.model.Cart;
import dev.kelvin.ecommercesite.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {


    @Mapping(target = "userId", source = "user.id")
    CartDTO toDTO(Cart cart);
    @Mapping(target="user.id", source = "userId")
    Cart toEntity(CartDTO cartDTO);

    @Mapping(target="productId", source="product.id")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target="product.id", source="productId")
    CartItem toEntity(CartItemDTO cartItemDTO);
}
