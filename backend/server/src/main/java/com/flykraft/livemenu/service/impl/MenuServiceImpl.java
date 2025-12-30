package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.menu.MenuItemRequestDto;
import com.flykraft.livemenu.entity.DishImage;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.entity.MenuItem;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.model.CloudinaryFile;
import com.flykraft.livemenu.repository.DishImageRepository;
import com.flykraft.livemenu.repository.MenuItemRepository;
import com.flykraft.livemenu.service.CloudinaryService;
import com.flykraft.livemenu.service.KitchenService;
import com.flykraft.livemenu.service.MenuService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final DishImageRepository dishImageRepository;
    private final KitchenService kitchenService;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<MenuItem> loadAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem loadMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " not found"));
    }

    @Transactional
    @Override
    public MenuItem addMenuItem(MenuItemRequestDto menuItemRequestDto) {
        Long currentKitchenId = TenantContext.getKitchenId();
        Kitchen kitchen = kitchenService.loadKitchenById(currentKitchenId);

        MenuItem menuItem = MenuItem.builder()
                .kitchen(kitchen)
                .name(menuItemRequestDto.getName())
                .desc(menuItemRequestDto.getDesc())
                .category(menuItemRequestDto.getCategory())
                .inStock(Boolean.TRUE)
                .isVeg(menuItemRequestDto.getIsVeg())
                .price(menuItemRequestDto.getPrice())
                .build();
        menuItem = menuItemRepository.save(menuItem);

        DishImage dishImage = saveImage(menuItemRequestDto.getImage(), getFolderPathForMenuItem(kitchen.getId()));
        if (dishImage == null) return menuItem;

        dishImage.setMenuItem(menuItem);
        dishImageRepository.save(dishImage);
        return menuItem;
    }

    private DishImage saveImage(MultipartFile imageFile, String folderPath) {
        if  (imageFile == null || imageFile.isEmpty()) return null;
        try {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(imageFile, folderPath);
            DishImage dishImage = DishImage.builder()
                    .publicId(cloudinaryFile.getPublicId())
                    .secureUrl(cloudinaryFile.getSecureUrl())
                    .build();
            return dishImageRepository.save(dishImage);
        } catch (IOException e) {
            log.error("Error uploading image {}: {}", imageFile.getOriginalFilename(), e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        MenuItem selectedMenuItem = loadMenuItemById(menuItemId);
        if (menuItemRequestDto.getImage() != null) {
            DishImage existingImage = selectedMenuItem.getDishImage();
            String folderPath = getFolderPathForMenuItem(selectedMenuItem.getKitchen().getId());
            DishImage dishImage = saveImage(menuItemRequestDto.getImage(), folderPath);
            selectedMenuItem.setDishImage(dishImage);
            deleteImage(existingImage);
        }
        selectedMenuItem.setName(menuItemRequestDto.getName());
        selectedMenuItem.setDesc(menuItemRequestDto.getDesc());
        selectedMenuItem.setCategory(menuItemRequestDto.getCategory());
        selectedMenuItem.setIsVeg(menuItemRequestDto.getIsVeg());
        selectedMenuItem.setPrice(menuItemRequestDto.getPrice());

        return menuItemRepository.save(selectedMenuItem);
    }

    private void deleteImage(DishImage existingImage) {
        if (existingImage == null) return;
        try {
            cloudinaryService.deleteFile(existingImage.getPublicId());
        } catch (IOException e) {
            log.error("Error deleting image with Public ID - {}: {}", existingImage.getPublicId(), e.getMessage(), e);
        }
    }

    @Override
    public MenuItem toggleInStockForMenuItem(Long menuItemId) {
        MenuItem menuItem = loadMenuItemById(menuItemId);
        menuItem.setInStock(!menuItem.getInStock());
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    @Override
    public void deleteMenuItemById(Long menuItemId) {
        DishImage existingImage = loadMenuItemById(menuItemId).getDishImage();
        menuItemRepository.deleteById(menuItemId);
        deleteImage(existingImage);
    }

    private String getFolderPathForMenuItem(Long kitchenId) {
        return "kitchens/" + kitchenId;
    }
}
