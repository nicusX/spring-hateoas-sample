package com.opencredo.demo.hateoas.api.resources;

// Note this doesn't extend ResourceSupport being used for request only
public class BookPurchase {

   private int  purchasedCopies;

   public int getPurchasedCopies() {
      return purchasedCopies;
   }

   public void setPurchasedCopies(int purchasedCopies) {
      this.purchasedCopies = purchasedCopies;
   }

}
