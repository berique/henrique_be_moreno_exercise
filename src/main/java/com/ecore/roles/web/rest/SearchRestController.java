package com.ecore.roles.web.rest;

import com.ecore.roles.model.Role;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.web.SearchRest;
import com.ecore.roles.web.dto.MembershipDto;
import com.ecore.roles.web.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/roles/search")
public class SearchRestController implements SearchRest {
    private final MembershipsService membershipsService;

    @Override
    @GetMapping
    public ResponseEntity<RoleDto> findRoleByUserIdTeamId(@RequestParam("teamMemberId") UUID userId, @RequestParam("teamId") UUID teamId) {
        Role role = membershipsService.searchByUserIdTeamId(userId, teamId)
                .getRole();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(RoleDto.fromModel(role));
    }
}
