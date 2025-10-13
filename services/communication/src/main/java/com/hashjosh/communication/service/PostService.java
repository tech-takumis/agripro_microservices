package com.hashjosh.communication.service;

import com.hashjosh.communication.dto.PostRequest;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.entity.Post;
import com.hashjosh.communication.entity.User;
import com.hashjosh.communication.mapper.PostMapper;
import com.hashjosh.communication.repository.PostRepository;
import com.hashjosh.communication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper mapper;

    public PostResponse createPost(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setDocumentIds(request.getDocumentIds());

        // Fetch author entity
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        post.setAuthor(author);

        return mapToResponse(postRepository.save(post));
    }

    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToResponse(post);
    }

    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

        return mapToResponse(postRepository.save(post));
    }

    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }

    // Helper
    private PostResponse mapToResponse(Post post) {

        List<String> strDocuments = post.getDocumentIds().stream()
                .map(
                    String::valueOf
                ).toList();

        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setAuthorId(String.valueOf(post.getAuthor().getId()));
        response.setDocumentIds(strDocuments);
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        return response;
    }
}

