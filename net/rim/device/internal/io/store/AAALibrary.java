package net.rim.device.internal.io.store;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.proxy.Proxy;

public final class AAALibrary {
   private static final long GUID = 6785790243489840050L;
   static String FONT_DIR = "/system/fonts/";
   static String STORE_FONT_DIR = "/store/system/fonts/";

   AAALibrary() {
   }

   public static final void register() {
      ContentStoreImpl.getInstance();
      initFonts();
      ContentStoreConnection.register();
      ContentStoreDatabase.getInstance().getFileTable().sanitize();
   }

   public static final void optionsChanged() {
      ContentStoreDatabase.getInstance().getFileTable().optionsChanged();
   }

   private static final void initFonts() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      AAALibrary$FontListener listener = (AAALibrary$FontListener)ar.getOrWaitFor(6785790243489840050L);
      if (listener == null) {
         listener = new AAALibrary$FontListener();
         Proxy.getInstance().addFileSystemJournalListener(listener);
         listener.reload();
         ar.put(6785790243489840050L, listener);
      }
   }
}
