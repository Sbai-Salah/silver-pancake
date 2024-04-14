package io.novelis.realtimeblog.service;

import io.novelis.realtimeblog.domain.User;
import io.novelis.realtimeblog.payload.PostDto;
import io.novelis.realtimeblog.payload.PostResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy);
    PostDto getPostById(long id);
    PostDto updatePost(PostDto postDto, long id);

    void deletePostById(long id);

    List<PostDto> searchPostsByKeyword(String keyword);

    List<PostDto> getPostsByUserId(Long userId);
    List<PostDto> getPostsByUserName(String userName);
    List<PostDto> getPostsByCategoryId(Long categoryId);

    void deletePostByIdAndUsername(Long postId, String username);

    void likePost(Long postId);
    void unlikePost(Long postId, Optional<User> user);

}
