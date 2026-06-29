package net.rim.device.apps.internal.blackberryemail.unknown;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      ApplicationRegistry.getApplicationRegistry().put(-1388261346431947322L, new UnknownMimePartModelFactory());
      RecipientCache.getInstance();
   }
}
