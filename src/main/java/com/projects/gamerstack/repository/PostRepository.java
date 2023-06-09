package com.projects.gamerstack.repository;

import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.Subreddit;
import com.projects.gamerstack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}