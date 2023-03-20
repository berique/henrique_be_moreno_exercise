package com.ecore.roles.web.rest;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.exception.ErrorResponse;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.service.RolesService;
import com.ecore.roles.service.TeamsService;
import com.ecore.roles.service.UsersService;
import com.ecore.roles.web.MembershipsApi;
import com.ecore.roles.web.dto.MembershipDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecore.roles.web.dto.MembershipDto.fromModel;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/roles/memberships")
public class MembershipsRestController implements MembershipsApi {

    private final MembershipsService membershipsService;
    private final RolesService rolesService;
    private final UsersService usersService;
    private final TeamsService teamsService;


    @Override
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<MembershipDto> assignRoleToMembership(
            @NotNull @Valid @RequestBody MembershipDto membershipDto) {
        if ( usersService.getUser(membershipDto.getUserId()) == null) {
            throw new InvalidArgumentException(Membership.class, "The provided user doesn't belong to the provided team.");
        }
        if ( teamsService.getTeam(membershipDto.getTeamId()) == null ) {
            throw new ResourceNotFoundException(Team.class, membershipDto.getTeamId());
        }
        if ( rolesService.getRole(membershipDto.getRoleId()) == null ) {
            throw new ResourceNotFoundException(Role.class, membershipDto.getRoleId());
        }
        Membership membership = membershipsService.assignRoleToMembership(membershipDto.toModel());
        return ResponseEntity
                .status(CREATED)
                .body(fromModel(membership));
    }

    @Override
    @GetMapping(
            path = "/search",
            produces = {"application/json"})
    public ResponseEntity<List<MembershipDto>> getMemberships(
            @RequestParam UUID roleId) {

        List<Membership> memberships = membershipsService.getMemberships(roleId);

        List<MembershipDto> newMembershipDto = memberships.parallelStream()
                .map(membership -> fromModel(membership))
                .collect(Collectors.toList());

        return ResponseEntity
                .status(OK)
                .body(newMembershipDto);
    }

}
