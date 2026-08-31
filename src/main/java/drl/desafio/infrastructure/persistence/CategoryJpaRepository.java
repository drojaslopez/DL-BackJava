package drl.desafio.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpa, UUID> {

    Optional<CategoryJpa> findByName(String name);
}
