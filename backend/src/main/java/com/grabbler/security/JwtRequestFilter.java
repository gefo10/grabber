package com.grabbler.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabbler.models.User;
import com.grabbler.payloads.exceptions.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Autowired private UserDetailsService userDetailsService;

  private final ObjectMapper objectMapper;

  @Autowired
  public JwtRequestFilter(
      JwtUtil jwtUtil, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");

    String email = null;
    String jwt = null;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwt = authHeader.substring(7);
      // Extract user ID from JWT token
      try {
        // Might throw exception if token is invalid
        email = jwtUtil.extractSubject(jwt);
      } catch (Exception e) {
        logger.error("JWT token extraction failed: " + e.getMessage());
        // throw new APIException("Invalid JWT token: " + e.getMessage());
        // Create the same ErrorResponse your GlobalExceptionHandler would
        ErrorResponse errorResponse =
            new ErrorResponse(
                ErrorCode.AUTHENTICATION_FAILED.getCode(),
                "Invalid JWT token: " + e.getMessage(),
                request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");

        // Write the JSON response
        objectMapper.writeValue(response.getWriter(), errorResponse);

        return; // Stop the filter chain
      }
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      User user = (User) userDetailsService.loadUserByUsername(email);

      if (jwtUtil.validateToken(jwt, user.getEmail())) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
