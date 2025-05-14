package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.dto.BlogPostDTO;
import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogPostService {

    // BlogPost veritabanı işlemlerini gerçekleştiren repository
    private final BlogPostRepository blogPostRepository;

    // Constructor injection (Spring bunu otomatik algılar, @Autowired gerekmez)
    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    // ✅ Tüm blog yazılarını getirir (Entity olarak)
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    // ✅ Belirli bir ID'ye sahip blog yazısını getirir (Entity olarak)
    public Optional<BlogPost> getBlogPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    // ✅ Yeni bir blog yazısı kaydeder veya mevcut birini günceller (Entity)
    public BlogPost saveBlogPost(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    // ✅ Belirli bir ID'ye sahip blog yazısını siler
    public void deleteBlogPost(Long id) {
        blogPostRepository.deleteById(id);
    }

    // 🔹 DTO: Tüm blogları DTO olarak döndürür
    public List<BlogPostDTO> getAllBlogPostsAsDto() {
        return blogPostRepository.findAll().stream()
                .map(post -> new BlogPostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getShortDescription(),
                        post.getImageUrl(),
                        post.getContent(),
                        post.getPublishedAt() 
                ))
                .collect(Collectors.toList());
    }

    // 🔹 DTO: Tek blogu DTO olarak döndürür
    public BlogPostDTO getBlogPostByIdAsDto(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog bulunamadı"));

        return new BlogPostDTO(
                post.getId(),
                post.getTitle(),
                post.getShortDescription(),
                post.getImageUrl(),
                post.getContent(),
                post.getPublishedAt()
        );
    }
}
