package edu.ptithcm.learnnextbackend.modules.user.unit;

import edu.ptithcm.learnnextbackend.common.utils.PasswordUtil; 
import edu.ptithcm.learnnextbackend.modules.user.UserRepository; 
import edu.ptithcm.learnnextbackend.modules.user.dto.request.CreateUserRequest; 
import edu.ptithcm.learnnextbackend.modules.user.dto.response.CreateUserResponse; 
import edu.ptithcm.learnnextbackend.modules.user.dto.request.UpdateUserRequest; 
import edu.ptithcm.learnnextbackend.modules.user.entity.User; 
import edu.ptithcm.learnnextbackend.modules.user.enums.UserStatus; 
import edu.ptithcm.learnnextbackend.modules.user.impl.UserServiceImpl;
 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; 
import org.mockito.*;
import java.util.*; 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; 

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceTest {
    @Mock private UserRepository userRepository; 
    @InjectMocks private UserServiceImpl userService;

    // CREATE SUCCESS
    @Test
    void create_user_success() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("user@gmail.com");
        req.setPassword("123456");
        req.setFullName("User A");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {
            mocked.when(() -> PasswordUtil.hash(any())).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(UUID.randomUUID());
                u.setStatus(UserStatus.ACTIVE);
                return u;
            });
            CreateUserResponse res = userService.createUser(req);

            assertNotNull(res);
            assertEquals(req.getEmail(), res.getEmail());
            assertEquals(req.getFullName(), res.getFullName());
            assertEquals(UserStatus.ACTIVE, res.getStatus());

            verify(userRepository).save(any(User.class));
        }
    }

    // EMAIL EXISTS
    @Test
    void create_user_email_exists() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("user@gmail.com");
        req.setPassword("123456");
        req.setFullName("User A");

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(req));
        assertEquals("Email already exists", ex.getMessage());
    }

    // FIND BY EMAIL SUCCESS
    @Test
    void find_by_email_success() {
        User user = User.builder()
                .email("user@gmail.com")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        User result = userService.findByEmail("user@gmail.com");

        assertNotNull(result);
        assertEquals("user@gmail.com", result.getEmail());
    }

    // FIND BY EMAIL NOT FOUND
    @Test
    void find_by_email_not_found() {
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByEmail("user@gmail.com"));
    }

    //GET BY ID SUCCESS
    @Test
    void get_by_id_success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .email("user@gmail.com")
                .fullName("User A")
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        CreateUserResponse res = userService.getById(id);
        assertEquals(id, res.getId());
    }

    // UPDATE SUCCESS
    @Test
    void update_user_success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .fullName("Old Name")
                .status(UserStatus.ACTIVE)
                .build();

        UpdateUserRequest req = new UpdateUserRequest();

        req.setFullName("New Name");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateUserResponse res = userService.update(id, req);

        assertEquals("New Name", res.getFullName());
    }

    //DELETE SUCCESS 
    @Test 
    void delete_user_success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .status(UserStatus.ACTIVE).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        userService.delete(id);
        assertEquals(UserStatus.DELETED, user.getStatus());
        verify(userRepository).save(user);
    }

    //GET ALL USERS 
    @Test
    void get_all_users() {
        List<User> users = List.of(User.builder().email("a@gmail.com").status(UserStatus.ACTIVE).build(),
                User.builder().email("b@gmail.com").status(UserStatus.ACTIVE).build());
        when(userRepository.findAllByStatusNot(UserStatus.DELETED)).thenReturn(users);
        List<CreateUserResponse> res = userService.getAllUsers();
        assertEquals(2, res.size());
    }

    //get_by_id_user_deleted
    @Test
    void get_by_id_deleted_user() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .status(UserStatus.DELETED)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userService.getById(id));

        assertEquals("User not found", ex.getMessage());
    }

    //get_by_id_not_found
    @Test
    void get_by_id_not_found() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                RuntimeException.class,
                () -> userService.getById(id));
    }

    //update_deleted_user
    @Test
    void update_deleted_user() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .status(UserStatus.DELETED)
                .build();

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("New Name");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertThrows(
                RuntimeException.class,
                () -> userService.update(id, req));
    }

    //delete_user_not_found
    @Test
    void delete_user_not_found() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(
                RuntimeException.class,
                () -> userService.delete(id));
    }
}
