package com.projects.gamerstack.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.dto.VoteDto;
import com.projects.gamerstack.exception.GamerStackException;
import com.projects.gamerstack.exception.PostNotFoundException;
import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.Vote;
import com.projects.gamerstack.repository.PostRepository;
import com.projects.gamerstack.repository.VoteRepository;

import lombok.AllArgsConstructor;

import static com.projects.gamerstack.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
    
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {

        Post post = postRepository.findById(voteDto.getPostId())
                        .orElseThrow(() -> new PostNotFoundException("Post Not found with id : " + voteDto.getPostId()));
        
        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());

        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new GamerStackException("You have already" + voteDto.getVoteType() + "d this post");
        }

        if(UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        }

        else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        voteRepository.save(mapToVote(voteDto, post));
        postRepository.save(post);
    }

    private Vote mapToVote(VoteDto voteDto, Post post) {

        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
