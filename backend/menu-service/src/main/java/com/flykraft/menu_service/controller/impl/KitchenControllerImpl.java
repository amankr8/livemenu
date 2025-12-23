package com.flykraft.menu_service.controller.impl;

import com.flykraft.menu_service.controller.KitchenController;
import com.flykraft.menu_service.dto.KitchenRequestDto;
import com.flykraft.menu_service.model.Kitchen;
import com.flykraft.menu_service.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KitchenControllerImpl implements KitchenController {
    private final KitchenService kitchenService;

    @Override
    public ResponseEntity<?> getKitchenBySubdomain(String kitchenSubdomain) {
        return ResponseEntity.ok().body(kitchenService.getKitchenBySubdomain(kitchenSubdomain).toResponseDto());
    }

    @Override
    public ResponseEntity<?> addKitchen(KitchenRequestDto kitchenRequestDto) {
        return ResponseEntity.ok().body(kitchenService.addKitchen(kitchenRequestDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateKitchenSubdomain(Long kitchenId, String kitchenSubdomain) {
        return ResponseEntity.ok().body(kitchenService.updateKitchenSubdomain(kitchenId, kitchenSubdomain).toResponseDto());
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
