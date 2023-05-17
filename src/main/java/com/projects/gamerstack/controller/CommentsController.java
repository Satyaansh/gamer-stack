package com.projects.gamerstack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentsController {
    
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDto commentDto) {
        commentService.createComment(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@RequestParam("postId") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByPostId(id));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllCommentsByUser(@RequestParam("userName") String userName) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByUser(userName));
    }
}
