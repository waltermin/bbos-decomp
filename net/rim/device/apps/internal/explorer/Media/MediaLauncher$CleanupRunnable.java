package net.rim.device.apps.internal.explorer.Media;

final class MediaLauncher$CleanupRunnable implements Runnable {
   @Override
   public final void run() {
      MediaLauncher.stop();
   }
}
