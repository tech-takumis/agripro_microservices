package com.hashjosh.communication.mapper;

import com.hashjosh.communication.client.UserHttpClient;
import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.dto.PostPageResponse;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.entity.Post;
import com.hashjosh.constant.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final DocumentClient documentClient;
    private final UserHttpClient userHttpClient;
    public PostResponse toPostResponse(Post post) {

        List<String> urls = post.getDocumentIds().stream()
                .map(documentClient::getDocumentPreviewUrl)
                .toList();

        UserResponseDTO user = userHttpClient.getUserById(post.getAuthorId());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(user.getUsername())
                .authorId(String.valueOf(post.getAuthorId()))
                .urls(urls)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public PostPageResponse toPostPageResponse(List<PostResponse> posts) {
        return PostPageResponse.builder()
                .posts(posts)
                .build();
    }
}
