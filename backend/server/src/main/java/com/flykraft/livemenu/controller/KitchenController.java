package com.flykraft.livemenu.controller;

import com.flykraft.livemenu.dto.kitchen.KitchenReqDto;
import com.flykraft.livemenu.dto.kitchen.RegisterKitchenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/kitchens")
public interface KitchenController {

    @GetMapping
    ResponseEntity<?> getKitchen();

    @PostMapping
    ResponseEntity<?> registerKitchen(@RequestBody RegisterKitchenDto registerKitchenDto);

    @PutMapping("/{kitchenId}")
    ResponseEntity<?> updateKitchenDetails(@PathVariable Long kitchenId, @RequestBody KitchenReqDto kitchenReqDto);
}
