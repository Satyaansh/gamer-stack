package com.projects.gamerstack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projects.gamerstack.dto.CommentDto;
import com.projects.gamerstack.model.Comment;
import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    Comment map(CommentDto commentDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    CommentDto mapToDto(Comment comment);
}
