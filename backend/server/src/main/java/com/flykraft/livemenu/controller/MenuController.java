package com.flykraft.livemenu.controller;

import com.flykraft.livemenu.dto.menu.MenuItemRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/menus")
public interface MenuController {

    @GetMapping
    ResponseEntity<?> getMenu();

    @PostMapping
    ResponseEntity<?> addMenuItem(@RequestBody MenuItemRequestDto menuItemRequestDto);

    @PutMapping("/{menuItemId}")
    ResponseEntity<?> updateMenuItem(@PathVariable Long menuItemId, @RequestBody MenuItemRequestDto menuItemRequestDto);

    @PatchMapping("/{menuItemId}")
    ResponseEntity<?> toggleInStockForMenuItem(@PathVariable Long menuItemId);

    @DeleteMapping("/{menuItemId}")
    ResponseEntity<?> deleteMenuItem(@PathVariable Long menuItemId);
}
