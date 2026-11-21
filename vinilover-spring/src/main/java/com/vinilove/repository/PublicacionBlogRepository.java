package com.vinilove.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.PublicacionBlog;

public interface PublicacionBlogRepository extends JpaRepository<PublicacionBlog, String> {
}
