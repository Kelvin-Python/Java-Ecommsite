package dev.kelvin.ecommercesite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "Product desc is required")

    private String description;
    @Positive(message = "price has to be positive")
    private BigDecimal price;
    private Integer quantity;
    private String image;
    private List<CommentDTO> comments;
}
