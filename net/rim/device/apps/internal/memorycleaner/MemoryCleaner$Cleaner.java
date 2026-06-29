package net.rim.device.apps.internal.memorycleaner;

import net.rim.vm.Memory;

final class MemoryCleaner$Cleaner extends Thread {
   private final MemoryCleaner this$0;

   public MemoryCleaner$Cleaner(MemoryCleaner _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      for (int i = 0; i < this.this$0._listeners.length; i++) {
         label36:
         try {
            this.this$0._listeners[i].cleanNow(4);
         } finally {
            break label36;
         }

         this.this$0._dialog.setCompletedOperation(i);
      }

      Memory.fullGC();
      this.this$0._dialog.GCComplete();
      if (this.this$0._runFromRibbon) {
         System.exit(0);
      }
   }
}
