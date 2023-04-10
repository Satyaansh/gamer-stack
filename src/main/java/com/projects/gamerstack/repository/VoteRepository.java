package com.projects.gamerstack.repository;

import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.User;
import com.projects.gamerstack.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}