package edu.ptithcm.learnnextbackend.modules.cart;

import edu.ptithcm.learnnextbackend.modules.cart.dto.request.AddToCartRequest;
import edu.ptithcm.learnnextbackend.modules.cart.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse removeFromCart(UUID courseId);

    void clearCart();
}
