package com.flykraft.menu_service.service;

import com.flykraft.menu_service.dto.MenuItemRequestDto;
import com.flykraft.menu_service.model.MenuItem;

import java.util.List;

public interface MenuService {

    List<MenuItem> getMenuItemsByKitchen(String kitchenSubdomain);

    MenuItem getMenuItemById(Long menuItemId);

    MenuItem addMenuItemToKitchen(Long kitchenId, MenuItemRequestDto menuItemRequestDto);

    MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto);

    MenuItem toggleStatusForMenuItem(Long menuItemId);

    void deleteMenuItemById(Long menuItemId);
}
