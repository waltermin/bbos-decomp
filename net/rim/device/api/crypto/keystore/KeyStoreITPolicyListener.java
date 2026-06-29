package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.internal.proxy.Proxy;

public final class KeyStoreITPolicyListener implements GlobalEventListener {
   private static final long ID;

   private KeyStoreITPolicyListener() {
      Proxy.getInstance().addGlobalEventListener(this);
   }

   public static final KeyStoreITPolicyListener getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      KeyStoreITPolicyListener listener = (KeyStoreITPolicyListener)registry.getOrWaitFor(2061894209299239981L);
      if (listener == null) {
         listener = new KeyStoreITPolicyListener();
         registry.put(2061894209299239981L, listener);
      }

      return listener;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L) {
         launchITPolicyCheck(false);
      }

      if (guid == -594020114676189989L) {
         launchITPolicyCheck(true);
      }
   }

   public static final void launchITPolicyCheck(boolean promptForPassword) {
      Proxy.getInstance().startThread(new KeyStoreITPolicyListener$KeyStoreITPolicyCheck(promptForPassword));
   }
}
