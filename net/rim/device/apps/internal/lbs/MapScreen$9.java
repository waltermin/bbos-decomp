package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapScreen$9 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$9(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return this.this$0._dashboard.getMode() != null;
   }

   @Override
   public final void run() {
      if (this.this$0._dashboard.getMode().getView() == 0) {
         this.this$0._dashboard.getMode().changeView(1);
         this.this$0._mapField.showHintLabel(LBSResources.getString(338));
      } else {
         this.this$0._dashboard.getMode().changeView(0);
         this.this$0._mapField.showHintLabel(LBSResources.getString(337));
      }

      MapScreen.access$1400(this.this$0);
      if (this.this$0._hideDashboardPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this.this$0._hideDashboardPID);
         this.this$0._hideDashboardPID = -1;
      }
   }

   @Override
   final int getResourceId() {
      return this.this$0._dashboard.getMode().getView() != 0 ? 284 : 285;
   }
}
