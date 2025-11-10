package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.PostPhoto;
import ru.job4j.cars.repository.PostPhotoRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PostPhotoService {

    private final PostPhotoRepository postPhotoRepository;

    public PostPhoto create(PostPhoto photo) {
        return postPhotoRepository.create(photo);
    }

    public void update(PostPhoto photo) {
        postPhotoRepository.update(photo);
    }

    public void delete(int photoId) {
        postPhotoRepository.delete(photoId);
    }

    public List<PostPhoto> findAllOrderById() {
        return postPhotoRepository.findAllOrderById();
    }

    public Optional<PostPhoto> findById(int photoId) {
        return postPhotoRepository.findById(photoId);
    }

    public List<PostPhoto> findByPostId(int postId) {
        return postPhotoRepository.findByPostId(postId);
    }
}
