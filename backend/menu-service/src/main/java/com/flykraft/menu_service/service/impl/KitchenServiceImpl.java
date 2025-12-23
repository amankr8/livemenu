package com.flykraft.menu_service.service.impl;

import com.flykraft.menu_service.dto.KitchenRequestDto;
import com.flykraft.menu_service.model.Kitchen;
import com.flykraft.menu_service.repository.KitchenRepository;
import com.flykraft.menu_service.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KitchenServiceImpl implements KitchenService {

    private final KitchenRepository kitchenRepository;

    @Override
    public Kitchen getKitchenById(Long kitchenId) {
        return kitchenRepository.findById(kitchenId).orElseThrow();
    }

    @Override
    public Kitchen getKitchenBySubdomain(String kitchenSubdomain) {
        return kitchenRepository.findBySubdomain(kitchenSubdomain).orElseThrow();
    }

    @Override
    public Kitchen addKitchen(KitchenRequestDto kitchenRequestDto) {
        Kitchen kitchen = Kitchen.builder()
                .name(kitchenRequestDto.getName())
                .address(kitchenRequestDto.getAddress())
                .whatsapp(kitchenRequestDto.getWhatsapp())
                .subdomain(kitchenRequestDto.getSubdomain())
                .build();
        return kitchenRepository.save(kitchen);
    }

    @Override
    public Kitchen updateKitchenSubdomain(Long kitchenId, String kitchenSubdomain) {
        Kitchen kitchen = getKitchenById(kitchenId);
        kitchen.setSubdomain(kitchenSubdomain);
        return kitchenRepository.save(kitchen);
    }

    @Override
    public Kitchen updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto) {
        Kitchen selectedKitchen = getKitchenById(kitchenId);
        selectedKitchen.setName(kitchenRequestDto.getName());
        selectedKitchen.setAddress(kitchenRequestDto.getAddress());
        selectedKitchen.setWhatsapp(kitchenRequestDto.getWhatsapp());
        return kitchenRepository.save(selectedKitchen);
    }

    @Override
    public void deleteKitchenById(Long kitchenId) {
        kitchenRepository.deleteById(kitchenId);
    }
}
