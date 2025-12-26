package com.flykraft.livemenu.controller.impl;

import com.flykraft.livemenu.controller.MenuController;
import com.flykraft.livemenu.dto.menu.MenuItemRequestDto;
import com.flykraft.livemenu.dto.menu.MenuItemResponseDto;
import com.flykraft.livemenu.entity.MenuItem;
import com.flykraft.livemenu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MenuControllerImpl implements MenuController {
    private final MenuService menuService;

    @Override
    public ResponseEntity<?> getMenu() {
        List<MenuItemResponseDto> menuItems = menuService.loadAllMenuItems()
                .stream().map(MenuItem::toResponseDto).toList();
        return ResponseEntity.ok().body(menuItems);
    }

    @Override
    public ResponseEntity<?> addMenuItem(MenuItemRequestDto menuItemRequestDto) {
        return ResponseEntity.ok().body(menuService.addMenuItem(menuItemRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        return ResponseEntity.ok().body(menuService.updateMenuItem(menuItemId, menuItemRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> toggleInStockForMenuItem(Long menuItemId) {
        return ResponseEntity.ok().body(menuService.toggleInStockForMenuItem(menuItemId).toResponseDto());
    }

    @Override
    public ResponseEntity<?> deleteMenuItem(Long menuItemId) {
        menuService.deleteMenuItemById(menuItemId);
        return ResponseEntity.ok().build();
    }
}
