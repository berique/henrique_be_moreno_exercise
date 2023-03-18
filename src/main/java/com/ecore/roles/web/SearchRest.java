package com.ecore.roles.web;

import com.ecore.roles.model.Role;

import java.util.UUID;

public interface SearchRest {
    Role findRoleByUserIdTeamId(UUID userId, UUID teamId);
}
