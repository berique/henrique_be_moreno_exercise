package com.ecore.roles.web.rest;

import com.ecore.roles.model.Role;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.web.SearchRest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/roles/search")
public class SearchRestController implements SearchRest {
    private final MembershipsService membershipsService;

    @Override
    public Role findRoleByUserIdTeamId(UUID userId, UUID teamId) {
        return membershipsService.searchByUserIdTeamId(userId, teamId)
                .getRole();
    }
}
