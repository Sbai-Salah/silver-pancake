package io.novelis.realtimeblog.service;

import io.novelis.realtimeblog.domain.User;
import io.novelis.realtimeblog.payload.LoginDto;
import io.novelis.realtimeblog.payload.RegisterDto;

import java.util.Optional;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
    Optional<User> findByUsername(String username);
    Optional<User> findAuthByUserId(Long userId);
    boolean isUserAdmin(String username);

}
