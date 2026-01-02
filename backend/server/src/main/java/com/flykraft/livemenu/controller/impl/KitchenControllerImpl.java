package com.flykraft.livemenu.controller.impl;

import com.flykraft.livemenu.controller.KitchenController;
import com.flykraft.livemenu.dto.kitchen.KitchenReqDto;
import com.flykraft.livemenu.dto.kitchen.KitchenResDto;
import com.flykraft.livemenu.dto.kitchen.RegisterKitchenDto;
import com.flykraft.livemenu.repository.KitchenRepository;
import com.flykraft.livemenu.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KitchenControllerImpl implements KitchenController {
    private final KitchenService kitchenService;
    private final KitchenRepository kitchenRepository;

    @Override
    public ResponseEntity<KitchenResDto> getKitchen() {
        return ResponseEntity.ok().body(kitchenService.loadKitchen().toResponseDto());
    }

    @Override
    public ResponseEntity<KitchenResDto> registerKitchen(RegisterKitchenDto registerKitchenDto) {
        return ResponseEntity.ok().body(kitchenService.registerKitchen(registerKitchenDto).toResponseDto());
    }

    @Override
    public ResponseEntity<KitchenResDto> updateKitchenDetails(Long kitchenId, KitchenReqDto kitchenReqDto) {
        return ResponseEntity.ok().body(kitchenService.updateKitchenDetails(kitchenId, kitchenReqDto).toResponseDto());
    }
}
