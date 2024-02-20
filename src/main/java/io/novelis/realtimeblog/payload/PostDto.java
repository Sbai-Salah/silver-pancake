package io.novelis.realtimeblog.payload;

import lombok.Data;

@Data
public class PostDto {
    private long id;
    private String title;
    private String description;
    private String content;
//    private LocalDateTime creationDate; // New field for creation date
//    private byte[] image; // New field for image

}
