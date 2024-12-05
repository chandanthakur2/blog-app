package com.example.blog.service;

import com.example.blog.model.Blog;
import com.example.blog.model.User;
import com.example.blog.repository.BlogRepository;
import com.example.blog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public List<Blog> getAllBlogs(){
        List<Blog> blogs =  blogRepository.findAll();
        return blogs;
    }

    public Blog getBlogById(Long id){
        Blog blog =  blogRepository.findById(id).orElse(null);
        if(blog == null){
            throw new RuntimeException("Blog not found");
        }
        return blog;
    }

    public Blog createBlog(Blog blog){
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Long id, Blog blog){
        Blog existingBlog = blogRepository.findById(id).orElse(null);
        if(existingBlog == null){
            throw new RuntimeException("Blog not found");
        }
        existingBlog.setTitle(blog.getTitle());
        existingBlog.setContent(blog.getContent());
        existingBlog.setAuthorId(blog.getAuthorId());
        existingBlog.setCoverImage(blog.getCoverImage());
        return blogRepository.save(existingBlog);
    }

    public void deleteBlog(Long id){
        Blog existingBlog = blogRepository.findById(id).orElse(null);
        if(existingBlog == null){
            throw new RuntimeException("Blog not found");
        }
        blogRepository.delete(existingBlog);
    }
}
