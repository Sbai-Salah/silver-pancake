package io.novelis.realtimeblog.repository;

import io.novelis.realtimeblog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
