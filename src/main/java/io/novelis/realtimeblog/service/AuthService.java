package io.novelis.realtimeblog.service;

import io.novelis.realtimeblog.payload.LoginDto;
import io.novelis.realtimeblog.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
