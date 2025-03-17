package com.rjproj.memberapp.security;

import com.rjproj.memberapp.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // Skip JWT filtering for the logout endpoint
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/v1/auth/logout")) {
            chain.doFilter(request, response);  // Let the request proceed without JWT validation
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        List<String> permissions = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        if (username != null) {
            MemberDetails memberDetails = (MemberDetails) userDetailsServiceImpl.loadUserByUsername(username);
            permissions = jwtUtil.extractPermissions(jwt);
            System.out.println("Extracted Permissions: " + permissions);
            if (memberDetails != null && jwtUtil.validateToken(jwt)) {
                Collection<? extends GrantedAuthority> authorities = getAuthorities(permissions);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(memberDetails, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }


    public void deleteToken() {
        jwtUtil.deleteToken();
    }


    public Collection<? extends GrantedAuthority> getAuthorities(List<String> permissions) {
        return permissions.stream().map(p -> new SimpleGrantedAuthority(p)).collect(Collectors.toList());
    }
}
