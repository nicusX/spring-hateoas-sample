package com.opencredo.demo.hateoas.api.resources;

// Note this doesn't extend ResourceSupport being used for request only
public class NewAuthor {
   
   private String firstName;
   private String lastName;

   public String getFirstName() {
      return firstName;
   }   
   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }
   public String getLastName() {
      return lastName;
   }
   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

}
