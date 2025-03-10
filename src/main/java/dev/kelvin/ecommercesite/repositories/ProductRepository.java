package dev.kelvin.ecommercesite.repositories;

import dev.kelvin.ecommercesite.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
