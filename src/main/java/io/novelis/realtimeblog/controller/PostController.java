package io.novelis.realtimeblog.controller;

import io.novelis.realtimeblog.Utils.AppConstants;
import io.novelis.realtimeblog.domain.User;
import io.novelis.realtimeblog.payload.PostDto;
import io.novelis.realtimeblog.payload.PostResponse;
import io.novelis.realtimeblog.service.AuthService;
import io.novelis.realtimeblog.service.PostService;
import jakarta.validation.Valid;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
//@Tag(
//        name = "CRUD REST APIs for Post Resource"
//)
public class PostController {
    private final PostService postService;
    private final AuthService userService;

    public PostController(PostService postService, AuthService userService){

        this.postService = postService;
        this.userService = userService;
    }



//    @Operation(
//            summary = "Create Post REST API",
//            description = "Create Post REST API is used to save post into database"
//    )
//    @ApiResponse(
//            responseCode = "201",
//            description = "Http Status 201 CREATED"
//    )
//    @SecurityRequirement(
//            name = "Bear Authentication"
//    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto){

        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }


    // getting all posts
//    @GetMapping
//    public List<PostDto> getAllPosts(){
//        return postService.getAllPosts();
//    }

    // getting all posts with pagination
//    @Operation(
//            summary = "Get All Posts REST API",
//            description = "Get All Posts REST API is used to fetch all the posts from the database"
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Http Status 200 SUCCESS"
//    )

    @GetMapping
    public PostResponse getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NO, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy

    ){
        // a good practice is to create a class to hold the constants ( hardcoded values )
        // Utils/AppConstants
        return postService.getAllPosts(pageNo, pageSize, sortBy);
    }


//    @Operation(
//            summary = "Get Post By Id REST API",
//            description = "Get Post By Id REST API is used to get single post from the database"
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Http Status 200 SUCCESS"
//    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") long id ){
        return ResponseEntity.ok(postService.getPostById(id));
    }

//    @Operation(
//            summary = "update Post REST API",
//            description = "Update Post REST API is used to update a particular post in the database"
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Http Status 200 SUCCESS"
//    )
//    @SecurityRequirement(
//            name = "Bear Authentication"
//    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@Valid @RequestBody PostDto postDto, @PathVariable(name = "id") long id){
        PostDto postResponse = postService.updatePost(postDto, id);

        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

//    @Operation(
//            summary = "Delete Post REST API",
//            description = "Delete Post REST API is used to delete a particular post from the database"
//    )
//    @ApiResponse(
//            responseCode = "200",
//            description = "Http Status 200 SUCCESS"
//    )
//    @SecurityRequirement(
//            name = "Bear Authentication"
//    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") long id){
        postService.deletePostById(id);
        return new ResponseEntity<>("Post deleted successfuly", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePostById(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        // Get the username of the authenticated user
        String username = userDetails.getUsername();
        // Delete the post by ID and username
        postService.deletePostByIdAndUsername(postId, username);
        return new ResponseEntity<>("Post deleted successfuly", HttpStatus.OK);
    }


    // Search for posts by keyword in title or description
    // /api/posts/search?keyword=chi-haja
    @GetMapping("/search")
    public List<PostDto> searchPostsByKeyword(@RequestParam String keyword) {
        return postService.searchPostsByKeyword(keyword);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PostDto>> getPostsByUserId(@RequestParam("userId") Long userId) {
        List<PostDto> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<PostDto>> getPostsByUserName(@PathVariable String userName) {
        List<PostDto> posts = postService.getPostsByUserName(userName);
        return ResponseEntity.ok(posts);
    }

    // get posts by category id
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Long categoryId) {
        List<PostDto> posts = postService.getPostsByCategoryId(categoryId);
        return ResponseEntity.ok(posts);
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.ok("Post liked successfully");
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        // Get the authenticated user
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        // Call the service method to unlike the post
        postService.unlikePost(postId, user);
        return ResponseEntity.ok("Post unliked successfully");
    }


//    @PostMapping("/{postId}/like")
//    public ResponseEntity<String> likePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
//        postService.likePost(postId);
//        return ResponseEntity.ok("Post liked successfully");
//    }
//
//    @DeleteMapping("/{postId}/unlike")
//    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {
//        postService.unlikePost(postId);
//        return ResponseEntity.ok("Post unliked successfully");
//    }

}
