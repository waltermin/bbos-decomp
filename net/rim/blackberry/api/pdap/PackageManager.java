package net.rim.blackberry.api.pdap;

import net.rim.blackberry.api.mail.AddressCardMailConverter;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void libMain(String[] args) {
      AddressCardMailConverter.register();
   }
}
