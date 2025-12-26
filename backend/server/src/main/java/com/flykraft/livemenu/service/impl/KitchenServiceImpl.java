package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.kitchen.KitchenRequestDto;
import com.flykraft.livemenu.dto.kitchen.RegisterKitchenDto;
import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.entity.KitchenOwner;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.model.KitchenRole;
import com.flykraft.livemenu.repository.KitchenOwnerRepository;
import com.flykraft.livemenu.repository.KitchenRepository;
import com.flykraft.livemenu.service.AuthService;
import com.flykraft.livemenu.service.KitchenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KitchenServiceImpl implements KitchenService {
    private final KitchenRepository kitchenRepository;
    private final KitchenOwnerRepository kitchenOwnerRepository;
    private final AuthService authService;

    @Override
    public Kitchen loadKitchenById(Long kitchenId) {
        return kitchenRepository.findById(kitchenId)
                .orElseThrow(() -> new ResourceNotFoundException("Kitchen with id " + kitchenId + " not found"));
    }

    @Override
    public Kitchen loadKitchen() {
        return loadKitchenById(TenantContext.getKitchenId());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    @Override
    public Kitchen registerKitchen(RegisterKitchenDto registerKitchenDto) {
        AuthUser authUser = authService.signup(registerKitchenDto.getCredentials());
        Kitchen kitchen = addKitchen(registerKitchenDto.getKitchenDetails());
        KitchenOwner kitchenOwner = KitchenOwner.builder()
                .authUser(authUser)
                .kitchen(kitchen)
                .role(KitchenRole.ADMIN)
                .build();
        kitchenOwnerRepository.save(kitchenOwner);
        return kitchen;
    }

    private Kitchen addKitchen(KitchenRequestDto kitchenRequestDto) {
        String subdomain = kitchenRequestDto.getSubdomain().toLowerCase().trim();
        if (kitchenRepository.findBySubdomain(subdomain).isPresent()) {
            throw new IllegalArgumentException("Subdomain " + subdomain + " is already taken");
        }

        Kitchen kitchen = Kitchen.builder()
                .name(kitchenRequestDto.getName())
                .tagline(kitchenRequestDto.getTagline())
                .address(kitchenRequestDto.getAddress())
                .whatsapp(kitchenRequestDto.getWhatsapp())
                .subdomain(subdomain)
                .build();
        return kitchenRepository.save(kitchen);
    }

    @PreAuthorize("hasAuthority('KITCHEN_OWNER')")
    @Transactional
    @Override
    public Kitchen updateKitchenDetails(Long kitchenId, KitchenRequestDto kitchenRequestDto) {
        Kitchen selectedKitchen = loadKitchenById(kitchenId);
        selectedKitchen.setName(kitchenRequestDto.getName());
        selectedKitchen.setTagline(kitchenRequestDto.getTagline());
        selectedKitchen.setAddress(kitchenRequestDto.getAddress());
        selectedKitchen.setWhatsapp(kitchenRequestDto.getWhatsapp());
        return kitchenRepository.save(selectedKitchen);
    }
}
