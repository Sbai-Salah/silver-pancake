package io.novelis.realtimeblog.service.Impl;

import io.novelis.realtimeblog.domain.Like;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper;


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
    public List<PostDto> getPostsByUserName(String userName) {
        List<Post> posts = postRepository.findByUser_Username(userName);
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    @Override
    public List<PostDto> getPostsByCategoryId(Long categoryId) {
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return posts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deletePostByIdAndUsername(Long postId, String username) {
        // Find the post by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        // Check if the authenticated user is the owner of the post
        if (!post.getUser().getEmail().equals(username)) {
            throw new Error("You are not authorized to delete this post- Post User: " + post.getUser().getUsername()
            + " You are : " + username);
        }
        // Delete the post
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(Long postId) {
        // ----- RETRIEVE USER AFTER AUTH ------
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: "+ username));


        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        // Check if the user has already liked the post
        if (post.getLikes().stream().anyMatch(like -> like.getUser().equals(user))) {
            throw new RuntimeException("User has already liked the post");
        }

        // Create a new Like entity and associate it with the user and post
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        post.getLikes().add(like);

        postRepository.save(post);
    }


    @Override
    @Transactional
    public void unlikePost(Long postId, Optional<User> user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        // Find the like associated with the user and post
        Optional<Like> likeOptional = post.getLikes().stream()
                .filter(like -> like.getUser().equals(user))
                .findFirst();

        // If like is found, remove it from the post's likes collection
        likeOptional.ifPresent(like -> post.getLikes().remove(like));

        postRepository.save(post);
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
//private PostDto mapToDto(Post post){
//    return mapper.map(post, PostDto.class);
//}
public PostDto mapToDto(Post post) {
    PostDto postDto = mapper.map(post, PostDto.class);
    postDto.setLikesCount(post.getLikes().size()); // Set the likes count
    postDto.setLikedUserIds(mapLikedUserIds(post.getLikes())); // Map liked user IDs
    return postDto;
}

    private Set<Long> mapLikedUserIds(Set<Like> likes) {
        Set<Long> likedUserIds = new HashSet<>();
        for (Like like : likes) {
            likedUserIds.add(like.getUser().getId()); // Add user IDs of users who liked the post
        }
        return likedUserIds;
    }
//    // convert DTO to entity
//    private Post mapToEntity(PostDto postDto){
//        return mapper.map(postDto, Post.class);
//    }
public Post mapToEntity(PostDto postDto) {
    Post post = mapper.map(postDto, Post.class);

    // Set likes count and liked users from DTO to entity
    post.setLikes(mapLikesFromDto(postDto));

    return post;
}

    private Set<Like> mapLikesFromDto(PostDto postDto) {
        Set<Like> likes = new HashSet<>();
        if (postDto.getLikedUserIds() != null) {
            for (Long userId : postDto.getLikedUserIds()) {
                User user = new User();
                user.setId(userId); // Assuming User has setId method

                Like like = new Like();
                like.setUser(user); // Set user for like

                // Set the post for the like if needed
                // like.setPost(post);

                likes.add(like);
            }
        }
        return likes;
    }

}
