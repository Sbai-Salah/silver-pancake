package io.novelis.realtimeblog.repository;

import io.novelis.realtimeblog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingOrDescriptionContaining(String titleKeyword, String descriptionKeyword);

    List<Post> findByUserId(Long userId);

    List<Post> findByCategoryId(Long categoryId);
}
