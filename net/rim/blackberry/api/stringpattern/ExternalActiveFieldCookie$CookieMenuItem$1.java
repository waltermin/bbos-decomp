package net.rim.blackberry.api.stringpattern;

class ExternalActiveFieldCookie$CookieMenuItem$1 implements Runnable {
   private final ExternalActiveFieldCookie$CookieMenuItem this$0;

   ExternalActiveFieldCookie$CookieMenuItem$1(ExternalActiveFieldCookie$CookieMenuItem _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._ami.run(this.this$0._matchedString);
   }
}
