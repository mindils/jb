package ru.mindils.jb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.mindils.jb.service.config.ApplicationConfiguration;

public class MainService {

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.register(ApplicationConfiguration.class);
            context.refresh();
        }
    }
}
