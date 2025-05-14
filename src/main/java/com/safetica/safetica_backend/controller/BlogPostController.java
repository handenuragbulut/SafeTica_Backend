package com.safetica.safetica_backend.controller;

import com.safetica.safetica_backend.dto.BlogPostDTO;
import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.service.BlogPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog") // Tüm endpointler bu yol altında olacak
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    // ✅ Tüm blog yazılarını getirir (entity olarak — eski yapı korunur)
    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllBlogPosts() {
        List<BlogPost> blogPosts = blogPostService.getAllBlogPosts();
        return ResponseEntity.ok(blogPosts);
    }

    // ✅ ID ile blog yazısı getirir (entity olarak)
    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getBlogPostById(@PathVariable Long id) {
        Optional<BlogPost> blogPost = blogPostService.getBlogPostById(id);
        return blogPost.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Yeni blog yazısı ekler
    @PostMapping
    public ResponseEntity<BlogPost> createBlogPost(@RequestBody BlogPost blogPost) {
        if (blogPost.getPublishedAt() == null) {
            blogPost.setPublishedAt(LocalDateTime.now());
        }
        BlogPost saved = blogPostService.saveBlogPost(blogPost);
        return ResponseEntity.ok(saved);
    }

    // ✅ Blog yazısı günceller
    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updateBlogPost(@PathVariable Long id, @RequestBody BlogPost updatedBlog) {
        Optional<BlogPost> existing = blogPostService.getBlogPostById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BlogPost blogToUpdate = existing.get();
        blogToUpdate.setTitle(updatedBlog.getTitle());
        blogToUpdate.setShortDescription(updatedBlog.getShortDescription());
        blogToUpdate.setImageUrl(updatedBlog.getImageUrl());
        blogToUpdate.setContent(updatedBlog.getContent());

        BlogPost saved = blogPostService.saveBlogPost(blogToUpdate);
        return ResponseEntity.ok(saved);
    }

    // ✅ Blog yazısını siler
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }

    // 🆕 ✅ Admin Panel: Tüm blogları DTO ile getirir
    @GetMapping("/admin/blogs")
    public ResponseEntity<List<BlogPostDTO>> getAllBlogPostsAsDto() {
        List<BlogPostDTO> dtos = blogPostService.getAllBlogPostsAsDto();
        return ResponseEntity.ok(dtos);
    }

    // 🆕 ✅ Admin Panel: Tek blogu DTO ile getirir (edit modal için)
    @GetMapping("/admin/blogs/{id}")
    public ResponseEntity<BlogPostDTO> getBlogPostByIdAsDto(@PathVariable Long id) {
        BlogPostDTO dto = blogPostService.getBlogPostByIdAsDto(id);
        return ResponseEntity.ok(dto);
    }
}
