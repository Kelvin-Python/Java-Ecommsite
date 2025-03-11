package dev.kelvin.ecommercesite.repositories;

import dev.kelvin.ecommercesite.dto.ProductListDTO;
import dev.kelvin.ecommercesite.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select new dev.kelvin.ecommercesite.dto.ProductListDTO(p.id,p.name,p.description,p.price,p.quantity,p.image) FROM Product p")
    Page<ProductListDTO> findAllWithoutComments(Pageable pageable);
}
