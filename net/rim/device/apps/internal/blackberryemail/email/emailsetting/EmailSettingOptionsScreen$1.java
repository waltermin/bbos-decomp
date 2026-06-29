package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

class EmailSettingOptionsScreen$1 implements Runnable {
   private final EmailSettingOptionsScreen this$0;

   EmailSettingOptionsScreen$1(EmailSettingOptionsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.forceExit();
   }
}
