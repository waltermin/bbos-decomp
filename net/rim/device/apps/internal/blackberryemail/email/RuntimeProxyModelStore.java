package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;

public final class RuntimeProxyModelStore extends AbstractProxyModelStore {
   public static final RuntimeProxyModelStore getInstance(long rootId) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         RuntimeProxyModelStore rpms = (RuntimeProxyModelStore)ar.get(rootId);
         if (rpms == null) {
            rpms = new RuntimeProxyModelStore(rootId);
            ar.put(rootId, rpms);
         }

         return rpms;
      }
   }

   private RuntimeProxyModelStore(long root) {
      super(root);
   }
}
