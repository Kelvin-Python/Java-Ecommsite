package dev.kelvin.ecommercesite.service;

import cn.hutool.core.util.ObjectUtil;
import dev.kelvin.ecommercesite.dto.ProductDTO;
import dev.kelvin.ecommercesite.dto.ProductListDTO;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.mapper.ProductMapper;
import dev.kelvin.ecommercesite.model.Product;
import dev.kelvin.ecommercesite.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final String UPLOAD_DIR = "src/main/resources/images/";

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        Product product = productMapper.toEntity(productDTO);
        if (ObjectUtil.isNotEmpty(image)) {
            String fileName = saveImage(image);
            product.setImage("/images/"+fileName);
        }
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long it,ProductDTO productDTO,MultipartFile image) throws IOException {
        Product existingProduct = productRepository.findById(it)
                .orElseThrow(()->new ResourceNotFoundException("Product Not Found"));

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());
        if (ObjectUtil.isNotEmpty(image)) {
            String fileName = saveImage(image);
            existingProduct.setImage("/images/"+fileName);
        }
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long it) {
        productRepository.deleteById(it);
    }

    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product Not Found"));
        return productMapper.toDTO(product);
    }

    public Page<ProductListDTO> getAllProducts(Pageable pagebale) {
        return productRepository.findAllWithoutComments(pagebale);
    }
    private String saveImage(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString()+"_"+image.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR+fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return fileName;
    }
}
