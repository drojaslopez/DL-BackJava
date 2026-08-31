package drl.desafio.application.service;

import drl.desafio.application.port.UpdateCategoryCommand;
import drl.desafio.application.port.CategoryResponse;
import drl.desafio.application.port.CreateCategoryCommand;
import drl.desafio.domain.entity.Category;
import drl.desafio.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public ManageCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse create(CreateCategoryCommand command) {
        Category category = new Category(UUID.randomUUID(), command.name());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryCommand command) {
        Category category = findOrThrow(id);
        category.rename(command.name());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        Category category = findOrThrow(id);
        categoryRepository.deleteById(category.getId());
    }

    private Category findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
