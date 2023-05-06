package com.projects.gamerstack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    
    private Long postId;
    private String subredditName;
    private String potsName;
    private String url;
    private String description;
    
}
