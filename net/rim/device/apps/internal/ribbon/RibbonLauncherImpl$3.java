package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.util.Factory;

final class RibbonLauncherImpl$3 implements Factory {
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$3(RibbonLauncherImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object createInstance(Object parms) {
      if (this.this$0._description == null) {
         this.this$0._description = new RibbonDescriptionField(this.this$0._compressedBanners);
      }

      return this.this$0._description;
   }
}
