package com.hashjosh.communication.controller;

import com.hashjosh.communication.dto.PostPageResponse;
import com.hashjosh.communication.dto.PostResponse;
import com.hashjosh.communication.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    // CREATE
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart("content") String content
    ) {
        return ResponseEntity.ok(postService.createPost(content,files));
    }


    @GetMapping
    public ResponseEntity<PostPageResponse> getAllPosts(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {

        PageRequest pageRequest = PageRequest.of(0,limit + 1, Sort.by("createdAt").descending());
        PostPageResponse fetch;
        if(cursor == null) {
            fetch = postService.getAllPosts(pageRequest);
        }else {
            fetch = postService.findByCursor(cursor, pageRequest);
        }

        List<PostResponse> posts = fetch.getPosts();

        boolean hasMore = posts.size() > limit;
        if(hasMore) {
            fetch.setPosts(posts.subList(0, limit));
        }

        UUID nextCursor = posts.isEmpty() ? null : posts.getLast().getId();
        fetch.setPosts(posts);
        fetch.setNextCursor(nextCursor);
        fetch.setHasMore(hasMore);
        return ResponseEntity.ok(fetch);
    }
    // READ (single post)
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@PathVariable UUID authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }



    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
