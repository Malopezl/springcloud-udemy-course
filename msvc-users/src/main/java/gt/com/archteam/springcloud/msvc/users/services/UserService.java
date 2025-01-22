package gt.com.archteam.springcloud.msvc.users.services;

import java.util.Optional;

import gt.com.archteam.springcloud.msvc.users.entities.User;

public interface UserService {
    User save(User user);

    Optional<User> update(User user, Long id);

    Iterable<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    void delete(Long id);
}
