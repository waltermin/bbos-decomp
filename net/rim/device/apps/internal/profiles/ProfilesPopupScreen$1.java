package net.rim.device.apps.internal.profiles;

class ProfilesPopupScreen$1 implements Runnable {
   private final ProfilesPopupScreen this$0;

   ProfilesPopupScreen$1(ProfilesPopupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.close();
      System.exit(0);
   }
}
