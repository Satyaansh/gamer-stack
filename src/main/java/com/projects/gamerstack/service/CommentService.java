package com.projects.gamerstack.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.dto.CommentDto;
import com.projects.gamerstack.exception.PostNotFoundException;
import com.projects.gamerstack.mapper.CommentMapper;
import com.projects.gamerstack.model.Comment;
import com.projects.gamerstack.model.NotificationEmail;
import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.User;
import com.projects.gamerstack.repository.CommentRepository;
import com.projects.gamerstack.repository.PostRepository;
import com.projects.gamerstack.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void createComment(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
            .orElseThrow(() -> new PostNotFoundException(commentDto.getPostId().toString()));
        Comment comment = commentMapper.map(commentDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post.");
        sendCommentNotification(message, post.getUser());
    }

    public void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentDto> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                                .stream()
                                .map(commentMapper::mapToDto)
                                .toList();
    }

    public List<CommentDto> getCommentsByUser(String userName) {
        User user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                                .stream()
                                .map(commentMapper::mapToDto)
                                .toList();
    }


    
}
