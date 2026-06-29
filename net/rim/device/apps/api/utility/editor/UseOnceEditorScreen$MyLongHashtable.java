package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.util.LongHashtable;

final class UseOnceEditorScreen$MyLongHashtable extends LongHashtable implements MemoryCleanerListener {
   UseOnceEditorScreen$MyLongHashtable() {
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final boolean cleanNow(int event) {
      if (event == 10 && this.size() > 0) {
         this.clear();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
