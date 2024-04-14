package io.novelis.realtimeblog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.novelis.realtimeblog.domain.Category;
import io.novelis.realtimeblog.domain.QCategory;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CategoryDao {
    private final EntityManager entityManager;

    public Optional<Category> getById(Long id) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QCategory qCategory = QCategory.category;
        Category category = jpaQueryFactory.selectFrom(qCategory).where(qCategory.id.eq(id)).fetchOne();
        return Optional.ofNullable(category);
    }

    public List<Category> findAll() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QCategory qCategory = QCategory.category;
        return jpaQueryFactory.selectFrom(qCategory).fetch();
    }

    public Category save(Category category) {
        entityManager.persist(category);
        return category;
    }

    public void delete(Category category) {
        entityManager.remove(category);
    }
}

