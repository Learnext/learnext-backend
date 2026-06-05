package edu.ptithcm.learnnextbackend.modules.cart;

import edu.ptithcm.learnnextbackend.modules.cart.dto.request.AddToCartRequest;
import edu.ptithcm.learnnextbackend.modules.cart.dto.response.CartResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody @Valid AddToCartRequest request
    ) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable UUID courseId
    ) {
        return ResponseEntity.ok(cartService.removeFromCart(courseId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
