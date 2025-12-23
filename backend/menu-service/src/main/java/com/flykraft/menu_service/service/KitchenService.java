package com.flykraft.menu_service.service;

import com.flykraft.menu_service.dto.KitchenRequestDto;
import com.flykraft.menu_service.model.Kitchen;

public interface KitchenService {

    Kitchen getKitchenById(Long kitchenId);

    Kitchen getKitchenBySubdomain(String kitchenSubdomain);

    Kitchen addKitchen(KitchenRequestDto kitchenRequestDto);

    Kitchen updateKitchenSubdomain(Long kitchenId, String kitchenSubdomain);

    Kitchen updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto);

    void deleteKitchenById(Long kitchenId);
}
