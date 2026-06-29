package net.rim.device.api.memorycleaner;

import net.rim.device.api.system.Clipboard;
import net.rim.device.internal.i18n.CommonResource;

final class MemoryCleanerManager$ClipboardMemoryCleaner implements MemoryCleanerListener {
   @Override
   public final boolean cleanNow(int event) {
      if (event != 7) {
         Clipboard clipboard = Clipboard.getClipboard();
         if (clipboard.get() != null) {
            clipboard.put(null);
            return true;
         }
      }

      return false;
   }

   @Override
   public final String getDescription() {
      return CommonResource.getString(10093);
   }
}
