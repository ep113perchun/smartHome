package pe.app.smartHome.service.secureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.app.smartHome.model.Users;
import pe.app.smartHome.repository.securityRepository.UsersRepository;
import pe.app.smartHome.config.MyUserDetails;

import java.util.Optional;

@Service
public class SecurityService implements UserDetailsService {
    @Autowired
    private UsersRepository securityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = securityRepository.findByName(username);
        return user.map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + " не существует"));
    }
}