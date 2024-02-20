package io.novelis.realtimeblog.service;

import io.novelis.realtimeblog.payload.PostDto;
import io.novelis.realtimeblog.payload.PostResponse;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
//    List<PostDto> getAllPosts();
    // get all posts with pagination
//    List<PostDto> getAllPosts(int pageNo, int pageSize);

    // get all posts with pagination and with more data provider with post response
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy);
    PostDto getPostById(long id);
    PostDto updatePost(PostDto postDto, long id);

    void deletePostById(long id);

    List<PostDto> searchPostsByKeyword(String keyword);

}
