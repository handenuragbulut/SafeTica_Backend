package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.service.BlogPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog") // Tüm endpointler bu yol altında olacak
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    // ✅ Tüm blog yazılarını getirir: GET /api/blog
    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        return ResponseEntity.ok(blogPosts);
    }

    // ✅ Belirli bir blog yazısını ID'ye göre getirir: GET /api/blog/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getBlogPostById(@PathVariable Long id) {
        Optional<BlogPost> blogPost = blogPostService.getBlogPostById(id);
        return blogPost.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Yeni bir blog yazısı ekler: POST /api/blog
    @PostMapping
    public ResponseEntity<BlogPost> createBlogPost(@RequestBody BlogPost blogPost) {
        BlogPost savedPost = blogPostService.saveBlogPost(blogPost);
        return ResponseEntity.ok(savedPost);
    }

    // ✅ Belirli bir blog yazısını günceller: PUT /api/blog/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updateBlogPost(@PathVariable Long id, @RequestBody BlogPost updatedBlogPost) {
        Optional<BlogPost> existing = blogPostService.getBlogPostById(id);
        if (existing.isPresent()) {
            updatedBlogPost.setId(id);
            return ResponseEntity.ok(blogPostService.saveBlogPost(updatedBlogPost));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Belirli bir blog yazısını siler: DELETE /api/blog/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }
}
