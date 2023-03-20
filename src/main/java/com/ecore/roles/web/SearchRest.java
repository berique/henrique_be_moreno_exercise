package com.ecore.roles.web;

import com.ecore.roles.model.Role;
import com.ecore.roles.web.dto.RoleDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface SearchRest {
    ResponseEntity<RoleDto> findRoleByUserIdTeamId(UUID userId, UUID teamId);
}
