package dev.kelvin.ecommercesite.service;

import dev.kelvin.ecommercesite.dto.CommentDTO;
import dev.kelvin.ecommercesite.exception.ResourceNotFoundException;
import dev.kelvin.ecommercesite.mapper.CommentMapper;
import dev.kelvin.ecommercesite.model.Comment;
import dev.kelvin.ecommercesite.model.Product;
import dev.kelvin.ecommercesite.model.User;
import dev.kelvin.ecommercesite.repositories.CommentRepository;
import dev.kelvin.ecommercesite.repositories.ProductRepository;
import dev.kelvin.ecommercesite.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDTO addComment(CommentDTO commentDTO, Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setProduct(product);
        comment.setUser(user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByProduct(Long productId) {
        List<Comment> commentList = commentRepository.findByProductId(productId);
        return commentList.stream().map(commentMapper::toDTO).toList();
    }
}
