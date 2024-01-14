package com.bootravel.common.security.globalconfig.author.imp;

import com.bootravel.common.security.globalconfig.author.AppAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

@Service("appAuthorizer")
public class AppAuthorizerImpl implements AppAuthorizer {

    private final Logger logger = LoggerFactory.getLogger(AppAuthorizerImpl.class);

    @Override
    public boolean authorize(Authentication authentication, String action, Object callerObj) {
        String securedPath = extractSecuredPath(callerObj);
        if (securedPath == null || "".equals(securedPath.trim())) {//login, logout
            return true;
        }

        try {
            UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) authentication;
            if (user == null) {
                return false;
            }
            String userId = (String) user.getPrincipal();
            if (userId == null || "".equals(userId.trim())) {
                return false;
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw e;
        }
        return true;
    }

    private String extractSecuredPath(Object callerObj) {
        Class<?> clazz = ResolvableType.forClass(callerObj.getClass()).getRawClass();
        Optional<Annotation> annotation = Arrays.asList(clazz.getAnnotations()).stream().filter((ann) ->
                ann instanceof RequestMapping).findFirst();
        logger.debug("FOUND CALLER CLASS: {}", ResolvableType.forClass(callerObj.getClass()).getType().getTypeName());
        return annotation.map(value -> ((RequestMapping) value).value()[0]).orElse(null);
    }
}