package com.bootravel.common.security.jwt.exception;

import com.bootravel.payload.responses.commonResponses.ErrorResponesHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ErrorJwt {
    public static void sendErrorResponse(HttpServletResponse response, String errorMessage, String errorKey) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        ErrorResponesHandler errorResponse = new ErrorResponesHandler(errorMessage, 401,errorKey);
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
        }
    }
    public static void handleInvalidTokenError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        // Create a custom error response JSON message
        String jsonResponse = "{ \"message\": \"" + errorMessage + "\", \"statusCode\": 400, \"errorKey\": \"invalid\" }";

        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
        }
    }
}
