package com.projects.gamerstack.service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.gamerstack.dto.SubredditDto;
import com.projects.gamerstack.exception.SubredditNotFoundException;
import com.projects.gamerstack.mapper.SubredditMapper;
import com.projects.gamerstack.model.Subreddit;
import com.projects.gamerstack.repository.SubredditRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
    
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {

        return subredditRepository.findAll()
                        .stream()
                        .map(subredditMapper::mapSubredditToDto)
                        .toList();
    }

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                                .orElseThrow(() -> new SubredditNotFoundException("Subreddit not found with id - " + id));
        return subredditMapper.mapSubredditToDto(subreddit);                        
    } 
}
