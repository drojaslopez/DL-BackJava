package drl.desafio.application.service;

import drl.desafio.application.port.UpdateCategoryCommand;
import drl.desafio.application.port.CategoryResponse;
import drl.desafio.application.port.CreateCategoryCommand;
import drl.desafio.domain.entity.Category;
import drl.desafio.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageCategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    private ManageCategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ManageCategoryUseCase(categoryRepository);
    }

    @Test
    void createsCategory() {
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse response = useCase.create(new CreateCategoryCommand("Supermercado"));

        assertEquals("SUPERMERCADO", response.name());
        verify(categoryRepository).save(any());
    }

    @Test
    void listsCategories() {
        when(categoryRepository.findAll()).thenReturn(
                List.of(new Category(UUID.randomUUID(), "SALUD")));
        assertEquals(1, useCase.list().size());
    }

    @Test
    void getsCategoryById() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category(id, "SALUD")));

        CategoryResponse response = useCase.getById(id);
        assertEquals(id, response.id());
        assertEquals("SALUD", response.name());
    }

    @Test
    void throwsErrorIfNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.getById(id));
    }

    @Test
    void updatesCategory() {
        UUID id = UUID.randomUUID();
        Category category = new Category(id, "SALUD");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse response = useCase.update(id, new UpdateCategoryCommand("Bienestar"));

        assertEquals("BIENESTAR", response.name());
    }

    @Test
    void deletesCategory() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category(id, "SALUD")));

        useCase.delete(id);
        verify(categoryRepository).deleteById(id);
    }
}
