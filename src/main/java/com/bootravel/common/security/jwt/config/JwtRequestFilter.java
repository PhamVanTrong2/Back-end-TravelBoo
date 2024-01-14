package com.bootravel.common.security.jwt.config;


import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import com.bootravel.common.security.jwt.exception.ErrorJwt;
import com.bootravel.common.security.jwt.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        log.info("Start API: " + request.getContextPath() + request.getRequestURI());
        // Add the specific URLs that should skip token validation
        List<String> urlsToSkipTokenValidation = Arrays.asList("/hotel/get-hotel/", "/booking-room/create");
        final String requestTokenHeader = request.getHeader("Authorization");

        if (shouldSkipTokenValidation(request, urlsToSkipTokenValidation) && (requestTokenHeader == null || requestTokenHeader.isEmpty())) {
                // Skip token validation for specific URLs
                chain.doFilter(request, response);
                return;

        }

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null) {
            jwtToken = requestTokenHeader;
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (SignatureException e) {
                ErrorJwt.handleInvalidTokenError(response, "JWT Token invalid");
                return;
            } catch (IllegalArgumentException e) {
                ErrorJwt.sendErrorResponse(response, "Unable to get JWT Token", "invalid");
                return;
            } catch (ExpiredJwtException e) {
                ErrorJwt.sendErrorResponse(response, "JWT Token has expired", "expired");
                return;
            }
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            // if the token is valid, configure Spring Security to manually set authentication
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                CustomUserDetails principal = (CustomUserDetails) usernamePasswordAuthenticationToken.getPrincipal();
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

    // Method to check if token validation should be skipped for the current request
    private boolean shouldSkipTokenValidation(HttpServletRequest request, List<String> urlsToSkipTokenValidation) {
        String requestURI = request.getRequestURI();
        return urlsToSkipTokenValidation.stream().anyMatch(url -> requestURI.startsWith(url));
    }

}