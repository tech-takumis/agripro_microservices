package com.hashjosh.communication.mapper;

import com.hashjosh.communication.client.AgricultureClient;
import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.dto.PostPageResponse;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final DocumentClient documentClient;
    private final AgricultureClient agricultureClient;
    public PostResponse toPostResponse(Post post) {

        List<String> urls = post.getDocumentIds().stream()
                .map(documentClient::getDocumentPreviewUrl)
                .toList();

        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorName(agricultureClient.getAgricultureName(post.getAuthorId()))
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
