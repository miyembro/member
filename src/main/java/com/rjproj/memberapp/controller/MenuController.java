package com.rjproj.memberapp.controller;

import com.rjproj.memberapp.dto.MenuItemResponse;
import com.rjproj.memberapp.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/organizations")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @RequestParam(value = "organizationId", required = false) UUID organizationId
    ) {
        return ResponseEntity.ok(menuService.getMenu(organizationId));
    }
}
