package com.ecore.roles.api;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.client.model.User;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.RolesService;
import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.MembershipDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static com.ecore.roles.utils.MockUtils.mockGetTeamById;
import static com.ecore.roles.utils.MockUtils.mockGetUserById;
import static com.ecore.roles.utils.RestAssuredHelper.getMemberships;
import static com.ecore.roles.utils.TestData.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MembershipsApiTests {
    private final MembershipRepository membershipRepository;
    private final RestTemplate restTemplate;
    private final RoleRepository roleRepository;
    private MockRestServiceServer mockServer;
    @LocalServerPort
    private int port;

    @Autowired
    public MembershipsApiTests(
            MembershipRepository membershipRepository,
            RestTemplate restTemplate,
            RoleRepository roleRepository) {
        this.membershipRepository = membershipRepository;
        this.restTemplate = restTemplate;
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
        membershipRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldCreateRoleMembership() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();

        // Mock
        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        // Act
        MembershipDto actualMembership = createMembership(expectedMembership);

        // Assert
        assertThat(actualMembership.getId()).isNotNull();
        assertThat(actualMembership).isEqualTo(MembershipDto.fromModel(expectedMembership));
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenBodyIsNull() {
        // Act & Assert
        RestAssuredHelper.createMembership(null)
                .validate(BAD_REQUEST);
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenRoleIsNull() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setRole(null);

        // Act & Assert
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST);
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenRoleIdIsNull() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setRole(Role.builder().build());

        // Act & Asset
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST);
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenUserIdIsNull() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setUserId(null);

        // Act & Assert
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST);
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenTeamIdISNull() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setTeamId(null);
        // Act & Assert
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST);
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenMembershipAlreadyExists() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();

        // Mocks
        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        // First Act
        createMembership(expectedMembership);

        // Reset mock
        mockServer.reset();
        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        // Final act and Assert
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST, "Membership already exists");
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenRoleDoesNotExist() {

        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        Role expectedRole = Role.builder().id(UUID_1).build();
        expectedMembership.setRole(expectedRole);

        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        RestAssuredHelper.createMembership(expectedMembership)
                .validate(NOT_FOUND, format("Role %s not found", UUID_1));
    }

    @Test
    void shouldFailToCreateRoleMembershipWhenTeamDoesNotExist() {
        // Arrange
        Membership expectedMembership = DEFAULT_MEMBERSHIP();

        // Mock
        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), null);

        // Act & Assert
        RestAssuredHelper.createMembership(expectedMembership)
                .validate(NOT_FOUND, format("Team %s not found", expectedMembership.getTeamId()));
    }

    @Test
    void shouldFailToAssignRoleWhenMembershipIsInvalid() {
        Membership expectedMembership = INVALID_MEMBERSHIP();

        mockGetUserById(mockServer, expectedMembership.getUserId(), null);
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        RestAssuredHelper.createMembership(expectedMembership)
                .validate(BAD_REQUEST,
                        "Invalid 'Membership' object. The provided user doesn't belong to the provided team.");
    }

    @Test
    void shouldGetAllMemberships() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();

        roleRepository.saveAndFlush(expectedMembership.getRole());
        mockGetUserById(mockServer, expectedMembership.getUserId(), User.builder().build());
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), new Team());

        createMembership(expectedMembership);

        MembershipDto[] actualMemberships = getMemberships(expectedMembership.getRole().getId())
                .statusCode(OK)
                .extract().as(MembershipDto[].class);

        assertThat(actualMemberships.length).isEqualTo(1);
        assertThat(actualMemberships[0].getId()).isNotNull();
        assertThat(actualMemberships[0]).isEqualTo(MembershipDto.fromModel(expectedMembership));
    }

    @Test
    void shouldGetAllMembershipsButReturnsEmptyList() {
        MembershipDto[] actualMemberships = getMemberships(DEVELOPER_ROLE_UUID)
                .statusCode(OK)
                .extract().as(MembershipDto[].class);

        assertThat(actualMemberships.length).isEqualTo(0);
    }

    @Test
    void shouldFailToGetAllMembershipsWhenRoleIdIsNull() {
        // Arrange and Mock
        mockGetTeamById(mockServer, null, null);
        // Act & Check
        getMemberships(null)
                .validate(BAD_REQUEST, "Bad Request");
    }

    private MembershipDto createMembership(Membership expectedMembership) {
        mockGetTeamById(mockServer, expectedMembership.getTeamId(), ORDINARY_CORAL_LYNX_TEAM());

        return RestAssuredHelper.createMembership(expectedMembership)
                .statusCode(CREATED)
                .extract().as(MembershipDto.class);
    }

}
