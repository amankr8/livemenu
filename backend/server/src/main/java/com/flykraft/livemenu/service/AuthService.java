package com.flykraft.livemenu.service;

import com.flykraft.livemenu.dto.auth.AuthRequestDto;
import com.flykraft.livemenu.entity.AuthUser;

public interface AuthService {

    AuthUser loadUserByUsername(String username);

    AuthUser signup(AuthRequestDto authRequestDto);

    String login(AuthRequestDto authRequestDto);
}
