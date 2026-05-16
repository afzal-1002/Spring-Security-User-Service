package com.afzora.nova.cart.service.implementation;

import com.afzora.nova.cart.entity.Role;
import com.afzora.nova.cart.repository.RoleRepository;
import com.afzora.nova.cart.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImplementation(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {

        String normalizedRole = role.getName().toUpperCase();
        if (roleRepository.existsByName(normalizedRole)) {
            throw new RuntimeException("Role already exists");
        }
        role.setName(normalizedRole);
        return roleRepository.save(role);
    }

    @Override
    public List<Role> createRoles(List<Role> roles) {

        List<Role> rolesToSave = roles.stream()
                .peek(role -> role.setName(role.getName().toUpperCase()))
                .filter(role -> !roleRepository.existsByName(role.getName()))
                .toList();

        return roleRepository.saveAll(rolesToSave);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void deleteRole(Long id) {

        Role role = findById(id);

        roleRepository.delete(role);
    }

    @Override
    public Set<Role> findRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    String normalizedRole = roleName.toUpperCase();
                    return roleRepository.findByName(normalizedRole)
                            .orElseGet(() -> {
                                Role newRole = Role.builder()
                                        .name(normalizedRole)
                                        .build();
                                return roleRepository.save(newRole);
                            });
                })
                .collect(Collectors.toSet());
    }
}