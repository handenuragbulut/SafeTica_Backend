package com.safetica.safetica_backend.service;

import com.safetica.safetica_backend.entity.BlogPost;
import com.safetica.safetica_backend.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    // BlogPost veritabanı işlemlerini gerçekleştiren repository
    private final BlogPostRepository blogPostRepository;

    // Constructor injection (Spring bunu otomatik algılar, @Autowired gerekmez)
    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    // ✅ Tüm blog yazılarını getirir
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    // ✅ Belirli bir ID'ye sahip blog yazısını getirir (opsiyonel olarak döner)
    public Optional<BlogPost> getBlogPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    // ✅ Yeni bir blog yazısı kaydeder veya mevcut birini günceller
    public BlogPost saveBlogPost(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    // ✅ Belirli bir ID'ye sahip blog yazısını siler
    public void deleteBlogPost(Long id) {
        blogPostRepository.deleteById(id);
    }
}
