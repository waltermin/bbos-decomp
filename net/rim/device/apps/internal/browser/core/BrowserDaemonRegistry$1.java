package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.download.DescriptorField;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;

class BrowserDaemonRegistry$1 implements Runnable {
   private final boolean val$validConfig;

   BrowserDaemonRegistry$1(boolean _1) {
      this.val$validConfig = _1;
   }

   @Override
   public void run() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      BrowserDaemonRegistry registry = (BrowserDaemonRegistry)ar.waitFor(4307171400805038204L);
      synchronized (registry._configListeners) {
         for (int i = registry._configListeners.length - 1; i >= 0; i--) {
            Object obj = registry._configListeners[i].get();
            if (obj == null) {
               Arrays.removeAt(registry._configListeners, i);
            } else {
               BrowserConfigChangeListener listener = (DescriptorField)obj;
               if (this.val$validConfig) {
                  listener.browserConfigChanged();
               } else {
                  listener.browserConfigInvalid();
               }
            }
         }
      }
   }
}
