package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapScreen$19 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$19(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      String url = Utilities.createLbsUrl(
         this.this$0._mapField.getLatitude(),
         this.this$0._mapField.getLongitude(),
         this.this$0._mapField.getZoom(),
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         ""
      );
      Clipboard.getClipboard().put(url);
      PopupStatus.show(LBSResources.getString(50), 1000);
   }
}
