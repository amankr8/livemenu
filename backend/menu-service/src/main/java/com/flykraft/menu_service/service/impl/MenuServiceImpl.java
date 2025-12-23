package com.flykraft.menu_service.service.impl;

import com.flykraft.menu_service.dto.MenuItemRequestDto;
import com.flykraft.menu_service.model.Kitchen;
import com.flykraft.menu_service.model.MenuItem;
import com.flykraft.menu_service.repository.MenuItemRepository;
import com.flykraft.menu_service.service.KitchenService;
import com.flykraft.menu_service.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final KitchenService kitchenService;

    @Override
    public List<MenuItem> getMenuItemsByKitchen(String kitchenSubdomain) {
        Kitchen kitchen = kitchenService.getKitchenBySubdomain(kitchenSubdomain);
        return menuItemRepository.findAllByKitchenId(kitchen.getId());
    }

    @Override
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId).orElseThrow();
    }

    @Override
    public MenuItem addMenuItemToKitchen(Long kitchenId, MenuItemRequestDto menuItemRequestDto) {
        Kitchen kitchen = kitchenService.getKitchenById(kitchenId);
        MenuItem menuItem = MenuItem.builder()
                .kitchen(kitchen)
                .name(menuItemRequestDto.getName())
                .desc(menuItemRequestDto.getDesc())
                .category(menuItemRequestDto.getCategory())
                .inStock(Boolean.TRUE)
                .price(menuItemRequestDto.getPrice())
                .build();
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        MenuItem selectedMenuItem = getMenuItemById(menuItemId);
        selectedMenuItem.setName(menuItemRequestDto.getName());
        selectedMenuItem.setDesc(menuItemRequestDto.getDesc());
        selectedMenuItem.setCategory(menuItemRequestDto.getCategory());
        selectedMenuItem.setPrice(menuItemRequestDto.getPrice());
        return menuItemRepository.save(selectedMenuItem);
    }

    @Override
    public MenuItem toggleStatusForMenuItem(Long menuItemId) {
        MenuItem menuItem = getMenuItemById(menuItemId);
        menuItem.setInStock(!menuItem.getInStock());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItemById(Long menuItemId) {
        menuItemRepository.deleteById(menuItemId);
    }
}
