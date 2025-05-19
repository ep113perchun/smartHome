package pe.app.smartHome.service.secureService;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.app.smartHome.model.Users;
import pe.app.smartHome.repository.securityRepository.UsersRepository;

@Service
@AllArgsConstructor
public class AppService {
    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;

    public void addUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }
}
