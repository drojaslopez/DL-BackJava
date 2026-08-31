package drl.desafio.infrastructure.persistence;

import drl.desafio.domain.entity.Category;
import drl.desafio.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryAdapter(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Category save(Category category) {
        CategoryJpa jpa = jpaRepository.existsById(category.getId())
                ? jpaRepository.getReferenceById(category.getId())
                : new CategoryJpa(category.getId(), category.getName());
        jpa.setName(category.getName());
        return toDomain(jpaRepository.save(jpa));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private Category toDomain(CategoryJpa jpa) {
        return new Category(jpa.getId(), jpa.getName());
    }
}
