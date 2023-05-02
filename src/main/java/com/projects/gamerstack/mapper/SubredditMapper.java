package com.projects.gamerstack.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projects.gamerstack.dto.SubredditDto;
import com.projects.gamerstack.model.Post;
import com.projects.gamerstack.model.Subreddit;

@Mapper(componentModel = "spring")
public interface SubredditMapper {
    
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Long mapPosts(List<Post> numberOfPosts) {
        return Long.valueOf(numberOfPosts.size());
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}
