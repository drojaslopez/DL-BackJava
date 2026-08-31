package drl.desafio.application.service;

import drl.desafio.application.port.CreateUserCommand;
import drl.desafio.application.port.UserResponse;
import drl.desafio.domain.entity.User;
import drl.desafio.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse execute(CreateUserCommand command) {
        User user = new User(UUID.randomUUID(), command.name(), command.email());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.isActive());
    }
}
