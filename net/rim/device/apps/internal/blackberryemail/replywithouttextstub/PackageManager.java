package net.rim.device.apps.internal.blackberryemail.replywithouttextstub;

import net.rim.device.api.system.ApplicationRegistry;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      ApplicationRegistry.getApplicationRegistry().put(7954277133629574293L, new ReplyWithoutTextStubModelFactory());
   }
}
