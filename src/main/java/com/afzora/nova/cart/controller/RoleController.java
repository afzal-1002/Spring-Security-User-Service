package com.afzora.nova.cart.controller;

import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Role>> createRoles(
            @RequestBody List<Role> roles
    ) {

        return ResponseEntity.ok(
                roleService.createRoles(roles)
        );
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
