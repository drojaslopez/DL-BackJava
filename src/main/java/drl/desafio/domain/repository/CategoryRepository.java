package drl.desafio.domain.repository;

import drl.desafio.domain.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(UUID id);

    Optional<Category> findByName(String name);

    List<Category> findAll();

    void deleteById(UUID id);
}
