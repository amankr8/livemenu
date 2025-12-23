package com.flykraft.menu_service.controller;

import com.flykraft.menu_service.dto.KitchenRequestDto;
import com.flykraft.menu_service.model.Kitchen;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/kitchens")
public interface KitchenController {

    @GetMapping("/{kitchenSubdomain}")
    ResponseEntity<?> getKitchenBySubdomain(@PathVariable String kitchenSubdomain);

    @PostMapping
    ResponseEntity<?> addKitchen(@RequestBody KitchenRequestDto kitchenRequestDto);

    @PatchMapping("/{kitchenId}/update")
    ResponseEntity<?> updateKitchenSubdomain(@PathVariable Long kitchenId, @RequestParam String kitchenSubdomain);

    @PutMapping("/{kitchenId}")
    ResponseEntity<?> updateKitchenDetails(@PathVariable Long kitchenId, @RequestBody KitchenRequestDto kitchenRequestDto);

    @DeleteMapping("/{kitchenId}")
    ResponseEntity<?> deleteKitchenById(@PathVariable Long kitchenId);
}
