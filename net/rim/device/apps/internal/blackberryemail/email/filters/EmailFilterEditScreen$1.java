package net.rim.device.apps.internal.blackberryemail.email.filters;

class EmailFilterEditScreen$1 implements Runnable {
   private final EmailFilterEditScreen this$0;

   EmailFilterEditScreen$1(EmailFilterEditScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.forceExit();
   }
}
