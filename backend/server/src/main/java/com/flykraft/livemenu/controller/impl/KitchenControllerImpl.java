package com.flykraft.livemenu.controller.impl;

import com.flykraft.livemenu.controller.KitchenController;
import com.flykraft.livemenu.dto.kitchen.KitchenRequestDto;
import com.flykraft.livemenu.dto.kitchen.RegisterKitchenDto;
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
    public ResponseEntity<Kitchen> getKitchen() {
        return ResponseEntity.ok().body(kitchenService.loadKitchen());
    }

    @Override
    public ResponseEntity<?> registerKitchen(RegisterKitchenDto registerKitchenDto) {
        return ResponseEntity.ok().body(kitchenService.registerKitchen(registerKitchenDto).toResponseDto());
    }

    @Override
    public ResponseEntity<?> updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto) {
        return ResponseEntity.ok().body(kitchenService.updateKitchenDetails(kitchenId, kitchenRequestDto).toResponseDto());
    }
}
