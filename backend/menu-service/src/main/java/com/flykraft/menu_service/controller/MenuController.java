package com.flykraft.menu_service.controller;

import com.flykraft.menu_service.dto.MenuItemRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/menus")
public interface MenuController {

    @GetMapping("/{kitchenSubdomain}")
    ResponseEntity<?> getMenuByKitchenSubdomain(@PathVariable String kitchenSubdomain);

    @PostMapping("/kitchen/{kitchenId}")
    ResponseEntity<?> addMenuItemToKitchen(@PathVariable Long kitchenId, @RequestBody MenuItemRequestDto menuItemRequestDto);

    @PutMapping("/{menuItemId}")
    ResponseEntity<?> updateMenuItem(@PathVariable Long menuItemId, @RequestBody MenuItemRequestDto menuItemRequestDto);

    @PatchMapping("/{menuItemId}")
    ResponseEntity<?> toggleInStockForMenuItem(@PathVariable Long menuItemId);

    @DeleteMapping("/{menuItemId}")
    ResponseEntity<?> deleteMenuItem(@PathVariable Long menuItemId);
}
