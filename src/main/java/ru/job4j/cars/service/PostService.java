package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.PostRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    public Post create(Post post) {
        return postRepository.create(post);
    }

    public void update(Post post) {
        postRepository.update(post);
    }

    public void delete(int postId) {
        postRepository.delete(postId);
    }

    @Transactional
    public List<Post> findAllPostWithOnePhoto() {
        List<Post> posts = postRepository.findAllWithOnePhoto();
        for (Post post : posts) {
            Hibernate.initialize(post.getPostPhotos());
        }
        return posts;
    }

    public List<Post> findAllOrderById() {
        return postRepository.findAllOrderById();
    }

    public List<Post> findActivePostsOrderByCreatedAtDesc() {
        return postRepository.findActivePostsOrderByCreatedAtDesc();
    }

    public Optional<Post> findById(int postId) {
        return postRepository.findById(postId);
    }

    public List<Post> findByUserId(int userId) {
        return postRepository.findByUserId(userId);
    }

}
