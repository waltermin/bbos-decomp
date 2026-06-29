package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.RibbonBanner;

final class RibbonLauncherImpl$1 implements Factory {
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$1(RibbonLauncherImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object createInstance(Object parms) {
      if (this.this$0._ribbonAppBanner == null) {
         this.this$0._ribbonAppBanner = RibbonBanner.getInstance().getRibbonBanner();
      }

      return this.this$0._ribbonAppBanner;
   }
}
