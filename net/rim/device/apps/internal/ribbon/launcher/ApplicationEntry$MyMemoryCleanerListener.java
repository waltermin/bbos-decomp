package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.api.memorycleaner.MemoryCleanerListener;

final class ApplicationEntry$MyMemoryCleanerListener implements MemoryCleanerListener {
   private final ApplicationEntry this$0;

   private ApplicationEntry$MyMemoryCleanerListener(ApplicationEntry _1) {
      this.this$0 = _1;
   }

   @Override
   public final boolean cleanNow(int event) {
      return this.this$0.cleanDescription(event);
   }

   @Override
   public final String getDescription() {
      return null;
   }

   ApplicationEntry$MyMemoryCleanerListener(ApplicationEntry x0, ApplicationEntry$1 x1) {
      this(x0);
   }
}
