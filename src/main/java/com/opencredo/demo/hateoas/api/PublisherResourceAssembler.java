package com.opencredo.demo.hateoas.api;

import static com.opencredo.demo.hateoas.api.ResourceIdFactory.getId;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Service;

import com.opencredo.demo.hateoas.api.resources.PublisherResource;
import com.opencredo.demo.hateoas.domain.Publisher;

@Service
public class PublisherResourceAssembler extends EmbeddableResourceAssemblerSupport<Publisher, PublisherResource, PublisherController> {

   @Autowired
   public PublisherResourceAssembler(final EntityLinks entityLinks, final RelProvider relProvider) {
      super(entityLinks, relProvider, PublisherController.class, PublisherResource.class);
    }

   @Override
   public Link linkToSingleResource(Publisher publisher) {
       return entityLinks.linkToSingleResource(PublisherResource.class, getId(publisher));
   }
   
   
   @Override
   protected PublisherResource instantiateResource(Publisher entity) {
      return new PublisherResource(entity.getName());
   }

   @Override
   public PublisherResource toResource(Publisher entity) {
      final PublisherResource  resource = createResourceWithId(getId(entity), entity);
      
      // Add link to list of published books
      final Link publishedBooksLink = linkTo( methodOn(PublisherController.class).listAllPublisherBooks(getId(entity)) )
            .withRel("published-books");
      resource.add(publishedBooksLink);
      
      return resource;
   }
      
}
