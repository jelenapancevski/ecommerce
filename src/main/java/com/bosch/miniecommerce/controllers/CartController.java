package com.bosch.miniecommerce.controllers;

import com.bosch.miniecommerce.dto.AddToCartObject;
import com.bosch.miniecommerce.dto.UpdateCartItemObject;
import com.bosch.miniecommerce.entities.CartItem;
import com.bosch.miniecommerce.entities.Product;
import com.bosch.miniecommerce.entities.User;
import com.bosch.miniecommerce.exceptions.ResourceNotFoundException;
import com.bosch.miniecommerce.repositories.CartItemRepository;
import com.bosch.miniecommerce.repositories.ProductRepository;
import com.bosch.miniecommerce.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class CartController {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Add item to cart
    @PostMapping("/api/cart/add")
    public ResponseEntity add(@Valid @RequestBody AddToCartObject request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(()-> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        CartItem cartItem;
        if (item.isPresent()){
            cartItem = item.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }
        else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        cartItem = cartItemRepository.save(cartItem);
        return ResponseEntity
                .created(URI.create("/api/cart/item/" + cartItem.getId()))
                .body(cartItem);
    }

    // Get cart contents
    @GetMapping("/api/cart")
    public ResponseEntity<List<CartItem>> cart(@AuthenticationPrincipal Jwt jwt)
    {   Long userId = jwt.getClaim("id");
        return ResponseEntity.ok(cartItemRepository.findByUserId(userId));
    }


    // Update cart item quantity
    @PutMapping("/api/cart/item/{id}")
    public ResponseEntity<?> item(@PathVariable Long id, @Valid @RequestBody UpdateCartItemObject request, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Item not found"));

        if (!item.getUser().getId().equals(userId)){
            throw new AccessDeniedException("User not the owner of selected cart item");
        }

        //Optional<CartItem> item = cartItemRepository.findByUserIdAndProductId(userId, id);

        if (request.getQuantity()<=0){
            //delete the item in cart
            cartItemRepository.delete(item);
            return ResponseEntity.noContent().build();
        }
        else {
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
            return ResponseEntity.ok(item);
        }
    }

    //Remove item from cart
    @DeleteMapping("/api/cart/item/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        Long userId = jwt.getClaim("id");
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Item not found"));
        if (!item.getUser().getId().equals(userId)){
            throw new AccessDeniedException("User not the owner of selected cart item");

        }
        //Optional<CartItem> item = cartItemRepository.findByUserIdAndProductId(userId, id);
        cartItemRepository.delete(item);
        return ResponseEntity.noContent().build();
    }


}
