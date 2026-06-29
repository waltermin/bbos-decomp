package net.rim.device.apps.internal.memorycleaner;

final class MemoryCleanerApp$StartMemoryCleaner extends Thread {
   @Override
   public final void run() {
      new MemoryCleaner().start(true);
   }
}
