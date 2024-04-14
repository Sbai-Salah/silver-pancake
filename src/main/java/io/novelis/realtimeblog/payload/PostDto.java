package io.novelis.realtimeblog.payload;

import io.novelis.realtimeblog.domain.Like;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
//import io.swagger.v3.oas.annotations.media.Schema;
@Data
//@Schema(
//        description = "PostDto Model Information"
//)
public class PostDto {
    private long id;
    // title should not be null  or empty
    // title should have at least 2 characters
//    @Schema(
//            description = "Blog Post Title"
//    )
    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;

    // post description should be not null or empty
    // post description should have at least 10 characters
//    @Schema(
//            description = "Blog Post Description"
//    )
    @NotEmpty
    @Size(min = 10, message = "Post description should have at least 10 characters")
    private String description;

//    @Schema(
//            description = "Blog Post Content"
//    )
    @NotEmpty
    private String content;


    private Set<CommentDto> comments;

//    @Schema(
//            description = "Blog Post Category"
//    )
    private Long categoryId;
    private String nameOfCategory;


    private Date creationDate;
//    @Schema(
//            description = "ID of the User who created the Post"
//    )
    private Long userId;

//    @Schema(
//            description = "URL of the Post Image"
//    )
    private String imageUrl;
    private int likesCount; // Include the count of likes
    private Set<Long> likedUserIds; // Include only the user IDs who liked the post

}
