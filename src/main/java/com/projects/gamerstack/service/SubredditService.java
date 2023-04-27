package com.projects.gamerstack.service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.dto.SubredditDto;
import com.projects.gamerstack.exception.SubredditNotFoundException;
import com.projects.gamerstack.model.Subreddit;
import com.projects.gamerstack.repository.SubredditRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
    
    private final SubredditRepository subredditRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {

        return subredditRepository.findAll()
                        .stream()
                        .map(this::mapToDto)
                        .toList();
    }

    private SubredditDto mapToDto(Subreddit subreddit) {

        return SubredditDto.builder().name(subreddit.getName())
                                .id(subreddit.getId())
                                .postCount(Long.valueOf(subreddit.getPosts().size()))
                                .build();
    }

    private Subreddit mapToSubreddit(SubredditDto subredditDto) {
        
        return Subreddit.builder().name("/g/" + subredditDto.getName())
                                .description(subredditDto.getDescription())
                                .user(authService.getCurrentUser())
                                .createdDate(Instant.now())
                                .build();
    }

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.save(mapToSubreddit(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                                .orElseThrow(() -> new SubredditNotFoundException("Subreddit not found with id - " + id));
        return mapToDto(subreddit);                        
    } 
}
