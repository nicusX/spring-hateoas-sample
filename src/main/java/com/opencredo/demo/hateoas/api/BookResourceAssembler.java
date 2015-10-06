package com.opencredo.demo.hateoas.api;

import static com.opencredo.demo.hateoas.api.ResourceIdFactory.getId;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.stereotype.Service;

import com.opencredo.demo.hateoas.api.resources.AuthorResource;
import com.opencredo.demo.hateoas.api.resources.BookResource;
import com.opencredo.demo.hateoas.api.resources.PublisherResource;
import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;
import com.opencredo.demo.hateoas.domain.Publisher;

@Service
public class BookResourceAssembler  extends EmbeddableResourceAssemblerSupport<Book, BookResource, BookController>{

   // Resource assemblers are not autowired in constructor as they depends each other on instantiation
   @Autowired 
   private AuthorResourceAssembler authorResourceAssembler;
   @Autowired
   private PublisherResourceAssembler publisherResourceAssembler;
   
   @Autowired
   public BookResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
      super(entityLinks, relProvider, BookController.class, BookResource.class);
    }
  
   @Override
   protected BookResource instantiateResource(Book entity) {
      return new BookResource(entity.getIsbn(), entity.getTitle(), entity.getCopiesAvailable());
   }

   
   private BookResource toBaseResource(Book entity) {
      final BookResource resource =  createResourceWithId(getId(entity) , entity);
      
      return resource;
   }
   
   
   @Override
   public Link linkToSingleResource(Book book) {
       return entityLinks.linkToSingleResource(BookResource.class, getId(book));
   }

   /**
    * Creates the default representation of book resource (in this case with Authors and Publishers as links)
    */
   @Override
   public BookResource toResource(Book entity) {
      final BookResource resource =  toBaseResource( entity);
 
      // Add links to available actions
      addActionLinks(resource, entity);
      
      // Add authors as links
      final String authorsRel = relProvider.getCollectionResourceRelFor(AuthorResource.class);
      for(Author author : entity.getAuthors()) {
         resource.add( authorResourceAssembler.linkToSingleResource(author).withRel(authorsRel) );
      }
      // Add publisher as link
      final String publisherRel = relProvider.getItemResourceRelFor(PublisherResource.class);
      final Publisher publisher = entity.getPublisher();
      resource.add(  publisherResourceAssembler.linkToSingleResource(publisher).withRel(publisherRel)  );
      
      return resource;
   }
   
   private void addActionLinks(final BookResource resource, final Book entity) {
      // Add "purchase" link
      final Link purchaseLink = linkTo(methodOn(controllerClass).purchaseBookCopies(entity.getIsbn(), null)).withRel("purchase");
      resource.add(purchaseLink);
      
      // Conditionally add "borrow" link, if there is any copy available
      if ( entity.getCopiesAvailable() > 0 ) {
         final Link borrowLink = linkTo(methodOn(controllerClass).borrowACopy(entity.getIsbn())).withRel("borrow");
         resource.add(borrowLink);         
      }
      
      // Add "return" link
      final Link returnLink = linkTo(methodOn(controllerClass).returnACopy(entity.getIsbn()) ).withRel("return");
      resource.add(returnLink);
   }
   
   /**
    * Creates a custom, detailed representation of book resource, embedding authors and publishers
    */
   public BookResource toDetailedResource(Book entity) {
      final BookResource resource =  toBaseResource( entity);

      // Add links to available actions
      addActionLinks(resource, entity);
      
      // Create the collection of embeddables of different types
      final List<EmbeddedWrapper> embeddables = new ArrayList<EmbeddedWrapper>();
      // Add authors
      embeddables.addAll( authorResourceAssembler.toEmbeddable(entity.getAuthors()) );
      // Add publisher
      embeddables.add( publisherResourceAssembler.toEmbeddable(entity.getPublisher()) );
      
      resource.setEmbeddeds( new Resources<>(embeddables) ); // Note it must be wrapped in a Resources
      
      return resource;
   }
   
}
