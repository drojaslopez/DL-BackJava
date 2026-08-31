package drl.desafio.infrastructure.persistence;

import drl.desafio.domain.entity.User;
import drl.desafio.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpa jpa = jpaRepository.existsById(user.getId())
                ? jpaRepository.getReferenceById(user.getId())
                : new UserJpa(user.getId(), user.getName(), user.getEmail(), user.isActive());
        jpa.setName(user.getName());
        jpa.setEmail(user.getEmail());
        jpa.setActive(user.isActive());
        return toDomain(jpaRepository.save(jpa));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpa jpa) {
        return new User(jpa.getId(), jpa.getName(), jpa.getEmail(), jpa.isActive());
    }
}
