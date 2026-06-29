package net.rim.device.internal.EScreens;

import net.rim.device.api.system.MemoryStats;
import net.rim.vm.Memory;

class EScreenVMStats$4 implements EScreenMemStatsRefresher {
   private final EScreenVMStats this$0;

   EScreenVMStats$4(EScreenVMStats _1) {
      this.this$0 = _1;
   }

   @Override
   public void refresh(MemoryStats stats) {
      Memory.getCodeStats(stats);
   }
}
