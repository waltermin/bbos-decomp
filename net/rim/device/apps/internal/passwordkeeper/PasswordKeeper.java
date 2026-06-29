package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationRegistry;

public final class PasswordKeeper {
   private PasswordKeeperSync _collection = new PasswordKeeperSync();
   private static final long ID;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(
      7414812286470404095L, "net.rim.device.apps.internal.passwordkeeper.resource.PasswordKeeper"
   );

   private PasswordKeeper() {
   }

   public static final PasswordKeeper getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      PasswordKeeper secure = (PasswordKeeper)registry.get(2846117606586001482L);
      if (secure == null) {
         secure = new PasswordKeeper();
         registry.put(2846117606586001482L, secure);
      }

      return secure;
   }

   final PasswordKeeperSync getCollection() {
      return this._collection;
   }

   public static final ResourceBundleFamily getBundle() {
      return _rb;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }
}
