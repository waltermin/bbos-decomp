package net.rim.blackberry.api.pim;

import net.rim.blackberry.api.mail.PIMAddressCardMailConverter;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void libMain(String[] args) {
      PIMAddressCardMailConverter.register();
   }
}
