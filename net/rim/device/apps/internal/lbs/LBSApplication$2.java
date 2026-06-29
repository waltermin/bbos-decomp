package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LBSApplication$2 implements Runnable {
   private final String val$url;

   LBSApplication$2(String val$url) {
      this.val$url = val$url;
   }

   @Override
   public final void run() {
      if (!ITPolicy.getBoolean(2, true)) {
         Dialog.alert(LBSResources.getString(146));
      } else {
         Dialog.inform(MessageFormat.format(LBSResources.getString(147), new Object[]{this.val$url}));
         MapScreen.saveMapApp();
         System.exit(0);
      }
   }
}
