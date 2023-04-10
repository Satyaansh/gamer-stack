package com.projects.gamerstack.repository;

import com.projects.gamerstack.model.Comment;
import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}