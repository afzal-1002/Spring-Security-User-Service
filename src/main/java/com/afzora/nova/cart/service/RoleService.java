package com.afzora.nova.cart.service;

import com.afzora.nova.cart.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    Role createRole(Role role);
    List<Role> createRoles(List<Role> roles);
    Role findByName(String name);
    Role findById(Long id);
    List<Role> getAllRoles();
    void deleteRole(Long id);
    Set<Role> findRolesByNames(Set<String> roleNames);
}