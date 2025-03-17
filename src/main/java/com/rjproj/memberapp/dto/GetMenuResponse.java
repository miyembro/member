package com.rjproj.memberapp.dto;

import java.util.List;

public record GetMenuResponse (
        List<MenuItemResponse> menuItems
) {
}
