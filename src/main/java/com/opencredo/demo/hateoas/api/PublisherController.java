package com.opencredo.demo.hateoas.api;

import static com.opencredo.demo.hateoas.api.ResourceHandlingUtils.entityOrNotFoundException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.opencredo.demo.hateoas.api.resources.BookResource;
import com.opencredo.demo.hateoas.api.resources.NewPublisher;
import com.opencredo.demo.hateoas.api.resources.PublisherResource;
import com.opencredo.demo.hateoas.domain.Publisher;
import com.opencredo.demo.hateoas.domain.persistence.PublisherRepository;


@RestController
@ExposesResourceFor(PublisherResource.class)
@RequestMapping("/publishers")
@Transactional
public class PublisherController  {

   private final PublisherRepository publisherRepository;
   private final PublisherResourceAssembler publisherResourceAssembler;
   private final BookResourceAssembler bookResourceAssembler;
   
   @Autowired
   public PublisherController(final PublisherRepository publisherRepository, final PublisherResourceAssembler publisherResourceAssembler, final BookResourceAssembler bookResourceAssembler) {
      this.publisherRepository = publisherRepository;
      this.publisherResourceAssembler = publisherResourceAssembler;
      this.bookResourceAssembler = bookResourceAssembler;
   }
   
   @RequestMapping(method=RequestMethod.GET)
   public ResponseEntity<Resources<PublisherResource>> listAllPublishers() {
      final Iterable<Publisher> publishers = publisherRepository.findAll();
      final Resources<PublisherResource> wrapped = publisherResourceAssembler.toEmbeddedList(publishers);
      return ResponseEntity.ok(wrapped);
   }
   
   @RequestMapping(value="/{publisherId}", method=RequestMethod.GET)
   public ResponseEntity<PublisherResource> showPublisher(@PathVariable("publisherId") final String publisherId) {
      final Publisher publisher = entityOrNotFoundException(  publisherRepository.findOne(Long.valueOf(publisherId)) );
      final PublisherResource resource = publisherResourceAssembler.toResource(publisher);
      return ResponseEntity.ok(resource);
      
   }
   
   @RequestMapping(value="/{publisherId}/books", method=RequestMethod.GET)
   public ResponseEntity<Resources<BookResource>> listAllPublisherBooks(@PathVariable("publisherId") final String publisherId) {
      final Publisher publisher = entityOrNotFoundException(  publisherRepository.findOne(Long.valueOf(publisherId)) );
      final Resources<BookResource> wrapped = bookResourceAssembler.toEmbeddedList(publisher.getBooks());
      return ResponseEntity.ok(wrapped);
   }
   
   @RequestMapping(method=RequestMethod.POST)
   public ResponseEntity<Void> newPublisher(@RequestBody NewPublisher newPublisher) {
      // TODO Add input validation

      final Publisher savedPublisher = publisherRepository.save( new Publisher(newPublisher.getName()) );
      final HttpHeaders headers = new HttpHeaders();
      headers.add("Location", publisherResourceAssembler.linkToSingleResource(savedPublisher).getHref() );
      return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
   }
}
