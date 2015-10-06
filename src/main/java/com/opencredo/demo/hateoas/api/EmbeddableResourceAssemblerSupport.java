package com.opencredo.demo.hateoas.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.opencredo.demo.hateoas.api.resources.BookResource;

public abstract class EmbeddableResourceAssemblerSupport<T, D extends ResourceSupport, C> extends ResourceAssemblerSupport<T, D> {

   protected final RelProvider relProvider;
   protected final EntityLinks entityLinks;
   protected final Class<C> controllerClass;

   public EmbeddableResourceAssemblerSupport(
         final EntityLinks entityLinks, final RelProvider relProvider, 
         Class<C> controllerClass, Class<D> resourceType) {
      super(controllerClass, resourceType);
      this.entityLinks = entityLinks;
      this.relProvider = relProvider;
      this.controllerClass = controllerClass;
    }

   /**
    * Create a wrapped representation of a collection, to be added to the _embedded Resources of the containing resource
    * It relies on the embedded resource being annotated with {@link Relation} 
    * and the collection of embedded objects being annotated with {@link JsonUnwrapped} (see {@link BookResource#setEmbeddeds()})
    */
   public List<EmbeddedWrapper> toEmbeddable(Iterable<T> entities) {
      final EmbeddedWrappers wrapper = new EmbeddedWrappers(true); // Prefer collection     
      final List<D> resources = toResources(entities);
      return resources.stream().map( a -> wrapper.wrap(a) ).collect(Collectors.toList());      
   }

   /**
    * Create a wrapped representation of a single object, to be added to the _embedded Resources of the containing resource
    */
   public EmbeddedWrapper toEmbeddable(T entity) {
      final EmbeddedWrappers wrapper = new EmbeddedWrappers(false); // DO NOT prefer collections
      final D resource = toResource(entity);
      return wrapper.wrap(resource);
   }
  
   /**
    * Create an empty main object wrapping a list of resources.
    * This is the way HAL expects endpoint returning a collection works
    */
   public Resources<D> toEmbeddedList(Iterable<T> entities) {
      final List<D> resources = toResources(entities);
      return new Resources<D>(resources, linkTo(controllerClass).withSelfRel()); // Add self link to list endpoint
   }
   
   public abstract Link linkToSingleResource(T entity);
}
