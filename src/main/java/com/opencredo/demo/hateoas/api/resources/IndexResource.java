package com.opencredo.demo.hateoas.api.resources;

import org.springframework.hateoas.ResourceSupport;

public class IndexResource extends ResourceSupport {

   private final String name;
   private final String description;

   public IndexResource(String name, String description) {
      super();
      this.name = name;
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

   public String getName() {
      return name;
   }
   
   
}
