package com.flykraft.livemenu.controller.impl;

import com.flykraft.livemenu.controller.KitchenController;
import com.flykraft.livemenu.dto.kitchen.KitchenRequestDto;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KitchenControllerImpl implements KitchenController {
    private final KitchenService kitchenService;

    @Override
    public ResponseEntity<Kitchen> getCurrentKitchen() {
        return ResponseEntity.ok().body(kitchenService.loadCurrentKitchen());
    }

    @Override
    public ResponseEntity<?> addKitchen(KitchenRequestDto kitchenRequestDto) {
        return ResponseEntity.ok().body(kitchenService.addKitchen(kitchenRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto) {
        return ResponseEntity.ok().body(kitchenService.updateKitchenDetails(kitchenId, kitchenRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> deleteKitchenById(Long kitchenId) {
        kitchenService.deleteKitchenById(kitchenId);
        return ResponseEntity.ok().build();
    }
}
