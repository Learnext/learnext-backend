package edu.ptithcm.learnnextbackend.modules.wishlist;

import edu.ptithcm.learnnextbackend.modules.wishlist.dto.WishlistResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/{courseId}")
    public ResponseEntity<WishlistResponse> add(@PathVariable UUID courseId) {
        return ResponseEntity.ok(wishlistService.add(courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> remove(@PathVariable UUID courseId) {
        wishlistService.remove(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<WishlistResponse>> getMyWishlist() {
        return ResponseEntity.ok(wishlistService.getMyWishlist());
    }

    @GetMapping("/check/{courseId}")
    public ResponseEntity<Map<String, Boolean>> check(@PathVariable UUID courseId) {
        return ResponseEntity.ok(Map.of("wishlisted", wishlistService.check(courseId)));
    }
}
