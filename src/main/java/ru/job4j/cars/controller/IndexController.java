package ru.job4j.cars.controller;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.service.PostPhotoService;
import ru.job4j.cars.service.PostService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Slf4j
@Controller
public class IndexController {

    private final PostService postService;


    /**
     * Обрабатывает GET-запросы по маршрутам "/" и "/index".
     * Добавляет в модель список всех постов, отсортированных по ID, и возвращает имя шаблона "index".
     *
     * @param model модель, используемая для передачи данных в представление
     * @return имя шаблона "index"
     */
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        List<Post> posts = postService.findAllPostWithPhotos();
        log.info("Контроллер отдал в шаблон {} постов", posts.size());
        for (Post post : posts) {
            log.info("Контроллер отдал в шаблон {} фотографий", post.getPostPhotos().size());
        }
        model.addAttribute("posts", posts);
        return "index";
    }
}