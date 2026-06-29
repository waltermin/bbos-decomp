package net.rim.device.internal.EScreens;

import net.rim.device.api.ui.component.Dialog;

final class EngScreenApp$Whoops implements Runnable {
   private EngScreenApp$Whoops() {
   }

   @Override
   public final void run() {
      Dialog.alert("Unable to startup EScreens");
      System.exit(0);
   }

   EngScreenApp$Whoops(EngScreenApp$1 x0) {
      this();
   }
}
