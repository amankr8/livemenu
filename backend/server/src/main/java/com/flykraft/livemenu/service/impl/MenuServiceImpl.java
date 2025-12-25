package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.dto.menu.MenuItemRequestDto;
import com.flykraft.livemenu.entity.DishImage;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.entity.MenuItem;
import com.flykraft.livemenu.model.CloudinaryFile;
import com.flykraft.livemenu.repository.DishImageRepository;
import com.flykraft.livemenu.repository.MenuItemRepository;
import com.flykraft.livemenu.service.CloudinaryService;
import com.flykraft.livemenu.service.KitchenService;
import com.flykraft.livemenu.service.MenuService;
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
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem getMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId).orElseThrow();
    }

    @Override
    public MenuItem addMenuItemToKitchen(Long kitchenId, MenuItemRequestDto menuItemRequestDto) {
        Kitchen kitchen = kitchenService.getKitchenById(kitchenId);
        DishImage dishImage = saveImage(menuItemRequestDto.getImage(), getFolderPathForMenuItem(kitchenId));

        MenuItem menuItem = MenuItem.builder()
                .kitchen(kitchen)
                .name(menuItemRequestDto.getName())
                .desc(menuItemRequestDto.getDesc())
                .category(menuItemRequestDto.getCategory())
                .inStock(Boolean.TRUE)
                .isVeg(menuItemRequestDto.getIsVeg())
                .price(menuItemRequestDto.getPrice())
                .dishImage(dishImage)
                .build();
        return menuItemRepository.save(menuItem);
    }

    private DishImage saveImage(MultipartFile imageFile, String folderPath) {
        if  (imageFile == null || imageFile.isEmpty()) return null;
        try {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(imageFile, folderPath);
            DishImage dishImage = DishImage.builder()
                    .publicId(cloudinaryFile.getPublicId())
                    .url(cloudinaryFile.getUrl())
                    .build();
            return dishImageRepository.save(dishImage);
        } catch (IOException e) {
            log.error("Error uploading image {}: {}", imageFile.getOriginalFilename(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public MenuItem updateMenuItem(Long menuItemId, MenuItemRequestDto menuItemRequestDto) {
        MenuItem selectedMenuItem = getMenuItemById(menuItemId);
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
        MenuItem menuItem = getMenuItemById(menuItemId);
        menuItem.setInStock(!menuItem.getInStock());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItemById(Long menuItemId) {
        menuItemRepository.deleteById(menuItemId);
    }

    private String getFolderPathForMenuItem(Long kitchenId) {
        return "kitchens/" + kitchenId;
    }
}
