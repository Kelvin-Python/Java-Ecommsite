package dev.kelvin.ecommercesite.mapper;

import dev.kelvin.ecommercesite.dto.CommentDTO;
import dev.kelvin.ecommercesite.dto.ProductDTO;
import dev.kelvin.ecommercesite.model.Comment;
import dev.kelvin.ecommercesite.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", source = "image") //add mapping
    ProductDTO toDTO(Product product);

    @Mapping(target = "image", source = "image") //add mapping
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "userId",source = "user.id")
    CommentDTO toDTO(Comment comment);
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product", ignore = true)
    Comment toEntity(CommentDTO commentDTO);
}
