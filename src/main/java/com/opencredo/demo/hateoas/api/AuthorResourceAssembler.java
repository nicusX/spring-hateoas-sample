package com.opencredo.demo.hateoas.api;

import static com.opencredo.demo.hateoas.api.ResourceIdFactory.getId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Service;

import com.opencredo.demo.hateoas.api.resources.AuthorResource;
import com.opencredo.demo.hateoas.domain.Author;
import com.opencredo.demo.hateoas.domain.Book;

@Service
public class AuthorResourceAssembler extends EmbeddableResourceAssemblerSupport<Author, AuthorResource, AuthorController>{

   @Autowired
   private BookResourceAssembler bookResourceAssembler;
   
   @Autowired
   public AuthorResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
      super(entityLinks, relProvider, AuthorController.class, AuthorResource.class);
   }

   @Override
   public Link linkToSingleResource(Author author) {
       return entityLinks.linkToSingleResource(AuthorResource.class, getId(author));
   }
   
   
   @Override
   public AuthorResource toResource(Author entity) {
      final AuthorResource resource = createResourceWithId(getId(entity), entity);
      // Add (multiple) links to authored books 
      for(Book book : entity.getBooks()) {
         resource.add( bookResourceAssembler.linkToSingleResource(book).withRel("authored-books") ); 
      }
       
      return resource;
   }

   @Override
   protected AuthorResource instantiateResource(Author entity) {
      return new AuthorResource(entity.getFirstName(), entity.getLastName());
   }
      
}

