package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.internal.proxy.Proxy;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      Proxy.getInstance().addGlobalEventListener(new SMSVerbFactory());
      VerbCombinerRepository.addCombiner(-4069039132719574797L, new SMSComposeVerbCombiner());
   }
}
