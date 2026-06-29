package net.rim.device.api.lowmemory;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.Memory;

public class LowMemoryManager {
   private static LowMemoryManager _instance;
   public static final long GUID_INSTANCE = 7979320271643693911L;
   public static final long GUID_FLASH_LOW = 945659952435832745L;

   protected LowMemoryManager() {
   }

   private static LowMemoryManager getInstance() {
      if (_instance == null) {
         _instance = (LowMemoryManager)ApplicationRegistry.getApplicationRegistry().waitFor(7979320271643693911L);
      }

      return _instance;
   }

   public static void addLowMemoryFailedListener(LowMemoryFailedListener listener) {
      getInstance().doAddLowMemoryFailedListener(listener);
   }

   public static void addLowMemoryListener(LowMemoryListener listener) {
      getInstance().doAddLowMemoryListener(listener);
   }

   public static void removeLowMemoryListener(LowMemoryListener listener) {
      getInstance().doRemoveLowMemoryListener(listener);
   }

   public static void removeLowMemoryFailedListener(LowMemoryFailedListener listener) {
      getInstance().doRemoveLowMemoryFailedListener(listener);
   }

   public static void poll() {
      getInstance().doPoll(true);
   }

   public static void markAsRecoverable(Object o) {
      Memory.markAsRecoverable(o);
   }

   protected void doAddLowMemoryListener(LowMemoryListener _1) {
      throw null;
   }

   protected void doRemoveLowMemoryListener(LowMemoryListener _1) {
      throw null;
   }

   protected void doAddLowMemoryFailedListener(LowMemoryFailedListener _1) {
      throw null;
   }

   protected void doRemoveLowMemoryFailedListener(LowMemoryFailedListener _1) {
      throw null;
   }

   protected void doPoll(boolean _1) {
      throw null;
   }
}
