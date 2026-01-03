package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.kitchen.KitchenReqDto;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.repository.KitchenRepository;
import com.flykraft.livemenu.service.KitchenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KitchenServiceImpl implements KitchenService {
    private final KitchenRepository kitchenRepository;

    @Override
    public Kitchen loadKitchen() {
        return loadKitchenById(TenantContext.getKitchenId());
    }

    @Override
    public Kitchen loadKitchenById(Long kitchenId) {
        return kitchenRepository.findById(kitchenId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitchen with id " + kitchenId + " not found"));
    }

    @Cacheable(value = "kitchens", key = "#subdomain")
    @Override
    public Kitchen loadKitchenBySubdomain(String subdomain) {
        return kitchenRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("Kitchen with subdomain " + subdomain + " not found"));
    }

    @Override
    public Kitchen addKitchen(KitchenReqDto kitchenReqDto) {
        String subdomain = kitchenReqDto.getSubdomain().toLowerCase().trim();
        if (kitchenRepository.findBySubdomain(subdomain).isPresent()) {
            throw new IllegalArgumentException("Subdomain " + subdomain + " is already taken");
        }

        Kitchen kitchen = Kitchen.builder()
                .name(kitchenReqDto.getName())
                .tagline(kitchenReqDto.getTagline())
                .address(kitchenReqDto.getAddress())
                .whatsapp(kitchenReqDto.getWhatsapp())
                .subdomain(subdomain)
                .build();
        return kitchenRepository.save(kitchen);
    }

    @CacheEvict(value = "kitchens", key = "#result.subdomain")
    @Override
    public Kitchen updateKitchenDetails(Long kitchenId, KitchenReqDto kitchenReqDto) {
        Kitchen selectedKitchen = loadKitchenById(kitchenId);
        selectedKitchen.setName(kitchenReqDto.getName());
        selectedKitchen.setTagline(kitchenReqDto.getTagline());
        selectedKitchen.setAddress(kitchenReqDto.getAddress());
        selectedKitchen.setWhatsapp(kitchenReqDto.getWhatsapp());
        return kitchenRepository.save(selectedKitchen);
    }
}
