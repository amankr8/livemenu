package com.flykraft.menu_service.controller.impl;

import com.flykraft.menu_service.controller.MenuController;
import com.flykraft.menu_service.dto.MenuItemRequestDto;
import com.flykraft.menu_service.dto.MenuItemResponseDto;
import com.flykraft.menu_service.model.MenuItem;
import com.flykraft.menu_service.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MenuControllerImpl implements MenuController {
    private final MenuService menuService;

    @Override
    public ResponseEntity<?> getMenuByKitchenSubdomain(String kitchenSubdomain) {
        List<MenuItemResponseDto> menuItemsByKitchen = menuService.getMenuItemsByKitchen(kitchenSubdomain)
                .stream().map(MenuItem::toResponseDto).toList();
        return ResponseEntity.ok().body(menuItemsByKitchen);
    }

    @Override
    public ResponseEntity<?> addMenuItemToKitchen(Long kitchenId, MenuItemRequestDto menuItemRequestDto) {
        return ResponseEntity.ok().body(menuService.addMenuItemToKitchen(kitchenId, menuItemRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        return ResponseEntity.ok().body(menuService.updateMenuItem(menuItemId, menuItemRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> toggleStatusForMenuItem(Long menuItemId) {
        return ResponseEntity.ok().body(menuService.toggleStatusForMenuItem(menuItemId).toResponseDto());
    }

    @Override
    public ResponseEntity<?> deleteMenuItem(Long menuItemId) {
        menuService.deleteMenuItemById(menuItemId);
        return ResponseEntity.ok().build();
    }
}
