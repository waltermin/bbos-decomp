package net.rim.device.apps.internal.idlescreen;

final class IdleScreenApplication$ElevatePriority implements Runnable {
   @Override
   public final void run() {
      IdleScreen screen = IdleScreenApplication._globalData._screen;
      if (screen != null) {
         screen.elevatePriority();
      }
   }
}
