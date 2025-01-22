package gt.com.archteam.springcloud.msvc.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import gt.com.archteam.springcloud.msvc.users.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
