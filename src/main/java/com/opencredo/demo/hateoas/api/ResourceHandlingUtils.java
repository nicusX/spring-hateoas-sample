package com.opencredo.demo.hateoas.api;

import org.springframework.data.domain.Persistable;

public abstract class ResourceHandlingUtils {

   public static <T extends Persistable<?>> T entityOrNotFoundException(T entity) {
      if ( entity == null  ) {
         throw new ResourceNotFoundException();
      }
      return entity;
   }
}
