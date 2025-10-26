package com.hashjosh.communication.service;

import com.hashjosh.communication.client.DocumentClient;
import com.hashjosh.communication.config.CustomUserDetails;
import com.hashjosh.communication.dto.PostPageResponse;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.entity.Post;
import com.hashjosh.communication.mapper.PostMapper;
import com.hashjosh.communication.repository.PostRepository;
import com.hashjosh.constant.document.dto.DocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final DocumentClient documentClient;

    public PostResponse createPost(String content, List<MultipartFile> files) {
        Post post = new Post();
        post.setContent(content);

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();


        post.setAuthorId(UUID.fromString(userDetails.getUserId()));

        List<UUID> documentIds = new ArrayList<>();
        if (files != null) {
            for(MultipartFile file : files){
                log.info("Received file: {}", file.getOriginalFilename());
                DocumentResponse response = documentClient.uploadDocument(file,userDetails.getUserId());
                log.info("Uploaded document ID: {}", response.getDocumentId());
                documentIds.add(response.getDocumentId());
            }
        }
        post.setDocumentIds(documentIds);
        return postMapper.toPostResponse(postRepository.save(post));
    }

    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return postMapper.toPostResponse(post);
    }

    public PostPageResponse getAllPosts(PageRequest pageRequest) {
        List<PostResponse> posts = postRepository.findByOrderByIdDesc(pageRequest).stream()
                .map(postMapper::toPostResponse)
                .toList();
        return postMapper.toPostPageResponse(posts);
    }


    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }
    public PostPageResponse findByCursor(UUID cursor, PageRequest page) {
        List<PostResponse> posts = postRepository.findByCreatedAtBeforeOrderByCreatedAtDesc(
                        postRepository.findById(cursor).map(Post::getCreatedAt).orElseThrow(),
                        page
                ).stream()
                .map(postMapper::toPostResponse)
                .toList();

        return PostPageResponse.builder()
                .posts(posts)
                .build();
    }

    public List<PostResponse> getPostsByAuthor(UUID authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .map(postMapper::toPostResponse)
                .collect(Collectors.toList());
    }
}
