package net.rim.device.apps.internal.commonmodels;

import net.rim.device.apps.internal.commonmodels.body.PackageManager;

public class Register {
   public static void libMain(String[] args) {
      PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.commonmodels.title.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.commonmodels.opaque.PackageManager.registerOnceOnSystemStart();
      net.rim.device.apps.internal.commonmodels.categories.PackageManager.registerOnceOnSystemStart();
   }
}
