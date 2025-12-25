package com.flykraft.livemenu.service;

import com.flykraft.livemenu.dto.menu.MenuItemRequestDto;
import com.flykraft.livemenu.entity.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItem> getAllMenuItems();

    MenuItem getMenuItemById(Long menuItemId);

    MenuItem addMenuItem(MenuItemRequestDto menuItemRequestDto);

    MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto);

    MenuItem toggleInStockForMenuItem(Long menuItemId);

    void deleteMenuItemById(Long menuItemId);
}
