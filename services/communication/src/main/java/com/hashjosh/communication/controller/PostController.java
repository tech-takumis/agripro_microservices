package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.*;
import com.hashjosh.communication.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    // CREATE
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    // READ (single post)
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // READ (all posts)
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID id,
            @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
