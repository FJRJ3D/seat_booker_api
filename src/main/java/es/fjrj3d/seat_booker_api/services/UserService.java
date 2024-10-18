package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private IUserRepository iUserRepository;
}
