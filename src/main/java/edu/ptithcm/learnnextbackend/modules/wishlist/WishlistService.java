package edu.ptithcm.learnnextbackend.modules.wishlist;

import edu.ptithcm.learnnextbackend.modules.wishlist.dto.WishlistResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    WishlistResponse add(UUID courseId);
    void remove(UUID courseId);
    List<WishlistResponse> getMyWishlist();
    boolean check(UUID courseId);

}
