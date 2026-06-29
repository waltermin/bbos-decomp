package net.rim.device.api.i18n;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntHashtable;

final class ResourceBundleFamily$MyIntHashtable extends IntHashtable implements MemoryCleanerListener {
   private final ResourceBundleFamily this$0;

   ResourceBundleFamily$MyIntHashtable(ResourceBundleFamily _1, int initialCapacity) {
      super(initialCapacity);
      this.this$0 = _1;
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final boolean cleanNow(int event) {
      if (event == 6 && PersistentContent.isEncryptionEnabled()) {
         boolean gc = this.this$0._cache.size() > 0;
         this.this$0._cache.clear();
         return gc;
      } else {
         return false;
      }
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
