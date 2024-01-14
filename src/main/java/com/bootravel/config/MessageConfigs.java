package com.bootravel.config;

import com.bootravel.utils.I18n;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MessageConfigs implements WebMvcConfigurer {

    @Value("${app.api.logging.requestIdParamName:#{'requestId'}}")
    private String requestIdParamName;


    /**
     * Add path to scan for i18n messages. return {@link MessageSource}
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:i18n/error"    // Error
        );
        messageSource.setDefaultEncoding("UTF-8");

        // Initial I18n
        I18n.init(messageSource);
        return messageSource;
    }
}
