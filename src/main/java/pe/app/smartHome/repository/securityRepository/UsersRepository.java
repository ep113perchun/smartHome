package pe.app.smartHome.repository.securityRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.app.smartHome.model.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByName(String Name);
}
