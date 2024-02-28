package io.novelis.realtimeblog.service.Impl;

import io.novelis.realtimeblog.domain.Post;
import io.novelis.realtimeblog.domain.User;
import io.novelis.realtimeblog.exception.ResourceNotFoundException;
import io.novelis.realtimeblog.payload.PostDto;
import io.novelis.realtimeblog.payload.PostResponse;
import io.novelis.realtimeblog.repository.PostRepository;
import io.novelis.realtimeblog.repository.UserRepository;
import io.novelis.realtimeblog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private ModelMapper mapper;


    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper, UserRepository userRepository
                           ) {
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto){
//        Post post = mapToEntity(postDto);
//        post.setUser(user);
//        Post newPost = postRepository.save(post);
//        return mapToDto(newPost);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User currentUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

            // Now you have the authenticated user, proceed with creating the post
            Post post = mapToEntity(postDto);
            post.setUser(currentUser);
            post.setComments(new HashSet<>());
            Post newPost = postRepository.save(post);
            return mapToDto(newPost);
        } else {
            System.out.println("User is not authenticated");

            return null;
        }

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

// ------------- NEW SERVICES ---------------------

    @Override
    public List<PostDto> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByCategoryId(Long categoryId) {
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }
// ------------- MANUAL MAPPING ---------------------
// Convert Entity to DTO
//    private PostDto mapToDto(Post post){
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
//
//        return postDto;
//    }

// Convert DTO to Entity
//    private Post mapToEntity(PostDto postDto){
//        Post post = new Post();
//        post.setId(postDto.getId());
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
//
//        return post;
//    }
// ------------- AUTOMATIC MAPPING ---------------------
// convert Entity into DTO
private PostDto mapToDto(Post post){
    return mapper.map(post, PostDto.class);
}

    // convert DTO to entity
    private Post mapToEntity(PostDto postDto){
        return mapper.map(postDto, Post.class);
    }


}
