package com.bootravel.common.security.jwt.service;


import com.bootravel.common.security.jwt.config.JwtUtil;
import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.common.security.jwt.entity.RefreshToken;
import com.bootravel.common.security.jwt.exception.TokenRefreshException;
import com.bootravel.common.security.jwt.repository.RefreshTokenRepository;
import com.bootravel.common.security.jwt.request.TokenRefreshRequest;
import com.bootravel.common.security.jwt.response.TokenRefreshResponse;
import com.bootravel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenService {
    @Value("${security.jwt.jwtRefreshExpirationMs:#{3600}}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public static Optional<Long> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static Long extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
            return springSecurityUser.getId();
        } else if (authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    public ResponseEntity<TokenRefreshResponse> findByToken(TokenRefreshRequest request) {
        String expiredToken = request.getRefreshToken();
        return refreshTokenRepository.findByToken(expiredToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUsersEntity)
                .map(user -> {
                    String token = jwtUtil.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, expiredToken));
                })
                .orElseThrow(() -> new TokenRefreshException(expiredToken, "Refresh token is not in database!"));
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUsersEntity(userRepository.findById(userId));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs * 1000));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }


    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserEntity(userRepository.findById(userId));
    }
}