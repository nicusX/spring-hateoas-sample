package com.opencredo.demo.hateoas;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;
import com.opencredo.demo.hateoas.domain.Publisher;
import com.opencredo.demo.hateoas.domain.persistence.AuthorRepository;
import com.opencredo.demo.hateoas.domain.persistence.BookRepository;
import com.opencredo.demo.hateoas.domain.persistence.PublisherRepository;

import static java.util.Arrays.asList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;

@Profile("sampledata")
@Component
public class SampleDataLoader implements ApplicationListener<ContextRefreshedEvent>  {
   private final static Logger LOG = LoggerFactory.getLogger(SampleDataLoader.class);
   
   
   @Autowired
   private BookRepository bookRepository;
   
   @Autowired
   private AuthorRepository authorRepository;
   
   @Autowired
   private PublisherRepository publisherRepository;
   
   @Override
   public void onApplicationEvent(ContextRefreshedEvent event) {
      LOG.info("Loading sample data");
      
      final Publisher[] publishers = { 
            publisherRepository.save(new Publisher("O'Reilly")),
            publisherRepository.save(new Publisher("Addison Wesley"))
      };
      
      final Author[] authors = {
            authorRepository.save(new Author("David", "Flanagan")),
            authorRepository.save(new Author("Martin", "Fowler")),
            authorRepository.save(new Author("Kendall", "Scott"))
      };
      
      
      @SuppressWarnings("unused")
      final Book[] books = {
            bookRepository.save(new Book("0596007736", "Java in a Nutshell", asList( authors[0]  ), publishers[0], 3)  ),
            bookRepository.save(new Book("0321127420", "Patterns of Enterprise Application Architecture", asList( authors[1], authors[2]), publishers[1], 1)),
            bookRepository.save(new Book("0321193687", "UML Distilled: Applying the Standard Object Modelling Language", asList( authors[1]), publishers[1], 0))
      }; 
      
   }
   
  
}
