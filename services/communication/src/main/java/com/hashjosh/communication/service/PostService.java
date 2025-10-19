package com.hashjosh.communication.service;

import com.hashjosh.communication.config.CustomUserDetails;
import com.hashjosh.communication.dto.PostPageResponse;
import com.hashjosh.communication.dto.PostRequest;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.entity.Post;
import com.hashjosh.communication.entity.User;
import com.hashjosh.communication.mapper.PostMapper;
import com.hashjosh.communication.repository.PostRepository;
import com.hashjosh.communication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostResponse createPost(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setDocumentIds(request.getDocumentIds());

        // Fetch author entity
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User author = userRepository.findByUserId(UUID.fromString(userDetails.getUserId()))
                .orElseThrow(() -> new RuntimeException("Author not found"));
        post.setAuthor(author);

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

    public PostResponse updatePost(UUID id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setDocumentIds(request.getDocumentIds());

        // Update author if changed
        if (!post.getAuthor().getId().equals(request.getAuthorId())) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            post.setAuthor(author);
        }

        return postMapper.toPostResponse(postRepository.save(post));
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

