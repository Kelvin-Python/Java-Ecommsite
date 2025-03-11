package dev.kelvin.ecommercesite.controller;

import dev.kelvin.ecommercesite.dto.CommentDTO;
import dev.kelvin.ecommercesite.model.Comment;
import dev.kelvin.ecommercesite.model.User;
import dev.kelvin.ecommercesite.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> create(@PathVariable Long productId,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          @RequestBody @Valid CommentDTO commentDTO) {
        Long userId = ((User)userDetails).getId();

        return ResponseEntity.ok(commentService.addComment(commentDTO,productId,userId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProduct(productId));
    }
}
