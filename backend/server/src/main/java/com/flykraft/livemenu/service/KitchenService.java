package com.flykraft.livemenu.service;

import com.flykraft.livemenu.dto.kitchen.KitchenRequestDto;
import com.flykraft.livemenu.entity.Kitchen;

public interface KitchenService {

    Kitchen loadCurrentKitchen();

    Kitchen loadKitchenById(Long kitchenId);

    Kitchen addKitchen(KitchenRequestDto kitchenRequestDto);

    Kitchen updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto);

    void deleteKitchenById(Long kitchenId);
}
