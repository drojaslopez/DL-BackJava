package drl.desafio.application.service;

import drl.desafio.application.port.UpdateUserCommand;
import drl.desafio.application.port.UserResponse;
import drl.desafio.domain.entity.User;
import drl.desafio.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManageUserUseCase {

    private final UserRepository userRepository;

    public ManageUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream()
                .map(UserResponseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserCommand command) {
        User user = findOrThrow(id);
        user.update(command.name(), command.email());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = findOrThrow(id);
        user.deactivate();
        userRepository.save(user);
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponseMapper.toResponse(user);
    }

    static final class UserResponseMapper {
        private UserResponseMapper() {
        }

        static UserResponse toResponse(User user) {
            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.isActive());
        }
    }
}
