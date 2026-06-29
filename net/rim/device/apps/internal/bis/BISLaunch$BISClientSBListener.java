package net.rim.device.apps.internal.bis;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;

final class BISLaunch$BISClientSBListener implements GlobalEventListener {
   private BISLaunch$BISClientSBListener() {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != -4220058463650496006L && guid != 2522898683889177438L && guid != 8288627527798139133L) {
         if (guid == -7464003439710973532L && BISClientInvoke.canBeInvoked()) {
            BISLaunch.removeRibbonIcon();
            BISLaunch.addRibbonIcon();
         }
      } else {
         BISLaunch.processServiceBook();
      }
   }

   BISLaunch$BISClientSBListener(BISLaunch$1 x0) {
      this();
   }
}
