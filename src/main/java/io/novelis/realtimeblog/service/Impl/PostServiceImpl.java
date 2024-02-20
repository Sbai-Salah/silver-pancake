package io.novelis.realtimeblog.service.Impl;

import io.novelis.realtimeblog.domain.Post;
import io.novelis.realtimeblog.exception.ResourceNotFoundException;
import io.novelis.realtimeblog.payload.PostDto;
import io.novelis.realtimeblog.payload.PostResponse;
import io.novelis.realtimeblog.repository.PostRepository;
import io.novelis.realtimeblog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto){
        Post post = mapToEntity(postDto);
        Post newPost = postRepository.save(post);
        return mapToDto(newPost);
    }

//    @Override
//    public List<PostDto> getAllPosts(){
//        List<Post> posts = postRepository.findAll();
//        // using method reference instead of the lambda expression.
//        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
//    }
    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy){

        // create pageable instance
        PageRequest pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Post> posts = postRepository.findAll(pageable);

        // get content for page object
        List<Post> listOfPosts = posts.getContent();

        // using method reference instead of the lambda expression.
        // return listOfPosts.stream().map(this::mapToDto).collect(Collectors.toList());
        // returning a post response with more information :)
        List<PostDto> content = listOfPosts.stream().map(this::mapToDto).collect(Collectors.toList());
        PostResponse postResponse = new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;

    }

    @Override
    public PostDto getPostById(long id){
        Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id){
        // get post by id : if notExist throw exception
        Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }


    @Override
    public void deletePostById(long id){
        // get post by id : if notExist throw exception
        Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        postRepository.deleteById(id);
    }


    @Override
    public List<PostDto> searchPostsByKeyword(String keyword) {
        List<Post> posts = postRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword);

        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

// ------------- MANUAL MAPPING ---------------------
// Convert Entity to DTO
    private PostDto mapToDto(Post post){
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());

        return postDto;
    }

// Convert DTO to Entity
    private Post mapToEntity(PostDto postDto){
        Post post = new Post();
        post.setId(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return post;
    }
}
