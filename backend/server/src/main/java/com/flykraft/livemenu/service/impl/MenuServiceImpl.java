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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {
    private final MenuItemRepository menuItemRepository;
    private final DishImageRepository dishImageRepository;
    private final KitchenService kitchenService;
    private final CloudinaryService cloudinaryService;

    private static final List<String> ALLOWED_FILE_TYPES = List.of("image/jpeg", "image/png");

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public List<MenuItem> loadAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem loadMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + menuItemId + " does not exist"));
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

        if (menuItemRequestDto.getImage() != null) {
            DishImage dishImage = saveImage(menuItemRequestDto.getImage(), getFolderPathForMenuItem(kitchen.getId()));
            dishImage.setMenuItem(menuItem);
            menuItem.setDishImage(dishImageRepository.save(dishImage));
        }

        return menuItem;
    }

    private void validateImage(MultipartFile image) {
        if (!ALLOWED_FILE_TYPES.contains(image.getContentType())) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed.");
        }
    }

    private DishImage saveImage(MultipartFile imageFile, String folderPath) {
        if  (imageFile == null || imageFile.isEmpty()) return null;

        validateImage(imageFile);
        CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(new DishImage(), imageFile, folderPath);
        return (DishImage) cloudinaryFile;
    }

    @Transactional
    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        MenuItem selectedMenuItem = loadMenuItemById(menuItemId);
        if (menuItemRequestDto.getImage() != null && !menuItemRequestDto.getImage().isEmpty()) {
            DishImage existingImage = selectedMenuItem.getDishImage();
            String folderPath = getFolderPathForMenuItem(selectedMenuItem.getKitchen().getId());
            DishImage dishImage = saveImage(menuItemRequestDto.getImage(), folderPath);
            dishImage.setMenuItem(selectedMenuItem);
            selectedMenuItem.setDishImage(dishImageRepository.save(dishImage));
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
        cloudinaryService.deleteFile(existingImage.getPublicId());
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
        return appName + "/kitchens/" + kitchenId;
    }
}
