package io.novelis.realtimeblog.payload;

//import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
    private long id;
    // name should not be null or empty
//    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    // email should not be null or empty
    // email field validation
//    @NotEmpty(message = "Email should not be null or empty")
//    @Email
    private String email;

    // comment body should not be null or empty
    // Comment body must be minimum 10 characters
    @Size(min = 10, message = "Comment body must be minimum 10 characters")
    private String body;

//    @Schema(
//            description = "ID of the Post to which the Comment belongs"
//    )
    private Long postId;
    private Date creationDate;
}
