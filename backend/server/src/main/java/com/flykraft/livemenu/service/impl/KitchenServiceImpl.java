package com.flykraft.livemenu.service.impl;

import com.flykraft.livemenu.config.TenantContext;
import com.flykraft.livemenu.dto.auth.AuthRequestDto;
import com.flykraft.livemenu.dto.kitchen.KitchenReqDto;
import com.flykraft.livemenu.dto.kitchen.RegisterKitchenDto;
import com.flykraft.livemenu.entity.AuthUser;
import com.flykraft.livemenu.entity.Kitchen;
import com.flykraft.livemenu.entity.KitchenOwner;
import com.flykraft.livemenu.exception.ResourceNotFoundException;
import com.flykraft.livemenu.model.Authority;
import com.flykraft.livemenu.model.KitchenRole;
import com.flykraft.livemenu.repository.KitchenOwnerRepository;
import com.flykraft.livemenu.repository.KitchenRepository;
import com.flykraft.livemenu.service.AuthService;
import com.flykraft.livemenu.service.KitchenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    @Override
    public Kitchen registerKitchen(RegisterKitchenDto registerKitchenDto) {
        AuthRequestDto authRequestDto = registerKitchenDto.getCredentials();
        AuthUser authUser = authService.register(
            authRequestDto.getUsername(),
            authRequestDto.getPassword(),
            Authority.KITCHEN_OWNER
        );
        Kitchen kitchen = addKitchen(registerKitchenDto.getKitchenDetails());
        KitchenOwner kitchenOwner = KitchenOwner.builder()
            .authUser(authUser)
            .kitchen(kitchen)
            .role(KitchenRole.ADMIN)
            .build();
        kitchenOwnerRepository.save(kitchenOwner);
        return kitchen;
    }

    private Kitchen addKitchen(KitchenReqDto kitchenReqDto) {
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

    @Transactional
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
