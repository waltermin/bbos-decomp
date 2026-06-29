package net.rim.device.api.system;

import net.rim.vm.MemStats;

public final class MemoryStats extends MemStats {
   @Override
   public final int getAllocated() {
      return super.getAllocated();
   }

   @Override
   public final int getFree() {
      return super.getFree();
   }

   @Override
   public final int getObjectSize() {
      return super.getObjectSize();
   }

   @Override
   public final int getObjectCount() {
      return super.getObjectCount();
   }
}
