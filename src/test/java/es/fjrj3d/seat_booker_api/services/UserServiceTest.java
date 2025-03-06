package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.UserNotFoundException;
import es.fjrj3d.seat_booker_api.models.EUserRole;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private IUserRepository iUserRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Long userId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(userId);
        user.setRole(EUserRole.USER);
    }

    @Test
    public void test_create_user() {
        when(iUserRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(user, createdUser);
        verify(iUserRepository, times(1)).save(user);
    }

    @Test
    public void test_get_all_users() {
        when(iUserRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(iUserRepository, times(1)).findAll();
    }

    @Test
    public void test_get_user_by_id_found() {
        when(iUserRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        verify(iUserRepository, times(1)).findById(userId);
    }

    @Test
    public void test_get_user_by_id_not_found() {
        when(iUserRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(userId);

        assertFalse(foundUser.isPresent());
        verify(iUserRepository, times(1)).findById(userId);
    }

    @Test
    public void test_update_user_found() {
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setRole(EUserRole.ADMIN);
        when(iUserRepository.existsById(userId)).thenReturn(true);
        when(iUserRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser, userId);

        assertEquals(updatedUser, result);
        verify(iUserRepository, times(1)).existsById(userId);
        verify(iUserRepository, times(1)).save(updatedUser);
    }

    @Test
    public void test_update_user_not_found() {
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setRole(EUserRole.ADMIN);
        when(iUserRepository.existsById(userId)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(updatedUser, userId);
        });

        assertEquals("User not found with ID: " + userId, thrown.getMessage());
        verify(iUserRepository, times(1)).existsById(userId);
    }

    @Test
    public void test_update_user_role() {
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setRole(EUserRole.ADMIN);
        when(iUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(iUserRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUserRole(userId, EUserRole.ADMIN);

        assertEquals(updatedUser, result);
        verify(iUserRepository, times(1)).findById(userId);
        verify(iUserRepository, times(1)).save(updatedUser);
    }

    @Test
    public void test_update_user_role_user_not_found() {
        when(iUserRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserRole(userId, EUserRole.ADMIN);
        });

        assertEquals("User not found with id: " + userId, thrown.getMessage());
        verify(iUserRepository, times(1)).findById(userId);
    }

    @Test
    public void test_delete_user() {
        when(iUserRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.deleteUser(userId);

        assertTrue(result);
        verify(iUserRepository, times(1)).existsById(userId);
        verify(iUserRepository, times(1)).deleteById(userId);
    }

    @Test
    public void test_delete_user_not_found() {
        when(iUserRepository.existsById(userId)).thenReturn(false);

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("User not found with ID: " + userId, thrown.getMessage());
        verify(iUserRepository, times(1)).existsById(userId);
    }
}