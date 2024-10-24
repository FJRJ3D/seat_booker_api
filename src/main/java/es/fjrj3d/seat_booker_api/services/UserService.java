package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.UserNotFoundException;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    IUserRepository iUserRepository;

    public User createUser(@Valid User user) {
        return iUserRepository.save(user);
    }

    public List<User> getAllUsers() {
        return iUserRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return iUserRepository.findById(id);
    }

    public User updateUser(User user, Long id) {
        if (!iUserRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        user.setId(id);
        return iUserRepository.save(user);
    }

    public boolean deleteUser(Long id) {
        if (!iUserRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with ID: " + id);
        } else {
            iUserRepository.deleteById(id);
            return true;
        }
    }
}
