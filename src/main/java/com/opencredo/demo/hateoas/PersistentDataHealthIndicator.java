package com.opencredo.demo.hateoas;

import com.opencredo.demo.hateoas.domain.persistence.AuthorRepository;
import com.opencredo.demo.hateoas.domain.persistence.BookRepository;
import com.opencredo.demo.hateoas.domain.persistence.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PersistentDataHealthIndicator implements HealthIndicator {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public Health health() {
        return Health
                .up()
                .withDetail("books.count", bookRepository.count())
                .withDetail("authors.count", authorRepository.count())
                .withDetail("publishers.count", publisherRepository.count())
                .build();
    }
}
