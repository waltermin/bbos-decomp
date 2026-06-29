package net.rim.device.internal.synchronization.ota.session;

class SessionManager$IgnoredSessionCleanup implements Runnable {
   private final SessionManager this$0;

   SessionManager$IgnoredSessionCleanup(SessionManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.checkForTimeoutsForIgnoredSessions();
      this.this$0._ignoredSessionCleanup = null;
   }
}
