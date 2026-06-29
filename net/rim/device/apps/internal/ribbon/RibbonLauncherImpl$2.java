package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.util.Factory;

final class RibbonLauncherImpl$2 implements Factory {
   private final RibbonLauncherImpl this$0;

   RibbonLauncherImpl$2(RibbonLauncherImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final Object createInstance(Object parms) {
      this.this$0._applicationIconArea = this.this$0._hierarchyManager.getLauncherField((String)parms);
      return this.this$0._applicationIconArea;
   }
}
