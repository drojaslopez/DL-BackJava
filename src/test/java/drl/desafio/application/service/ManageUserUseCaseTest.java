package drl.desafio.application.service;

import drl.desafio.application.port.UpdateUserCommand;
import drl.desafio.application.port.UserResponse;
import drl.desafio.domain.entity.User;
import drl.desafio.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private ManageUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManageUserUseCase(userRepository);
    }

    @Test
    void listsUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(UUID.randomUUID(), "A", "a@x.cl"),
                new User(UUID.randomUUID(), "B", "b@x.cl")));

        List<UserResponse> response = useCase.list();
        assertEquals(2, response.size());
    }

    @Test
    void getsUserById() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(
                Optional.of(new User(id, "Juan", "juan@x.cl")));

        UserResponse response = useCase.getById(id);
        assertEquals(id, response.id());
        assertTrue(response.active());
    }

    @Test
    void throwsErrorIfNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.getById(id));
    }

    @Test
    void updatesUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Old", "old@x.cl");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = useCase.update(id,
                new UpdateUserCommand("New", "new@x.cl"));

        assertEquals("New", response.name());
        assertEquals("new@x.cl", response.email());
    }

    @Test
    void deactivatesUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Luis", "luis@x.cl");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.deactivate(id);
        assertFalse(user.isActive());
        verify(userRepository).save(any());
    }
}
