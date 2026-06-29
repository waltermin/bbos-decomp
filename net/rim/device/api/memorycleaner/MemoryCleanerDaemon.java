package net.rim.device.api.memorycleaner;

public final class MemoryCleanerDaemon {
   private static MemoryCleanerManager _manager = MemoryCleanerManager.getInstance();

   private MemoryCleanerDaemon() {
   }

   public static final void addListener(MemoryCleanerListener listener) {
      addListener(listener, true);
   }

   public static final void addListener(MemoryCleanerListener listener, boolean enable) {
      _manager.addListener(listener, false, enable);
   }

   public static final void addWeakListener(MemoryCleanerListener listener) {
      addWeakListener(listener, true);
   }

   public static final void addWeakListener(MemoryCleanerListener listener, boolean enable) {
      _manager.addListener(listener, true, enable);
   }

   public static final void removeListener(MemoryCleanerListener listener) {
      _manager.removeListener(listener);
   }

   public static final void cleanAll() {
      _manager.cleanAll();
   }
}
