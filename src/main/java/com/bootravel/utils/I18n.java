package com.bootravel.utils;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class I18n {
    private I18n() {
    }

    /**
     * MessageSource, will be instantiated after MessageConfigs is constructed
     *
     * @see MessageConfigs
     */
    private static MessageSource messageSource;

    /**
     * Defaults locale: Japanese
     */
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static void init(MessageSource messageSource) {
        I18n.messageSource = messageSource;
    }

    /**
     * Get Message by key
     *
     * @param key message key
     * @return Message contents
     */
    public static String get(String key) {
        return messageSource.getMessage(key, null, key, DEFAULT_LOCALE);
    }

    /**
     * Get Message by key then format with parameter values
     *
     * @param key    message key
     * @param params parameter values
     * @return formatted message
     */
    public static String get(String key, Object... params) {
        return messageSource.getMessage(key, params, key, DEFAULT_LOCALE);
    }

    /**
     * Get Message by message key and locale setting
     *
     * @param key    message key
     * @param locale locale
     * @return message contents by locale;
     */
    public static String get(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
