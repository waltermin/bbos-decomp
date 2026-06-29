package net.rim.device.apps.internal.blackberryemail.email.filters;

class EmailFilterList$1 implements Runnable {
   private final EmailFilterList this$0;

   EmailFilterList$1(EmailFilterList _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.forceExit();
   }
}
