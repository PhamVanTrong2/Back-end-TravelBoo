package com.bootravel.exception;

import java.net.URI;

public class ErrorConstants {
    // Add more error here
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI USER_NAME_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/user-name-already-used");

    private ErrorConstants() {
    }
}
