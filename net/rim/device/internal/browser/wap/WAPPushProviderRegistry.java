package net.rim.device.internal.browser.wap;

import net.rim.device.api.system.ApplicationRegistry;

public final class WAPPushProviderRegistry {
   private IWAPPushProvider _instance;
   private static final long APP_REGISTRY_KEY;

   private WAPPushProviderRegistry(IWAPPushProvider instance) {
      this._instance = instance;
   }

   public static final IWAPPushProvider getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return ((WAPPushProviderRegistry)ar.waitFor(-4435977049555571020L))._instance;
   }

   public static final IWAPPushProvider getInstanceNoWait() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      WAPPushProviderRegistry reg = (WAPPushProviderRegistry)ar.get(-4435977049555571020L);
      return reg == null ? null : reg._instance;
   }

   public static final void setInstance(IWAPPushProvider instance) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      WAPPushProviderRegistry reg = (WAPPushProviderRegistry)ar.getOrWaitFor(-4435977049555571020L);
      if (reg == null) {
         reg = new WAPPushProviderRegistry(instance);
         ar.put(-4435977049555571020L, reg);
      }

      reg._instance = instance;
   }
}
