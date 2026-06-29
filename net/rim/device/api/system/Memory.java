package net.rim.device.api.system;

public final class Memory {
   private Memory() {
   }

   public static final MemoryStats getRAMStats() {
      MemoryStats ram = new MemoryStats();
      net.rim.vm.Memory.getRAMStats(ram);
      return ram;
   }

   public static final MemoryStats getFlashStats() {
      MemoryStats flash = new MemoryStats();
      net.rim.vm.Memory.getFlashStats(flash);
      return flash;
   }

   public static final MemoryStats getTransientStats() {
      MemoryStats flash = new MemoryStats();
      net.rim.vm.Memory.getTransientStats(flash);
      return flash;
   }

   public static final MemoryStats getPersistentStats() {
      MemoryStats flash = new MemoryStats();
      net.rim.vm.Memory.getPersistentStats(flash);
      return flash;
   }

   public static final MemoryStats getObjectStats() {
      MemoryStats objects = new MemoryStats();
      net.rim.vm.Memory.getObjectStats(objects);
      return objects;
   }

   public static final MemoryStats getCodeStats() {
      MemoryStats code = new MemoryStats();
      net.rim.vm.Memory.getCodeStats(code);
      return code;
   }

   public static final int getMemoryNeeded() {
      return net.rim.vm.Memory.getFlashNeeded(false);
   }

   public static final int getFlashFree() {
      return net.rim.vm.Memory.getFlashFree();
   }

   public static final int getFlashTotal() {
      return net.rim.vm.Memory.getFlashTotal();
   }
}
