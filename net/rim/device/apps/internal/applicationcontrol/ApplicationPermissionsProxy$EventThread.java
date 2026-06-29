package net.rim.device.apps.internal.applicationcontrol;

final class ApplicationPermissionsProxy$EventThread extends Thread {
   private final ApplicationPermissionsProxy this$0;

   private ApplicationPermissionsProxy$EventThread(ApplicationPermissionsProxy _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.enterEventDispatcher();
   }

   ApplicationPermissionsProxy$EventThread(ApplicationPermissionsProxy x0, ApplicationPermissionsProxy$1 x1) {
      this(x0);
   }
}
