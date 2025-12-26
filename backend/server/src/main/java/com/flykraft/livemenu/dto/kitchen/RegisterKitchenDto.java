package com.flykraft.livemenu.dto.kitchen;

import com.flykraft.livemenu.dto.auth.AuthRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterKitchenDto {
    private KitchenRequestDto kitchenDetails;
    private AuthRequestDto credentials;
}
